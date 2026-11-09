#!/usr/bin/env bash

# Wedding System - Automatic Test & Error Report
set -u

REPORT_DIR="test-reports/$(date +%Y%m%d-%H%M%S)"
mkdir -p "$REPORT_DIR"

SUMMARY="$REPORT_DIR/summary.txt"
BUILD_LOG="$REPORT_DIR/build.log"
APP_LOG="$REPORT_DIR/application.log"
HTTP_LOG="$REPORT_DIR/http-check.log"

PASS=0
FAIL=0
WARN=0

echo "WEDDING SYSTEM AUTOMATED TEST REPORT" | tee "$SUMMARY"
echo "Date: $(date)" | tee -a "$SUMMARY"
echo "Branch: $(git branch --show-current)" | tee -a "$SUMMARY"
echo "-----------------------------------" | tee -a "$SUMMARY"

check_result() {
    if [ "$1" -eq 0 ]; then
        echo "[PASS] $2" | tee -a "$SUMMARY"
        PASS=$((PASS + 1))
    else
        echo "[FAIL] $2" | tee -a "$SUMMARY"
        FAIL=$((FAIL + 1))
    fi
}

echo
echo "1. Running Maven clean verify..."
./mvnw clean verify > "$BUILD_LOG" 2>&1
BUILD_STATUS=$?
check_result "$BUILD_STATUS" "Maven build and automated tests"

if [ "$BUILD_STATUS" -ne 0 ]; then
    echo "Build failed. See build.log" | tee -a "$SUMMARY"
else
    echo
    echo "2. Starting application..."

    if curl -s --max-time 2 http://localhost:8080/ >/dev/null; then
        echo "[WARN] Port 8080 already has a running service." | tee -a "$SUMMARY"
        WARN=$((WARN + 1))
    else
        ./mvnw spring-boot:run > "$APP_LOG" 2>&1 &
        APP_PID=$!

        trap 'kill "$APP_PID" 2>/dev/null || true' EXIT

        READY=0
        for i in $(seq 1 60); do
            if curl -s --max-time 2 http://localhost:8080/ >/dev/null; then
                READY=1
                break
            fi
            if ! kill -0 "$APP_PID" 2>/dev/null; then
                break
            fi
            sleep 2
        done

        check_result "$READY" "Application startup on port 8080"

        if [ "$READY" -eq 1 ]; then
            echo "3. Checking HTTP response..."

            CODE=$(curl -s -o /dev/null -w "%{http_code}" \
                --max-time 10 http://localhost:8080/ || echo "000")

            echo "Homepage HTTP status: $CODE" | tee "$HTTP_LOG"

            if [[ "$CODE" =~ ^[23][0-9][0-9]$ ]]; then
                check_result 0 "Homepage HTTP response"
            else
                check_result 1 "Homepage HTTP response"
            fi
        else
            echo "Application did not become ready." >> "$HTTP_LOG"
        fi

        kill "$APP_PID" 2>/dev/null || true
        wait "$APP_PID" 2>/dev/null || true
        trap - EXIT
    fi
fi

echo
echo "-----------------------------------" | tee -a "$SUMMARY"
echo "PASSED: $PASS" | tee -a "$SUMMARY"
echo "FAILED: $FAIL" | tee -a "$SUMMARY"
echo "WARNINGS: $WARN" | tee -a "$SUMMARY"
echo "-----------------------------------" | tee -a "$SUMMARY"

echo
echo "Important error lines:" > "$REPORT_DIR/errors.txt"

for FILE in "$BUILD_LOG" "$APP_LOG"; do
    if [ -f "$FILE" ]; then
        grep -inE "ERROR|FAILURE|Exception|Caused by:|BUILD FAILURE" \
            "$FILE" >> "$REPORT_DIR/errors.txt" || true
    fi
done

echo "Report saved in: $REPORT_DIR"
echo "Check summary.txt and errors.txt"

zip -qr "${REPORT_DIR}.zip" "$REPORT_DIR"
echo "ZIP report: ${REPORT_DIR}.zip"

if [ "$FAIL" -gt 0 ]; then
    exit 1
fi
