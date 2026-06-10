#!/usr/bin/env bash
set -euo pipefail

ENVIRONMENT="production"
DEPLOY_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PREVIOUS_TAG_FILE="$DEPLOY_DIR/.previous-tag"

export $(grep -v '^#' "$DEPLOY_DIR/.env.production" | xargs)

cd "$DEPLOY_DIR"

CURRENT_TAG=$(docker inspect --format='{{index .Config.Image}}' "$(docker compose -f docker-compose.production.yml ps -q nutrust-backend 2>/dev/null | head -1)" 2>/dev/null | awk -F: '{print $NF}' || echo "")

if [ -n "$CURRENT_TAG" ] && [ -f "$PREVIOUS_TAG_FILE" ]; then
  CURRENT_PREVIOUS=$(cat "$PREVIOUS_TAG_FILE")
  echo "$CURRENT_PREVIOUS" > "$PREVIOUS_TAG_FILE.bak" 2>/dev/null || true
fi

echo "$CURRENT_TAG" > "$PREVIOUS_TAG_FILE"

PREVIOUS_TAG=$(cat "$PREVIOUS_TAG_FILE" 2>/dev/null | tail -1 || echo "")

if [ -z "$PREVIOUS_TAG" ] || [ "$PREVIOUS_TAG" = "" ]; then
  echo "No previous image tag found. Cannot rollback."
  exit 1
fi

docker compose -f docker-compose.production.yml down

docker compose -f docker-compose.production.yml up -d

docker compose -f docker-compose.production.yml ps
