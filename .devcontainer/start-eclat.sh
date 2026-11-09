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

if [[ -f "$PID_FILE" ]]; then
  OLD_PID="$(cat "$PID_FILE" 2>/dev/null || true)"
  if [[ "$OLD_PID" =~ ^[0-9]+$ ]] && kill -0 "$OLD_PID" 2>/dev/null; then
    echo "Éclat may already be running (PID $OLD_PID). Checking health..."
    if curl --fail --silent --show-error "http://127.0.0.1:$PORT/login" >/dev/null 2>&1; then
      echo "Éclat is already responding at port $PORT."
      exit 0
    fi
    echo "Recorded process is not healthy; cleaning stale PID file."
  fi
  rm -f "$PID_FILE"
fi

RUNNING_PIDS="$(port_pids)"
if [[ -n "$RUNNING_PIDS" ]]; then
  echo "Port $PORT is already occupied by PID(s): $RUNNING_PIDS"
  echo "Not killing unknown processes. Inspect with: lsof -iTCP:$PORT -sTCP:LISTEN"
  exit 1
fi

echo "Starting Éclat in Codespaces on port $PORT..."
echo "Log: $LOG_FILE"
nohup ./mvnw --batch-mode --no-transfer-progress spring-boot:run \
  -Dspring-boot.run.arguments="--server.port=$PORT --eclat.sms.provider=console" \
  >"$LOG_FILE" 2>&1 < /dev/null &
APP_PID=$!
echo "$APP_PID" > "$PID_FILE"

for attempt in $(seq 1 90); do
  if ! kill -0 "$APP_PID" 2>/dev/null; then
    echo "Éclat process stopped during startup. Last 100 log lines:"
    tail -n 100 "$LOG_FILE" || true
    rm -f "$PID_FILE"
    exit 1
  fi
  if curl --fail --silent "http://127.0.0.1:$PORT/login" >/dev/null 2>&1; then
    echo "Éclat is ready."
    echo "Codespaces forwards port $PORT. Open the forwarded URL from the PORTS panel."
    echo "PID: $APP_PID"
    exit 0
  fi
  sleep 2
done

echo "Éclat did not become ready within 180 seconds. Last 100 log lines:"
tail -n 100 "$LOG_FILE" || true
exit 1
