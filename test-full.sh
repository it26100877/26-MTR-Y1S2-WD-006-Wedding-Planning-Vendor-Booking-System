#!/usr/bin/env bash

set -uo pipefail

ROOT="$(pwd)"
TIME="$(date +%Y%m%d-%H%M%S)"
REPORT_DIR="$ROOT/test-reports/full-test-$TIME"
mkdir -p "$REPORT_DIR"

LOG="$REPORT_DIR/full-test.log"
FAIL=0

exec > >(tee "$LOG") 2>&1

echo "=================================================="
echo " WEDDING PLANNING SYSTEM - FULL PROJECT TEST"
echo " Date: $TIME"
echo "=================================================="

# 1. Check project structure
echo
echo "[1/6] CHECKING PROJECT STRUCTURE"

for folder in \
  src/main/java \
  src/main/webapp \
  src/test/java \
  src/main/resources
do
  if [ -d "$folder" ]; then
    echo "PASS: $folder exists"
  else
    echo "FAIL: $folder missing"
    FAIL=1
  fi
done

# 2. Check member modules
echo
echo "[2/6] CHECKING MEMBER MODULES"

MODULE_COUNT=0

for folder in src/main/java/com/wedding/member*; do
  if [ -d "$folder" ]; then
    MODULE_COUNT=$((MODULE_COUNT + 1))
    echo "FOUND: $folder"
  fi
done

echo "Member module folders found: $MODULE_COUNT"

if [ "$MODULE_COUNT" -lt 6 ]; then
  echo "WARNING: Fewer than 6 member module folders found."
  echo "Please review module structure."
  FAIL=1
else
  echo "PASS: Six or more member module folders found."
fi

# 3. Count source, JSP and test files
echo
echo "[3/6] COUNTING PROJECT FILES"

JAVA_COUNT=$(find src/main/java -type f -name "*.java" | wc -l)
TEST_COUNT=$(find src/test/java -type f \( -name "*Test.java" -o -name "*Tests.java" \) | wc -l)
JSP_COUNT=$(find src/main/webapp -type f -name "*.jsp" | wc -l)

echo "Java source files: $JAVA_COUNT"
echo "Test source files: $TEST_COUNT"
echo "JSP pages: $JSP_COUNT"

# 4. Run complete Maven verification
echo
echo "[4/6] RUNNING MAVEN VERIFY"
echo "This may take some time..."

if ./mvnw clean verify; then
  echo "PASS: Maven verify completed."
else
  echo "FAIL: Maven verify failed."
  FAIL=1
fi

# 5. Summarize automated test results
echo
echo "[5/6] AUTOMATED TEST SUMMARY"

python3 - <<'PY'
import glob
import xml.etree.ElementTree as ET

files = glob.glob("target/surefire-reports/TEST-*.xml")
totals = {"tests": 0, "failures": 0, "errors": 0, "skipped": 0}

for file in files:
    try:
        root = ET.parse(file).getroot()
        for key in totals:
            totals[key] += int(root.attrib.get(key, 0))
    except Exception as e:
        print("Could not read:", file, e)

print("Test files:", len(files))
for key, value in totals.items():
    print(f"{key.capitalize()}: {value}")

if not files:
    print("WARNING: No Surefire XML reports found.")
elif totals["failures"] or totals["errors"]:
    print("RESULT: TEST FAILURES FOUND")
elif totals["tests"] == 0:
    print("RESULT: NO TESTS EXECUTED")
else:
    print("RESULT: ALL EXECUTED TESTS PASSED")
PY

# 6. Check running application
echo
echo "[6/6] APPLICATION HTTP CHECK"

HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
  --max-time 10 http://localhost:8080/ || true)

echo "HTTP status: $HTTP_CODE"

case "$HTTP_CODE" in
  200|301|302|303|307|308)
    echo "PASS: Application responds on port 8080."
    ;;
  *)
    echo "WARNING: Application is not responding normally."
    echo "Start it with: ./mvnw spring-boot:run"
    ;;
esac

# Final result
echo
echo "=================================================="

if [ "$FAIL" -eq 0 ]; then
  echo "STRUCTURE + MAVEN CHECKS: PASSED"
else
  echo "SOME CHECKS NEED ATTENTION"
fi

echo "Report saved at: $REPORT_DIR"
echo "Full log: $LOG"
echo "=================================================="
