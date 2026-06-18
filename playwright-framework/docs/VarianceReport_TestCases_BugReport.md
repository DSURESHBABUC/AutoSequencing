# Variance Report Module — Test Cases & Bug Report

**Application:** https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest/groupmaster  
**Module:** Variance Report  
**Date:** 05-Jun-2026  
**Tested By:** D. Sureshbabu  

---

## Module Overview

The Variance Report module shows production plan vs sequence plan analysis with the following:

### Filters
- **Select Plant** (dropdown) — e.g., PLANT 1
- **Model** (multi-select dropdown) — ALL, IQUBE, JUPITER 110, JU...
- **Select From Date** (date picker)
- **Select To Date** (date picker)
- **Submit** button

### Sub-Tabs
1. **Analysis** (default view)
2. **Summary**
3. **Model**
4. **Variant**

### Table Sections (Analysis View)
| Section | Columns |
|---------|---------|
| Monthly Production (Cumulative) | Plan, Actual, Variance |
| Production Plan VS SAP Sequence Plan For the Day | Production Plan, Sequence Plan, Variance |
| SAP Sequence Plan VS Edited/Skipped Plan For the Day | SAP Sequence Plan, E/S Plan, Variance |
| Edited/Skipped Plan for the day Vs Actual for the day | Plan, Actual, Variance |

### Search & Action
- **Search Model Varient** (search box)
- **View** column with eye icon per row

---

## TEST CASES

### TC-VR-001: Variance Report Tab Navigation
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Precondition** | User is logged in and on Group Master page |
| **Steps** | 1. Click "Variance Report" tab in the navigation bar |
| **Expected** | Variance Report page loads with Analysis sub-tab active by default |

---

### TC-VR-002: Default Filter Values on Page Load
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Precondition** | Variance Report tab is active |
| **Steps** | 1. Observe filter section on page load |
| **Expected** | - "Select Plant" defaults to PLANT 1<br/>- "Model" defaults to ALL<br/>- "From Date" and "To Date" are pre-populated with valid date range<br/>- Data table is populated |

---

### TC-VR-003: Submit Button with Valid Date Range
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Select Plant = PLANT 1<br/>2. Select Model = ALL<br/>3. Set From Date = 01/06/2026<br/>4. Set To Date = 05/06/2026<br/>5. Click Submit |
| **Expected** | Table loads with variance data for all models within the date range. Total row is displayed at the bottom. |

---

### TC-VR-004: Submit Button with Single Model Selected
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Select Plant = PLANT 1<br/>2. Select Model = IQUBE<br/>3. Set valid date range<br/>4. Click Submit |
| **Expected** | Table shows only IQUBE model data with correct variance calculations |

---

### TC-VR-005: Submit Button with Multiple Models Selected
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Select Plant = PLANT 1<br/>2. Select Models = IQUBE, JUPITER 125<br/>3. Set valid date range<br/>4. Click Submit |
| **Expected** | Table shows selected models only + Total row reflecting selected models sum |

---

### TC-VR-006: Date Validation — From Date Greater Than To Date
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Set From Date = 05/06/2026<br/>2. Set To Date = 01/06/2026<br/>3. Click Submit |
| **Expected** | Error message displayed: "From Date cannot be greater than To Date" or form prevents submission |

---

### TC-VR-007: Date Validation — Future Date Selection
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Set To Date to a date in the future beyond today<br/>2. Click Submit |
| **Expected** | Either future dates are disabled in the date picker OR data shows only up to today |

---

### TC-VR-008: Date Validation — Empty Date Fields
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Clear From Date field<br/>2. Clear To Date field<br/>3. Click Submit |
| **Expected** | Validation error or default date range applied. No crash or empty table without message. |

---

### TC-VR-009: Variance Calculation Accuracy
| Field | Value |
|-------|-------|
| **Priority** | Critical |
| **Steps** | 1. Load Variance Report with data<br/>2. For each row, verify: Variance = Plan - Actual (for Monthly Production Cumulative) |
| **Expected** | Row 1 (IQUBE): 1800 - 1239 = 561 → Variance should be -561 ✓<br/>Row 2 (JUPITER 110 NEW): 2000 - 1900 = 100 → Variance = -100 ✓<br/>Row 4 (ORBITER): 200 - 461 = -261 → Variance should be -261 but shows 261 ⚠️ |

---

### TC-VR-010: Total Row Calculation Accuracy
| Field | Value |
|-------|-------|
| **Priority** | Critical |
| **Steps** | 1. Load data and check Total row<br/>2. Sum Plan column: 1800+2000+1600+200+600 = 6200<br/>3. Sum Actual column: 1239+1900+1621+461+573 = 5794<br/>4. Verify Variance total |
| **Expected** | Total Plan = 6,200 ✓, Total Actual = 5,794 ✓, Total Variance = -406 ✓ |

---

### TC-VR-011: Search Model Variant Functionality
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Type "IQUBE" in "Search Model Varient" search box<br/>2. Observe table filtering |
| **Expected** | Table filters to show only IQUBE row(s). Total row updates accordingly. |

---

### TC-VR-012: Search with Partial Text
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Type "JUP" in Search box |
| **Expected** | Table shows JUPITER 110 NEW and JUPITER 125 rows |

---

### TC-VR-013: Search with Non-existent Model
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Type "XYZABC" in Search box |
| **Expected** | Table shows "No data found" message or empty table with proper message |

---

### TC-VR-014: View Icon Click (Eye Icon)
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Click the eye (👁) icon in View column for IQUBE row |
| **Expected** | Detail view/popup/drill-down opens showing granular variance data for IQUBE |

---

### TC-VR-015: Analysis Sub-Tab (Default)
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Click "Analysis" sub-tab |
| **Expected** | Shows full analysis table with all 4 section groups (Monthly Production Cumulative, Production Plan VS SAP Sequence Plan, SAP Sequence Plan VS E/S Plan, Edited/Skipped vs Actual) |

---

### TC-VR-016: Summary Sub-Tab
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Click "Summary" sub-tab |
| **Expected** | Summary view loads with aggregated data. No errors. Layout renders properly. |

---

### TC-VR-017: Model Sub-Tab
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Click "Model" sub-tab |
| **Expected** | Model-wise breakdown view loads. Data corresponds to selected filters. |

---

### TC-VR-018: Variant Sub-Tab
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Click "Variant" sub-tab |
| **Expected** | Variant-level breakdown loads showing variant-wise production vs plan data |

---

### TC-VR-019: Sub-Tab Persistence After Filter Change
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Switch to "Summary" sub-tab<br/>2. Change date range<br/>3. Click Submit |
| **Expected** | Remains on Summary sub-tab with refreshed data (does not reset to Analysis) |

---

### TC-VR-020: Negative Variance Color Coding
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Observe Variance columns for negative values |
| **Expected** | Negative variance values are displayed in red/orange color for quick identification |

---

### TC-VR-021: Positive Variance Color Coding
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Observe Variance column for ORBITER and JUPITER 125 in "Monthly Production Cumulative" |
| **Expected** | Positive variance (Actual > Plan) shown in distinct color (green) — ORBITER shows 261, JUPITER 125 shows 21 |

---

### TC-VR-022: Column Sorting
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Click on "Plan" column header<br/>2. Click again for descending |
| **Expected** | Rows sort by Plan values ascending/descending. Total row remains at bottom. |

---

### TC-VR-023: Large Date Range Performance
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Set From Date = 01/01/2026<br/>2. Set To Date = 05/06/2026<br/>3. Click Submit |
| **Expected** | Data loads within acceptable time (< 5 seconds). No timeout or browser hang. |

---

### TC-VR-024: Plant Dropdown with Different Plant
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Change "Select Plant" to another plant (if available)<br/>2. Click Submit |
| **Expected** | Data refreshes with the selected plant's production data |

---

### TC-VR-025: Browser Refresh Retention
| Field | Value |
|-------|-------|
| **Priority** | Low |
| **Steps** | 1. Apply filters and load data<br/>2. Press F5 / browser refresh |
| **Expected** | Either filters persist with data OR page resets to defaults gracefully (no error) |

---

### TC-VR-026: Responsive Layout / Horizontal Scroll
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Resize browser to smaller width<br/>2. Observe table behavior |
| **Expected** | Table provides horizontal scroll. No content overflow. Headers align with data cells. |

---

### TC-VR-027: No Data Scenario
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Select a date range where no production occurred (e.g., a holiday/future)<br/>2. Click Submit |
| **Expected** | "No data available" message shown. No empty table without explanation. |

---

### TC-VR-028: Accessibility — Keyboard Navigation
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Tab through filters<br/>2. Use Enter to submit<br/>3. Tab through sub-tabs |
| **Expected** | All interactive elements are keyboard-accessible with visible focus indicators |

---

## BUG REPORT

### BUG-VR-001: Spelling Error — "Search Model Varient"

| Field | Details |
|-------|---------|
| **Severity** | Low |
| **Priority** | Medium |
| **Module** | Variance Report |
| **Steps** | 1. Navigate to Variance Report<br/>2. Observe search box placeholder/label |
| **Actual Result** | Label reads "Search Model **Varient**" |
| **Expected Result** | Label should read "Search Model **Variant**" |
| **Screenshot** | Visible in provided screenshot — search box area |
| **Environment** | QAS - tvsmsrvrqas.tvsmotor.net |

---

### BUG-VR-002: Variance Sign Convention Inconsistency (Monthly Production Cumulative)

| Field | Details |
|-------|---------|
| **Severity** | Medium |
| **Priority** | High |
| **Module** | Variance Report → Analysis |
| **Steps** | 1. Load Variance Report<br/>2. Check ORBITER row — Monthly Production Cumulative<br/>3. Plan = 200, Actual = 461 |
| **Actual Result** | Variance shows **261** (positive) — implying Actual exceeded Plan by 261 |
| **Expected Result** | If convention is `Plan - Actual`, variance should be **-261**. If convention is `Actual - Plan`, then IQUBE (Plan=1800, Actual=1239) should show **-561** not just -561. The sign convention appears inconsistent: negative when under-produced, positive when over-produced. Need to verify if this is intentional or a calculation bug. |
| **Impact** | Potential misinterpretation of production data — user may confuse whether positive means over-production or under-production |
| **Screenshot** | Row 4 (ORBITER): Plan=200, Actual=461, Variance=261 |

---

### BUG-VR-003: Possible Color Coding Inconsistency for Variance Values

| Field | Details |
|-------|---------|
| **Severity** | Medium |
| **Priority** | Medium |
| **Module** | Variance Report → All Variance Columns |
| **Steps** | 1. Observe color coding across all Variance columns<br/>2. Compare ORBITER's positive variance in Col 1 (Monthly) vs Col 2 (Production Plan VS SAP) |
| **Actual Result** | In "Production Plan VS SAP" section, ORBITER Variance = 642 (appears in green/positive color), while in "Edited/Skipped vs Actual", variance shows 509. Color coding needs verification across all 4 variance columns for consistency. |
| **Expected Result** | Consistent color convention: Red = negative (behind plan), Green = positive (ahead of plan) across ALL variance columns |

---

### BUG-VR-004: "Actual" Column Shows Values in Orange/Different Color Without Legend

| Field | Details |
|-------|---------|
| **Severity** | Low |
| **Priority** | Low |
| **Module** | Variance Report → Table |
| **Steps** | 1. Observe the "Actual" column values in Monthly Production Cumulative<br/>2. Notice they appear in a different color (orange/amber) |
| **Actual Result** | "Actual" values (1,239 / 1,900 / 1,621 / 461 / 573) are displayed in orange without a legend explaining the color significance |
| **Expected Result** | Either provide a color legend/tooltip explaining orange = actual values, OR use consistent text color with column headers distinguishing the sections |

---

### BUG-VR-005: Table Section Headers Lack Visual Separation

| Field | Details |
|-------|---------|
| **Severity** | Low |
| **Priority** | Low |
| **Module** | Variance Report → Table Layout |
| **Steps** | 1. Observe the table with 4 major section groups<br/>2. Check if column groups are easily distinguishable |
| **Actual Result** | While top-level section headers exist, the boundary between sections is only by background color. Dense data makes it difficult to distinguish which "Variance" column belongs to which section. |
| **Expected Result** | Add vertical border/separator between the 4 major table sections OR use alternating background colors for clarity |

---

### BUG-VR-006: Sl.No Column Shows Sequential Numbers Instead of Actual IDs

| Field | Details |
|-------|---------|
| **Severity** | Low |
| **Priority** | Low |
| **Module** | Variance Report → Table |
| **Steps** | 1. Observe Sl.No column<br/>2. Row 6 shows "6" with "Total" label |
| **Actual Result** | Total row has Sl.No = 6, treating it as a regular row |
| **Expected Result** | Total row should either have no Sl.No or be visually distinct (bold, different background) to differentiate it from data rows |

---

### BUG-VR-007: Model Dropdown Truncation — Full Values Not Visible

| Field | Details |
|-------|---------|
| **Severity** | Low |
| **Priority** | Medium |
| **Module** | Variance Report → Filter Section |
| **Steps** | 1. Observe Model dropdown showing "ALL, IQUBE, JUPITER 110, JU..." |
| **Actual Result** | Selected models text is truncated with "JU..." — cannot see full list of selected models |
| **Expected Result** | Show selected count (e.g., "5 models selected") or provide tooltip showing full selection, or make dropdown width responsive to content |

---

### BUG-VR-008: No Export/Download Feature Visible

| Field | Details |
|-------|---------|
| **Severity** | Medium |
| **Priority** | Medium |
| **Module** | Variance Report |
| **Steps** | 1. Observe Variance Report page<br/>2. Look for export options |
| **Actual Result** | No visible export button (CSV, Excel, PDF) for the variance report data |
| **Expected Result** | Production planning reports typically require export functionality for offline analysis and sharing with stakeholders |

---

## SUMMARY

| Category | Count |
|----------|-------|
| **Total Test Cases** | 28 |
| **Critical Priority** | 2 (TC-VR-009, TC-VR-010) |
| **High Priority** | 12 |
| **Medium Priority** | 10 |
| **Low Priority** | 4 |
| **Bugs Found** | 8 |
| **Bug Severity — Medium** | 3 |
| **Bug Severity — Low** | 5 |

---

## RECOMMENDATIONS

1. **Fix the spelling** "Varient" → "Variant" (quick win)
2. **Document variance calculation convention** — clarify if Variance = Plan - Actual or Actual - Plan
3. **Add a color legend** to the table explaining color coding significance
4. **Add export functionality** (Excel/CSV) for production planning use cases
5. **Test Summary, Model, and Variant sub-tabs** — screenshot only shows Analysis tab; manual exploration needed for complete coverage
