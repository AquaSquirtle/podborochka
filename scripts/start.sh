#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
if [ ! -f .env ]; then
  echo 'Create .env from .env.example and fill in the service credentials.' >&2
  exit 1
fi
APP_VERSION=$(cat VERSION)
export APP_VERSION
docker compose up -d --build --wait "$@"
echo 'Application is ready. Default URL: http://localhost:8082'
