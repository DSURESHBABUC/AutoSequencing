# DVP Analysis Dashboard — Test Cases & Bug Report

**Application:** https://uat-eyeq.tvsmotor.net/knowledgegraphdvpsummary  
**Module:** DVP Analysis Dashboard (under Knowledge Graph → DVP Analysis Dashboard)  
**Date:** 07-Jun-2026  
**Tested By:** D. Sureshbabu  

---

## Module Overview

The DVP Analysis Dashboard provides intelligent DVP Suggestions powered by real-world data. It allows users to analyze Usage Conditions across markets, understand Environment & Topography impacts, and optimize Maintenance strategies for robust product validation.

### Input Type Toggle
- **Part Number** (default) — Search by part number for DVP suggestions
- **Part Name** — Search by part name for DVP suggestions

### Filters (Part Number mode)
- **Part Number** (text input with suggestions) — e.g., N7140210, N9190900, P6010100
- **Country** (text input with suggestions) — e.g., Indonesia, Congo - PNR, Congo
- **Submit** button

### Navigation (Left Sidebar)
- Menu (collapsible)
- Customer Quality →
- Manufacturing Quality →
- Supplier Quality →
- Product Quality →
- **Knowledge Graph** →
  - Tree Map
  - Knowledge Graph 2.0
  - **DVP Analysis Dashboard** (current)
  - Master Bank
- Engine Consistency →
- Feedback & Ratings Insights

### Expected Output (After Submit)
- DVP Suggestions results
- Usage Conditions analysis
- Environment & Topography data
- Maintenance strategy recommendations

---

## TEST CASES

### TC-DVP-001: Page Loads at Correct URL
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Precondition** | User is authenticated to EYEQ platform |
| **Steps** | 1. Navigate to https://uat-eyeq.tvsmotor.net/knowledgegraphdvpsummary |
| **Expected** | Page loads with "Explore intelligent DVP Suggestions" header text, input type toggle, and search fields visible |

---

### TC-DVP-002: Page Title & Header Verification
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Observe page header after load |
| **Expected** | Header displays: "Explore intelligent DVP Suggestions powered by real-world data, analyze Usage Conditions across markets, understand Environment & Topography impacts, and optimize Maintenance strategies for robust product validation." Subtitle: "Data-driven DVP Insights. From field conditions to validation parameters." |

---

### TC-DVP-003: Input Type Toggle — Part Number (Default)
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Observe "Choose Input Type" section on page load |
| **Expected** | "Part Number" tab is selected/active by default. Part Number and Country input fields are visible. |

---

### TC-DVP-004: Input Type Toggle — Switch to Part Name
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Click "Part Name" toggle button |
| **Expected** | Form switches to Part Name input mode. Previous Part Number field is replaced with Part Name field. Country field remains. |

---

### TC-DVP-005: Input Type Toggle — Switch Back to Part Number
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Click "Part Name" tab<br/>2. Click "Part Number" tab again |
| **Expected** | Form reverts to Part Number mode. Part Number input with suggestions visible. |

---

### TC-DVP-006: Part Number Field — Suggestions Displayed
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Observe Part Number field area |
| **Expected** | Suggestion chips visible below the field: N7140210, N9190900, P6010100 |

---

### TC-DVP-007: Part Number Field — Click Suggestion N7140210
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Click suggestion chip "N7140210" |
| **Expected** | Part Number field is populated with "N7140210" |

---

### TC-DVP-008: Part Number Field — Click Suggestion N9190900
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Clear Part Number field<br/>2. Click suggestion chip "N9190900" |
| **Expected** | Part Number field is populated with "N9190900" |

---

### TC-DVP-009: Part Number Field — Click Suggestion P6010100
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Clear Part Number field<br/>2. Click suggestion chip "P6010100" |
| **Expected** | Part Number field is populated with "P6010100" |

---

### TC-DVP-010: Part Number Field — Manual Entry
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Click on Part Number input<br/>2. Type "N7140210" manually |
| **Expected** | Field accepts input. Auto-complete suggestions may appear as user types. |

---

### TC-DVP-011: Country Field — Suggestions Displayed
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Observe Country field area |
| **Expected** | Suggestion chips visible below the field: Indonesia, Congo - PNR, Congo |

---

### TC-DVP-012: Country Field — Click Suggestion "Indonesia"
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Click suggestion chip "Indonesia" |
| **Expected** | Country field is populated with "Indonesia" |

---

### TC-DVP-013: Country Field — Click Suggestion "Congo - PNR"
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Click suggestion chip "Congo - PNR" |
| **Expected** | Country field is populated with "Congo - PNR" |

---

### TC-DVP-014: Country Field — Manual Entry
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Click on Country input<br/>2. Type "Indonesia" manually |
| **Expected** | Field accepts input. Auto-complete suggestions may appear. |

---

### TC-DVP-015: Submit with Valid Part Number + Country
| Field | Value |
|-------|-------|
| **Priority** | Critical |
| **Steps** | 1. Enter Part Number = N7140210<br/>2. Enter Country = Indonesia<br/>3. Click Submit |
| **Expected** | Loading indicator appears. DVP analysis results load showing Usage Conditions, Environment & Topography data, and Maintenance recommendations. No error dialog. |

---

### TC-DVP-016: Submit with Part Number Only (No Country)
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Enter Part Number = N7140210<br/>2. Leave Country field empty<br/>3. Click Submit |
| **Expected** | Either: (a) validation error "Country is required" shown, or (b) results load for all countries for that part number |

---

### TC-DVP-017: Submit with Country Only (No Part Number)
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Leave Part Number empty<br/>2. Enter Country = Indonesia<br/>3. Click Submit |
| **Expected** | Validation error shown: "Part Number is required" (field marked with red asterisk *) |

---

### TC-DVP-018: Submit with Both Fields Empty
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Leave Part Number empty<br/>2. Leave Country empty<br/>3. Click Submit |
| **Expected** | Validation errors shown for both required fields. No API call made. No crash. |

---

### TC-DVP-019: Submit with Invalid Part Number
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Enter Part Number = "INVALIDXYZ000"<br/>2. Enter Country = Indonesia<br/>3. Click Submit |
| **Expected** | Graceful handling: "No data found" message or empty results state. No error dialog/crash. |

---

### TC-DVP-020: Submit with Invalid Country
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Enter Part Number = N7140210<br/>2. Enter Country = "XYZINVALID"<br/>3. Click Submit |
| **Expected** | Graceful handling: "No data found" or empty results. No unhandled error. |

---

### TC-DVP-021: Required Field Indicator — Part Number Has Red Asterisk
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Observe "Part Number" label |
| **Expected** | Red asterisk (*) visible next to "Part Number" label indicating mandatory field |

---

### TC-DVP-022: Required Field Indicator — Country Has Red Asterisk
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Observe "Country" label |
| **Expected** | Red asterisk (*) visible next to "Country" label indicating mandatory field |

---

### TC-DVP-023: Left Sidebar Navigation — Menu Collapse/Expand
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Click the collapse icon (◁) on the sidebar |
| **Expected** | Sidebar collapses. Content area expands. Menu can be re-expanded. |

---

### TC-DVP-024: Left Sidebar Navigation — Knowledge Graph Submenu
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Click "Knowledge Graph" in the sidebar<br/>2. Observe submenu expansion |
| **Expected** | Submenu shows: Tree Map, Knowledge Graph 2.0, DVP Analysis Dashboard (highlighted/active), Master Bank |

---

### TC-DVP-025: Left Sidebar — DVP Analysis Dashboard Is Active/Highlighted
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Observe sidebar state on DVP Analysis Dashboard page |
| **Expected** | "DVP Analysis Dashboard" menu item is highlighted/active with distinct styling (blue left border visible in screenshot) |

---

### TC-DVP-026: Navigation to Knowledge Graph 2.0 from Sidebar
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Click "Knowledge Graph 2.0" in sidebar |
| **Expected** | Navigates to https://uat-eyeq.tvsmotor.net/knowledgegraph2.0 without error |

---

### TC-DVP-027: Navigation to Tree Map from Sidebar
| Field | Value |
|-------|-------|
| **Priority** | Low |
| **Steps** | 1. Click "Tree Map" in sidebar |
| **Expected** | Navigates to Tree Map page without error |

---

### TC-DVP-028: Submit Button — Visual State Before Input
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Observe Submit button without entering any data |
| **Expected** | Submit button is either disabled (grayed out) until required fields are filled, OR enabled and shows validation on click |

---

### TC-DVP-029: Results Display — DVP Suggestions Section
| Field | Value |
|-------|-------|
| **Priority** | Critical |
| **Steps** | 1. Submit valid Part Number (N7140210) + Country (Indonesia)<br/>2. Wait for results |
| **Expected** | DVP Suggestions section loads with relevant test recommendations. Data is readable and formatted. |

---

### TC-DVP-030: Results Display — Usage Conditions Section
| Field | Value |
|-------|-------|
| **Priority** | Critical |
| **Steps** | 1. After valid submission, observe results area |
| **Expected** | Usage Conditions data is displayed showing real-world usage analysis for the selected part/country |

---

### TC-DVP-031: Results Display — Environment & Topography Section
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. After valid submission, observe results area |
| **Expected** | Environment and Topography analysis section shows relevant geographical/environmental data |

---

### TC-DVP-032: Results Display — Maintenance Section
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. After valid submission, observe results area |
| **Expected** | Maintenance strategy recommendations section loads with actionable insights |

---

### TC-DVP-033: Multiple Submissions — Change Part Number
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Submit N7140210 + Indonesia<br/>2. Clear Part Number<br/>3. Enter N9190900<br/>4. Click Submit again |
| **Expected** | Results refresh with new data for N9190900. Previous results are replaced. No stale data. |

---

### TC-DVP-034: Multiple Submissions — Change Country
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Submit N7140210 + Indonesia<br/>2. Clear Country<br/>3. Enter Congo<br/>4. Click Submit again |
| **Expected** | Results refresh with DVP data for N7140210 in Congo market. |

---

### TC-DVP-035: Page Refresh — State Retention
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Submit valid data and view results<br/>2. Press F5/browser refresh |
| **Expected** | Either: (a) Page retains input values and results, or (b) Page resets to initial state gracefully (no error) |

---

### TC-DVP-036: Loading State During Data Fetch
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Click Submit with valid data<br/>2. Observe page during data loading |
| **Expected** | Loading spinner/indicator visible while API fetches data. User cannot double-submit. Spinner disappears when data loads. |

---

### TC-DVP-037: API Timeout Handling
| Field | Value |
|-------|-------|
| **Priority** | High |
| **Steps** | 1. Submit valid data<br/>2. Simulate slow network (throttle in DevTools) |
| **Expected** | Timeout handled gracefully with user-friendly error message. No infinite spinner. Retry option available. |

---

### TC-DVP-038: Special Characters in Part Number Field
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Enter Part Number = "N7140210!@#$%"<br/>2. Click Submit |
| **Expected** | Either: (a) Input validation prevents special characters, or (b) API returns "no data" gracefully |

---

### TC-DVP-039: SQL Injection Test — Part Number Field
| Field | Value |
|-------|-------|
| **Priority** | Critical (Security) |
| **Steps** | 1. Enter Part Number = "' OR 1=1 --"<br/>2. Enter Country = Indonesia<br/>3. Click Submit |
| **Expected** | No data returned. No server error exposed. Input is sanitized. |

---

### TC-DVP-040: XSS Test — Part Number Field
| Field | Value |
|-------|-------|
| **Priority** | Critical (Security) |
| **Steps** | 1. Enter Part Number = "<script>alert('xss')</script>"<br/>2. Click Submit |
| **Expected** | Script is not executed. Input is escaped/sanitized. No alert popup. |

---

### TC-DVP-041: Responsive Layout — Mobile View
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Resize browser to 375px width (mobile)<br/>2. Observe page layout |
| **Expected** | Sidebar collapses/hides. Input fields stack vertically. Submit button remains accessible. Suggestion chips wrap properly. |

---

### TC-DVP-042: Accessibility — Keyboard Navigation
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Tab through Part Number toggle, input fields, suggestions, and Submit button |
| **Expected** | All interactive elements are keyboard-accessible with visible focus indicators |

---

### TC-DVP-043: Part Number Field — Max Length Boundary
| Field | Value |
|-------|-------|
| **Priority** | Medium |
| **Steps** | 1. Type a very long string (100+ characters) in Part Number field |
| **Expected** | Either: (a) Field has max-length limit, or (b) Handles gracefully without breaking layout |

---

### TC-DVP-044: Country Field — Max Length Boundary
| Field | Value |
|-------|-------|
| **Priority** | Low |
| **Steps** | 1. Type a very long string (100+ characters) in Country field |
| **Expected** | Either: (a) Field has max-length limit, or (b) Handles gracefully |

---

### TC-DVP-045: Suggestion Chips — Visual Feedback on Hover
| Field | Value |
|-------|-------|
| **Priority** | Low |
| **Steps** | 1. Hover over suggestion chip "N7140210" |
| **Expected** | Chip shows hover state (cursor pointer, color change, or slight elevation) indicating clickability |

---

---

## BUG REPORT

### BUG-DVP-001: "Part Number" and "Country" Fields Lack Placeholder Consistency

| Field | Details |
|-------|---------|
| **Severity** | Low |
| **Priority** | Low |
| **Module** | DVP Analysis Dashboard → Input Fields |
| **Steps** | 1. Navigate to DVP Analysis Dashboard<br/>2. Observe Part Number field placeholder: "Search Part Number..."<br/>3. Observe Country field placeholder: "Search Country..." |
| **Actual Result** | Part Number placeholder says "Search Part Number..." with a search icon (🔍). Country says "Search Country..." with a search icon. These suggest a search/autocomplete behavior. |
| **Expected Result** | If these are autocomplete fields, typing should trigger suggestions from a dropdown. If they are plain text inputs with predefined suggestions below, the placeholder should read "Enter Part Number" instead of "Search..." to avoid confusion. |
| **Impact** | Minor UX confusion — "Search" implies a lookup/autocomplete mechanism |

---

### BUG-DVP-002: Suggestion Chips Positioning May Confuse Users

| Field | Details |
|-------|---------|
| **Severity** | Low |
| **Priority** | Medium |
| **Module** | DVP Analysis Dashboard → Suggestions |
| **Steps** | 1. Observe "Suggestions:" label and chips below Part Number and Country fields |
| **Actual Result** | Suggestion chips (N7140210, N9190900, P6010100 for Part Number; Indonesia, Congo - PNR, Congo for Country) are displayed as static chips below the input fields with a "Suggestions:" label |
| **Expected Result** | It's unclear if clicking a suggestion populates the field. If they are clickable examples, they should have tooltip/hover state. If they are just informational, they should be styled differently (e.g., plain text without chip styling). |
| **Impact** | Users may not realize suggestions are interactive, or may expect more comprehensive autocomplete |

---

### BUG-DVP-003: No Clear/Reset Button for Input Fields

| Field | Details |
|-------|---------|
| **Severity** | Medium |
| **Priority** | Medium |
| **Module** | DVP Analysis Dashboard → Form Controls |
| **Steps** | 1. Enter Part Number = N7140210<br/>2. Enter Country = Indonesia<br/>3. Look for a way to clear/reset both fields |
| **Actual Result** | No visible "Reset" or "Clear" button. User must manually select and delete text from each field. |
| **Expected Result** | A "Reset" or "Clear" button should be available to clear all form fields in one click, especially after viewing results when user wants to try a different combination. |
| **Impact** | Reduced usability — multiple searches require manual clearing of each field |

---

### BUG-DVP-004: Country Field Shows "Congo - PNR" — Abbreviation Not Explained

| Field | Details |
|-------|---------|
| **Severity** | Low |
| **Priority** | Low |
| **Module** | DVP Analysis Dashboard → Country Suggestions |
| **Steps** | 1. Observe Country suggestions: "Indonesia", "Congo - PNR", "Congo" |
| **Actual Result** | "Congo - PNR" abbreviation is not explained anywhere. "PNR" could mean Pointe-Noire (city), Passenger Name Record, or something domain-specific. Also unclear why "Congo" and "Congo - PNR" are separate options. |
| **Expected Result** | Either: (a) Full name displayed (e.g., "Congo - Pointe-Noire"), or (b) Tooltip on hover explaining the abbreviation, or (c) A help icon with context |
| **Impact** | Users from non-African markets may not understand which Congo region they should select |

---

### BUG-DVP-005: "Choose Input Type" Label Vertical Alignment with Toggle

| Field | Details |
|-------|---------|
| **Severity** | Low |
| **Priority** | Low |
| **Module** | DVP Analysis Dashboard → UI Layout |
| **Steps** | 1. Observe "Choose Input Type" text and the toggle buttons below it |
| **Actual Result** | "Choose Input Type" is a standalone label above the toggle buttons. The toggle buttons (Part Number / Part Name) use icon-style buttons that may not be immediately clear as a selection mechanism. |
| **Expected Result** | The toggle should be a more standard radio button group or segmented control with clearer active/inactive states. The "Part Number" tab with the clipboard icon is active but the visual distinction could be stronger. |
| **Impact** | Minor — users familiar with the UI will understand, but new users may not immediately grasp the toggle concept |

---

### BUG-DVP-006: No Breadcrumb Navigation

| Field | Details |
|-------|---------|
| **Severity** | Low |
| **Priority** | Low |
| **Module** | DVP Analysis Dashboard → Navigation |
| **Steps** | 1. Navigate to DVP Analysis Dashboard via sidebar<br/>2. Look for breadcrumb trail |
| **Actual Result** | No breadcrumb navigation (e.g., "Knowledge Graph > DVP Analysis Dashboard") is shown at the top of the page |
| **Expected Result** | Breadcrumb trail for navigation context, especially since the page is nested under Knowledge Graph submenu |
| **Impact** | Minor — sidebar shows location, but breadcrumbs provide additional wayfinding for complex hierarchies |

---

### BUG-DVP-007: Submit Button — No Loading State Feedback (Potential)

| Field | Details |
|-------|---------|
| **Severity** | Medium |
| **Priority** | High |
| **Module** | DVP Analysis Dashboard → Submit Action |
| **Steps** | 1. Enter valid Part Number and Country<br/>2. Click Submit<br/>3. Observe button during API call |
| **Actual Result** | (Needs verification) If Submit button doesn't show loading state (spinner/disabled), user may click multiple times causing duplicate API calls |
| **Expected Result** | Submit button should: (a) become disabled during loading, (b) show inline spinner or "Loading..." text, (c) prevent double-submission |
| **Impact** | Potential duplicate API calls and confusion about whether the action registered |

---

### BUG-DVP-008: Page Subtitle Has Unclear Typography Hierarchy

| Field | Details |
|-------|---------|
| **Severity** | Low |
| **Priority** | Low |
| **Module** | DVP Analysis Dashboard → Header Section |
| **Steps** | 1. Observe the header text area |
| **Actual Result** | Main text contains multiple highlighted terms (DVP Suggestions, Usage Conditions, Environment & Topography, Maintenance) in different styling (bold/underlined). The subtitle "Data-driven DVP Insights. From field conditions to validation parameters." appears in italic below with a divider line. |
| **Expected Result** | The highlighted keywords in the description create visual noise. Consider a cleaner approach: a concise H1 title + short subtitle, with the detailed description available via "Learn more" link or collapsed accordion. |
| **Impact** | Information overload on page load — the descriptive paragraph competes with the action area (input fields) for attention |

---

---

## AUTOMATION BLOCKERS & ISSUES

### 🚫 Critical Blockers

| # | Blocker | Impact | Workaround |
|---|---------|--------|------------|
| 1 | **Microsoft SSO Authentication** | The EYEQ platform uses Microsoft MSAL/SSO login. Playwright must handle the multi-step OAuth flow (email → password → KMSI prompt → redirect). This adds ~10-15 seconds per suite and can fail due to MFA/Conditional Access policies. | Current framework handles SSO in `BaseTest.performLoginAndSaveState()` with auth state caching (8-hour TTL). Works but fragile if MFA is enforced. |
| 2 | **Angular Material Components (mat-select, mat-chip)** | DVP Analysis Dashboard uses Angular Material components. Standard `select` locators don't work for `mat-select`. Suggestion chips may use custom directives without standard HTML attributes. | Use composite locators: `mat-select`, `mat-option`, `mat-chip`. Page Object already handles this with multi-selector CSS. |
| 3 | **Dynamic Content Loading (SPA)** | The page is a Single Page Application. After clicking Submit, results load asynchronously. No standard page navigation event fires. `waitForNavigation()` won't work. | Use `waitForSelector()` on result elements, or `waitForResponse()` on specific API endpoints. `waitForLoadState('networkidle')` can timeout on SPAs with WebSocket/polling. |
| 4 | **No Unique Test IDs on Elements (data-testid)** | The application doesn't use `data-testid` or `data-cy` attributes. Locators rely on CSS classes, ARIA labels, and text content which can break with UI updates. | Current Page Object uses broad multi-selector strategies (e.g., fallback chains). Fragile long-term — request dev team to add `data-testid` attributes. |

### ⚠️ Medium Issues

| # | Issue | Impact | Workaround |
|---|-------|--------|------------|
| 5 | **Suggestion Chip Interaction** | Clicking suggestion chips (N7140210, Indonesia, etc.) may use Angular click handlers that don't respond to Playwright `.click()` on the text element. May need to target the mat-chip or parent container. | Use `page.locator("mat-chip:has-text('N7140210')")` or evaluate JS click: `element.dispatchEvent(new Event('click'))`. |
| 6 | **Input Type Toggle (Part Number / Part Name)** | The toggle uses a custom component (likely `mat-button-toggle-group`). Standard button click may not trigger Angular's `(click)` binding. | Locator: `mat-button-toggle:has-text('Part Name')` then `.click()`. Verify with `page.waitForSelector()` on the changed form field. |
| 7 | **API Response Validation** | After Submit, there's no deterministic way to know when results have fully rendered. The page may show partial results, loading skeletons, or charts that render progressively. | Intercept API responses with `page.waitForResponse(url => url.includes('/dvp'))` and then wait for DOM to update. |
| 8 | **Network Dependency** | Tests require network access to `uat-eyeq.tvsmotor.net`. Cannot run offline or in isolated CI environments without VPN/network access. | Require VPN in CI pipeline. Add reachability check (existing `isHostReachable()` in BaseTest). Skip tests gracefully if host unreachable. |
| 9 | **Search Input Type Ambiguity** | The Part Number and Country inputs use `input[type='search']` or custom Angular inputs. The exact selector depends on the rendered HTML which changes between Angular versions. | Use broad selectors: `input[placeholder*='Part Number' i]`, `input[placeholder*='Country' i]`. |
| 10 | **Results Variability** | DVP analysis results depend on backend data. Test assertions can't hardcode expected values. Must use structural assertions (element exists, not empty, correct format) rather than exact value matching. | Assert structure: results container visible, at least one section populated, no error state. Avoid asserting specific text values. |

### 🔧 Recommendations for Dev Team

1. **Add `data-testid` attributes** to key elements:
   - `data-testid="input-type-toggle-part-number"`
   - `data-testid="input-type-toggle-part-name"`
   - `data-testid="part-number-input"`
   - `data-testid="country-input"`
   - `data-testid="submit-button"`
   - `data-testid="suggestion-chip-{value}"`
   - `data-testid="results-container"`
   - `data-testid="dvp-suggestions-section"`
   - `data-testid="usage-conditions-section"`

2. **Expose API endpoints** documentation so automation can use `waitForResponse()` with specific URL patterns.

3. **Add error state classes** (e.g., `.dvp-error-state`, `.dvp-no-data`) that are easy to detect programmatically.

4. **Implement a service worker or loading flag** (e.g., `window.__DVP_LOADING__ = true/false`) that Playwright can poll for readiness.

5. **Consider SSO bypass for test environments** — a test-only API token or cookie-based auth that skips the MSAL flow entirely for CI/CD pipelines.

---

## SUMMARY

| Category | Count |
|----------|-------|
| **Total Test Cases** | 45 |
| **Critical Priority** | 4 (TC-DVP-015, TC-DVP-029, TC-DVP-039, TC-DVP-040) |
| **High Priority** | 19 |
| **Medium Priority** | 14 |
| **Low Priority** | 8 |
| **Bugs Found** | 8 |
| **Bug Severity — Medium** | 2 (BUG-DVP-003, BUG-DVP-007) |
| **Bug Severity — Low** | 6 |
| **Automation Blockers — Critical** | 4 |
| **Automation Blockers — Medium** | 6 |

---

## TESTING VERDICT

**Module Status:** 🟡 Partially Testable via Automation

The DVP Analysis Dashboard is automatable with the existing Playwright + Java framework, but faces these primary challenges:

1. **SSO Auth** — handled by existing framework (auth state caching)
2. **Angular Material components** — handled with composite locators in DvpSummaryPage
3. **Result validation** — limited to structural checks (presence, non-empty) since data is dynamic
4. **No test IDs** — biggest long-term fragility risk

**Recommendation:** Run the existing `DvpSummaryTest.java` (48 test methods) to get baseline pass/fail rates, then prioritize adding `data-testid` attributes with the dev team to stabilize the suite.
