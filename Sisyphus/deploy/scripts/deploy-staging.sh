#!/usr/bin/env bash
set -euo pipefail

ENVIRONMENT="staging"
DEPLOY_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

export $(grep -v '^#' "$DEPLOY_DIR/.env.staging" | xargs)

cd "$DEPLOY_DIR"

docker compose -f docker-compose.staging.yml down

docker compose -f docker-compose.staging.yml build

docker compose -f docker-compose.staging.yml up -d

for i in $(seq 1 30); do
  if docker compose -f docker-compose.staging.yml exec -T postgres pg_isready -U "$DB_USER" -d "${POSTGRES_DB:-nutrust}" > /dev/null 2>&1; then
    break
  fi
  sleep 2
done

docker compose -f docker-compose.staging.yml exec -T postgres pg_isready -U "$DB_USER" -d "${POSTGRES_DB:-nutrust}"

docker compose -f docker-compose.staging.yml exec -T nutrust-backend flyway migrate || true

docker compose -f docker-compose.staging.yml ps
