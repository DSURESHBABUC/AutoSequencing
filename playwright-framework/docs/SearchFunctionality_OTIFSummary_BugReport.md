# Bug Report — OTIF Summary Search Not Filtering Results

**Application:** https://uat-sns.tvsmotor.net/Autoseq/groupmaster
**Module:** Production Sequencing & Scheduling → OTIF → OTIF Summary (Details table)
**Date:** 11-Jun-2026
**Reported By:** D. Sureshbabu
**Environment:** UAT — uat-sns.tvsmotor.net

---

## BUG-OTIF-001: Details table search returns non-matching records

| Field | Details |
|-------|---------|
| **Summary** | OTIF Summary "Details" search box does not filter the table — searching for "RADEON" still returns rows that do not contain the search term |
| **Issue Type** | Bug |
| **Priority** | High |
| **Severity** | Major |
| **Component / Module** | AutoSeq → OTIF → OTIF Summary → Details grid search |
| **Environment** | UAT — https://uat-sns.tvsmotor.net/Autoseq/groupmaster |
| **Reproducibility** | Consistent (as observed) |
| **Affects Version** | UAT build as of 11-Jun-2026 |

### Pre-Conditions
1. User is logged in to the Production Sequencing & Scheduling application.
2. User is on the OTIF tab → OTIF Summary view.
3. Filters applied: Plant = `Plant 2`, Conveyor = `2ECON400, 2EC...`, From–To Date = `01/06/2026 – 30/06/2026`, Model = `ALL, APACHE R...`.
4. Details table is populated (1 – 30 of 104 records).

### Steps to Reproduce
1. Open the Live Sequence / Group Master screen and select the **OTIF** tab.
2. Apply the filters above and click **Submit** so the Details table loads.
3. In the **search box** above the Details table (top-right), type `RADEON`.
4. Observe the rows displayed in the Details table.

### Test Data
- Search term entered: `RADEON`

### Actual Result
- The Details table continues to display rows that do **not** match the search term `RADEON`.
- Example non-matching row still shown:
  - Seq No `17`, Unique id `2VCON300_20260601_17`, Variant **`SPORT ES+ OBDIIB`**, SKU `ND190820DB - TVS SPORT - OBDIIB ES+ MWL M GREY`.
- The result set is not reduced/filtered to only `RADEON` records; matching and non-matching records appear together.

### Expected Result
- On entering `RADEON`, the Details table should filter to show **only** records whose searchable fields (Variant and/or SKU No – Description) contain `RADEON`.
- Non-matching records (e.g., `SPORT ES+ OBDIIB`) should be excluded.
- The record count / pagination (`x of 104`) should update to reflect the filtered subset.
- If no records match, a clear "No data found" / empty state should be shown.

### Impact
- Users cannot rely on search to locate specific variants/SKUs within the 104-record OTIF dataset.
- Increases risk of acting on the wrong sequence/SKU data and slows OTIF analysis.

### Attachments
- Screenshot: OTIF Summary with `RADEON` entered in search; non-matching row `SPORT ES+ OBDIIB` highlighted.

### Notes / Open Questions for Dev
1. Which columns is the search intended to match against — Variant, SKU No – Description, Unique id, or all?
2. Is the search expected to filter client-side on the loaded page only, or trigger a server-side query across all 104 records?
3. Is the match expected to be case-insensitive and partial (contains) vs. exact?
4. Does pagination interfere with filtering (i.e., does search only look at the current 30-row page)?

### Suggested Areas to Investigate
- Search input change handler not bound to the table filter pipeline.
- Filter applied to a different/empty column key than the displayed Variant/SKU columns.
- Search evaluated only against the current page rather than the full result set.
- Submit/refresh overwriting or resetting the applied search filter.
```

---

## Jira-Ready Summary (copy/paste)

**Summary:** OTIF Summary Details search returns non-matching records (search not filtering)

**Priority:** High  **Severity:** Major  **Type:** Bug

**Environment:** UAT — https://uat-sns.tvsmotor.net/Autoseq/groupmaster → OTIF → OTIF Summary

**Steps to Reproduce:**
1. Open OTIF tab → OTIF Summary.
2. Filters: Plant 2 / Conveyor 2ECON400 / 01-06-2026–30-06-2026 / Model ALL,APACHE R... → Submit.
3. Type `RADEON` in the Details search box.

**Actual:** Table still shows non-matching rows (e.g., Variant `SPORT ES+ OBDIIB`, SKU `ND190820DB`). Results are not filtered to the search term.

**Expected:** Only records containing `RADEON` are shown; non-matching rows excluded; record count/pagination updates; empty state if no match.

**Impact:** Users cannot reliably search/locate variants or SKUs in the OTIF dataset.
