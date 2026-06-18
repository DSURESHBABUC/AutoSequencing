#!/bin/zsh
# ╔══════════════════════════════════════════════════════════════════════════╗
# ║   TVS Playwright — EOD Test Execution & PDF Report                      ║
# ║                                                                          ║
# ║   Runs the full regression suite and generates a PDF report.            ║
# ║   Schedule this script via cron or launchd for daily EOD execution.     ║
# ║                                                                          ║
# ║   Usage:                                                                 ║
# ║     ./scripts/run-eod-report.sh                                         ║
# ║     ./scripts/run-eod-report.sh --suite testng-eyeq.xml                ║
# ║                                                                          ║
# ║   Cron Example (6:00 PM IST daily):                                     ║
# ║     0 18 * * 1-5 cd /Users/d.sureshbabu/AutoSequencing/playwright-framework && ./scripts/run-eod-report.sh ║
# ╚══════════════════════════════════════════════════════════════════════════╝

set -e

# ── Configuration ──────────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
SUITE_FILE="${1:-testng.xml}"
DATE_STR=$(date +"%Y-%m-%d")
LOG_FILE="$PROJECT_DIR/reports/eod-run-${DATE_STR}.log"

cd "$PROJECT_DIR"

echo "═══════════════════════════════════════════════════════════════════════"
echo "  TVS Playwright — EOD Test Execution"
echo "  Date: $(date '+%d-%b-%Y %H:%M:%S')"
echo "  Suite: $SUITE_FILE"
echo "  Project: $PROJECT_DIR"
echo "═══════════════════════════════════════════════════════════════════════"

# ── Ensure reports directory exists ────────────────────────────────────────
mkdir -p reports

# ── Run Maven tests (generates HTML + PDF reports via listeners) ───────────
echo ""
echo "🚀 Starting test execution..."
echo ""

mvn test \
    -DsuiteXmlFile="$SUITE_FILE" \
    -Dheadless=true \
    -Dbrowser=chromium \
    2>&1 | tee "$LOG_FILE"

EXIT_CODE=${pipestatus[1]}

# ── Summary ────────────────────────────────────────────────────────────────
echo ""
echo "═══════════════════════════════════════════════════════════════════════"
if [ $EXIT_CODE -eq 0 ]; then
    echo "  ✅ TEST EXECUTION COMPLETE — ALL PASSED"
else
    echo "  ❌ TEST EXECUTION COMPLETE — SOME TESTS FAILED"
fi
echo "  Reports generated:"
echo "    📄 PDF:  reports/TestExecutionReport_${DATE_STR}.pdf"
echo "    🌐 HTML: reports/PlaywrightTestReport.html"
echo "    📋 Log:  $LOG_FILE"
echo "═══════════════════════════════════════════════════════════════════════"

# ── Return the PDF path for downstream use ─────────────────────────────────
PDF_PATH="$PROJECT_DIR/reports/TestExecutionReport_${DATE_STR}.pdf"
if [ -f "$PDF_PATH" ]; then
    echo ""
    echo "📄 PDF Report: $PDF_PATH"
    # Open PDF on macOS (optional — comment out for CI)
    # open "$PDF_PATH"
else
    echo "⚠️  PDF not found at expected path: $PDF_PATH"
fi

exit $EXIT_CODE
