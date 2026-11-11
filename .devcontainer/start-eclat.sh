#!/usr/bin/env bash
set -Eeuo pipefail

APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$APP_DIR"

PORT="${SERVER_PORT:-8080}"
LOG_DIR="$APP_DIR/logs"
PID_FILE="$APP_DIR/.eclat-app.pid"
LOG_FILE="$LOG_DIR/codespaces-app.log"
mkdir -p "$LOG_DIR"
chmod +x ./mvnw

port_pids() {
  if command -v lsof >/dev/null 2>&1; then
    lsof -tiTCP:"$PORT" -sTCP:LISTEN 2>/dev/null || true
  elif command -v fuser >/dev/null 2>&1; then
    fuser -n tcp "$PORT" 2>/dev/null | tr ' ' '\n' || true
  fi
}

health_check() {
  curl --fail --silent "http://127.0.0.1:$PORT/login" >/dev/null 2>&1
}

print_forwarded_url_hint() {
  if [[ -n "${CODESPACE_NAME:-}" && -n "${GITHUB_CODESPACES_PORT_FORWARDING_DOMAIN:-}" ]]; then
    echo "Forwarded application URL:"
    echo "https://${CODESPACE_NAME}-${PORT}.${GITHUB_CODESPACES_PORT_FORWARDING_DOMAIN}/"
    echo "Open this URL, not localhost:$PORT."
  else
    echo "Open the PORTS panel and click the forwarded address for port $PORT."
    echo "Do not open localhost:$PORT from your computer browser."
  fi
}

if health_check; then
  echo "Éclat is already responding on port $PORT."
  print_forwarded_url_hint
  exit 0
fi

RUNNING_PIDS="$(port_pids)"
if [[ -n "$RUNNING_PIDS" ]]; then
  echo "Port $PORT is occupied by process(es): $RUNNING_PIDS, but /login is not responding."
  echo "Not killing unknown processes. Recent app log:"
  tail -n 80 "$LOG_FILE" 2>/dev/null || true
  echo "Inspect the port with: lsof -iTCP:$PORT -sTCP:LISTEN"
  exit 1
fi

if [[ -f "$PID_FILE" ]]; then
  rm -f "$PID_FILE"
fi

echo "Starting Éclat in Codespaces on port $PORT..."
echo "Log: $LOG_FILE"
nohup ./mvnw --batch-mode --no-transfer-progress spring-boot:run \
  -Dspring-boot.run.arguments="--server.port=$PORT --eclat.sms.provider=console" \
  >"$LOG_FILE" 2>&1 < /dev/null &
APP_PID=$!
echo "$APP_PID" > "$PID_FILE"

for attempt in $(seq 1 90); do
  if health_check; then
    echo "Éclat is ready."
    echo "Startup PID: $APP_PID"
    print_forwarded_url_hint
    exit 0
  fi
  if ! kill -0 "$APP_PID" 2>/dev/null; then
    echo "Éclat stopped during startup. Recent log:"
    tail -n 100 "$LOG_FILE" || true
    rm -f "$PID_FILE"
    exit 1
  fi
  sleep 2
done

echo "Éclat did not become ready within 180 seconds. Recent log:"
tail -n 100 "$LOG_FILE" || true
exit 1
