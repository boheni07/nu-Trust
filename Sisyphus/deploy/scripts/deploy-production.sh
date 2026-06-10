#!/usr/bin/env bash
set -euo pipefail

ENVIRONMENT="production"
DEPLOY_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

export $(grep -v '^#' "$DEPLOY_DIR/.env.production" | xargs)

cd "$DEPLOY_DIR"

echo "Starting production deployment..."

docker compose -f docker-compose.production.yml down

docker compose -f docker-compose.production.yml build

docker compose -f docker-compose.production.yml up -d

for i in $(seq 1 60); do
  if docker compose -f docker-compose.production.yml exec -T postgres pg_isready -U "$DB_USER" -d "${POSTGRES_DB:-nutrust}" > /dev/null 2>&1; then
    break
  fi
  sleep 2
done

docker compose -f docker-compose.production.yml exec -T postgres pg_isready -U "$DB_USER" -d "${POSTGRES_DB:-nutrust}"

docker compose -f docker-compose.production.yml exec -T nutrust-backend flyway migrate || true

docker compose -f docker-compose.production.yml ps

echo "Production deployment completed."
