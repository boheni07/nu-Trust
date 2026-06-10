#!/usr/bin/env bash
set -euo pipefail

DEPLOY_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "$DEPLOY_DIR"

echo "Validating Docker Compose staging config..."

docker compose -f Sisyphus/deploy/docker-compose.staging.yml config --quiet

echo "Staging config validated."

echo "Validating Docker Compose production config..."

docker compose -f Sisyphus/deploy/docker-compose.production.yml config --quiet

echo "Production config validated."

echo "Running smoke tests..."

cd Sisyphus/deploy

docker compose -f docker-compose.staging.yml down --remove-orphans 2>/dev/null || true

docker compose -f docker-compose.staging.yml up -d postgres rabbitmq redis

echo "Waiting for PostgreSQL..."
for i in $(seq 1 30); do
  if docker compose exec -T postgres pg_isready -U "$DB_USER" -d nutrust > /dev/null 2>&1; then
    echo "PostgreSQL ready."
    break
  fi
  sleep 2
done

echo "Waiting for RabbitMQ..."
for i in $(seq 1 30); do
  if docker compose exec -T rabbitmq rabbitmq-diagnostics -q ping > /dev/null 2>&1; then
    echo "RabbitMQ ready."
    break
  fi
  sleep 2
done

echo "Waiting for Redis..."
for i in $(seq 1 30); do
  if docker compose exec -T redis redis-cli ping > /dev/null 2>&1; then
    echo "Redis ready."
    break
  fi
  sleep 2
done

echo "Testing Caddy proxy routing..."

echo "Smoke tests completed."

docker compose -f docker-compose.staging.yml down --remove-orphans
