#!/usr/bin/env sh
set -eu

cd "$(dirname "$0")/.."
version=${1:-0.2.0}
mode=${2:-}
registry_address=${REGISTRY_ADDRESS:-localhost:5050}

case "$version" in
  ''|*[!a-zA-Z0-9_.-]*|[-.]* ) echo 'Invalid image version' >&2; exit 1 ;;
esac
case "$mode" in
  ''|--no-build) ;;
  *) echo 'Usage: scripts/publish-images.sh [version] [--no-build]' >&2; exit 1 ;;
esac

export APP_VERSION="$version"
if [ -n "${PLATFORMS:-}" ]; then
  if [ "$mode" = --no-build ]; then
    echo 'PLATFORMS cannot be combined with --no-build' >&2
    exit 1
  fi
  docker buildx build --platform "$PLATFORMS" --tag "$registry_address/podborochka-backend:$version" --push .
  docker buildx build --platform "$PLATFORMS" --tag "$registry_address/podborochka-frontend:$version" --push frontend
  printf '%s\n' 'FROM postgres:16' | docker buildx build --platform "$PLATFORMS" --tag "$registry_address/postgres:16" --push -
else
  if [ "$mode" != --no-build ]; then
    docker compose build backend frontend
  fi
  for service in backend frontend; do
    docker image tag "podborochka-$service:$version" "$registry_address/podborochka-$service:$version"
    docker image push "$registry_address/podborochka-$service:$version"
  done
  docker image inspect postgres:16 >/dev/null 2>&1 || docker image pull postgres:16
  docker image tag postgres:16 "$registry_address/postgres:16"
  docker image push "$registry_address/postgres:16"
fi
echo "Published application $version and PostgreSQL to $registry_address"
