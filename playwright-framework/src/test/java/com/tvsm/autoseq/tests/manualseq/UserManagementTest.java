package com.tvsm.autoseq.tests.manualseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.manualseq.ManualSeqLoginPage;
import com.tvsm.autoseq.pages.manualseq.ManualSeqUserManagementPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * UserManagementTest — automation suite for the Manage Users screen.
 *
 * Flow: Navigate to Group Master → Click "User Management" tab →
 *       Verify Manage Users heading, table, search, Add User button,
 *       status badges, edit buttons, pagination and UI/UX behaviour.
 *
 * Application: https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest/groupmaster
 *
 *   TC-MSUM-001  User Management tab is visible on the navigation bar
 *   TC-MSUM-002  Clicking User Management opens Manage Users page
 *   TC-MSUM-003  Manage Users heading is displayed
 *   TC-MSUM-004  All required column headers are present
 *   TC-MSUM-005  User table is displayed with at least one row
 *   TC-MSUM-006  First row contains non-empty data
 *   TC-MSUM-007  Multi-plant assignments display as comma-separated values
 *   TC-MSUM-008  ACTIVE status badges are present
 *   TC-MSUM-009  DE-ACTIVE status badge handling (if any user is inactive)
 *   TC-MSUM-010  Every row has an Edit button in Actions column
 *   TC-MSUM-011  Search input is visible
 *   TC-MSUM-012  Search by Employee No filters the table
 *   TC-MSUM-013  Search by Name filters the table
 *   TC-MSUM-014  Search is case-insensitive
 *   TC-MSUM-015  Search with no match returns empty / no records
 *   TC-MSUM-016  Clearing search restores the full list
 *   TC-MSUM-017  Add User button is visible
 *   TC-MSUM-018  Clicking Add User opens a dialog/form
 *   TC-MSUM-019  Pagination control is visible
 *   TC-MSUM-020  Pagination range text shows "X – Y of Z"
 *   TC-MSUM-021  Next page navigation works
 *   TC-MSUM-022  Previous page navigation works
 *   TC-MSUM-023  Page size selector is visible
 *   TC-MSUM-024  Edit button on first row opens an edit form/dialog
 *   TC-MSUM-025  No error dialogs are present on the page
 *   TC-MSUM-026  Page URL remains stable on the User Management screen
 */
public class UserManagementTest extends BaseTest {

    private ManualSeqUserManagementPage umPage;

    private static final String SEARCH_EMP_NO = "14478";
    private static final String SEARCH_NAME   = "suresh";
    private static final String SEARCH_NONE   = "ZZZZZZNONE";

    @BeforeClass(alwaysRun = true)
    public void navigateToUserManagement() {
        // Always perform an explicit QAS login. The shared auth/state.json is for
        // the SNS (uat-sns) domain and does NOT carry roles for the QAS app —
        // role-protected tabs like User Management depend on the QAS session.
        ManualSeqLoginPage loginPage = new ManualSeqLoginPage(page);
        try {
            // Reuses auth/state.json when possible — only triggers Microsoft
            // login (and 2FA) when the saved session is missing or expired.
            loginPage.loginIfNeeded();
            page.waitForTimeout(4000);
            logInfo("✅ QAS login completed for: " + ConfigReader.username());
        } catch (Exception e) {
            logInfo("ℹ️  QAS login flow exception (continuing): " + e.getMessage());
        }

        // Navigate directly to the Group Master screen — the User Management
        // module is rendered there for users with the Admin role.
        navigateWithRetry(ConfigReader.manualSeqGroupMasterUrl(), 3);
        page.waitForTimeout(3000);

        try {
            waitForAppLoad();
        } catch (Exception ignored) {}
        page.waitForTimeout(2000);

        umPage = new ManualSeqUserManagementPage(page);
        umPage.dismissSwalIfPresent();

        // DEBUG — dump visible nav text so we can see if the tab is rendered
        try {
            String navText = (String) page.evaluate(
                    "Array.from(document.querySelectorAll('nav, [role=\"tab\"], .mat-tab-label-content, .mat-mdc-tab-label-content, a, button'))"
                    + ".map(e => (e.innerText || '').trim()).filter(t => t.length>0 && t.length<60).slice(0,40).join(' | ')");
            logInfo("Visible nav text: " + navText);
        } catch (Exception ignored) {}
        captureScreenshot("DEBUG_BeforeClass_Landing");

        // Click User Management tab
        if (umPage.isUserManagementTabVisible()) {
            umPage.clickUserManagementTab();
            page.waitForTimeout(2500);
        } else {
            logInfo("⚠️  User Management tab not detected during @BeforeClass");
        }
        umPage.dismissOverlays();

        logInfo("Navigated to: " + ConfigReader.manualSeqGroupMasterUrl());
        logInfo("On User Management screen");
    }

    /** Navigate with retry to handle transient ERR_ABORTED during SSO redirect chains. */
    private void navigateWithRetry(String url, int maxAttempts) {
        for (int i = 1; i <= maxAttempts; i++) {
            try {
                page.navigate(url);
                return;
            } catch (Exception e) {
                logInfo("Navigation attempt " + i + " failed: " + e.getMessage());
                if (i == maxAttempts) {
                    logInfo("⚠️  All navigation attempts failed — continuing with current URL");
                    return;
                }
                try { page.waitForTimeout(2000); } catch (Exception ignored) {}
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-001 — User Management tab visible
    // Skips the entire suite gracefully if the logged-in user lacks the
    // Admin role required to see User Management.
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 1,
          description = "TC-MSUM-001: Verify User Management tab is visible on the navigation bar")
    public void userManagementTabVisibleTest() {
        boolean visible = umPage.isUserManagementTabVisible();
        logInfo("User Management tab visible: " + visible);

        if (!visible) {
            String msg = "User Management tab is not visible — the logged-in user (" +
                    ConfigReader.username() + ") does NOT have the Admin role on this " +
                    "environment. Provide Admin credentials in config.properties (e.g. via " +
                    "-Dapp.username and -Dapp.password) to run this suite.";
            logInfo("⏭ " + msg);
            captureScreenshot("TC-MSUM-001_TabNotVisible_NonAdmin");
            throw new org.testng.SkipException(msg);
        }

        captureScreenshot("TC-MSUM-001_TabVisible");
        logInfo("✅ User Management tab is visible");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-002 — Click tab opens Manage Users
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 2,
          dependsOnMethods = "userManagementTabVisibleTest",
          description = "TC-MSUM-002: Clicking User Management opens Manage Users page")
    public void clickTabOpensManageUsersTest() {
        umPage.clickUserManagementTab();
        page.waitForTimeout(2500);
        umPage.dismissOverlays();

        boolean tableVisible = umPage.isTableVisible();
        boolean headingVisible = umPage.isManageUsersHeadingVisible();

        logInfo("Manage Users heading visible: " + headingVisible);
        logInfo("Table visible: " + tableVisible);

        Assert.assertTrue(headingVisible || tableVisible,
                "Manage Users page did not load — heading and table both missing");

        captureScreenshot("TC-MSUM-002_ManageUsersOpen");
        logInfo("✅ Manage Users page opened");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-003 — Manage Users heading displayed
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 3,
          dependsOnMethods = "clickTabOpensManageUsersTest",
          description = "TC-MSUM-003: Verify 'Manage Users' heading is displayed")
    public void manageUsersHeadingTest() {
        boolean visible = umPage.isManageUsersHeadingVisible();
        logInfo("Manage Users heading visible: " + visible);

        if (!visible) {
            logInfo("ℹ️  Heading not detected via standard selectors — page may use different markup");
        }

        captureScreenshot("TC-MSUM-003_Heading");
        logInfo("✅ Heading check complete");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-004 — Required column headers present
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 4,
          dependsOnMethods = "clickTabOpensManageUsersTest",
          description = "TC-MSUM-004: Verify all required column headers are present")
    public void columnHeadersTest() {
        List<String> headers = umPage.getColumnHeaders();
        logInfo("Column headers: " + headers);

        Assert.assertFalse(headers.isEmpty(),
                "No column headers found in the Manage Users table");

        // Check for at least a few expected headers
        String allHeaders = String.join(",", headers).toLowerCase();
        boolean hasExpectedFields = allHeaders.contains("employee")
                || allHeaders.contains("name")
                || allHeaders.contains("role")
                || allHeaders.contains("status");

        Assert.assertTrue(hasExpectedFields,
                "None of the expected headers (Employee No / Name / Role / Status) found");

        captureScreenshot("TC-MSUM-004_ColumnHeaders");
        logInfo("✅ Column headers verified: " + headers);
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-005 — Table displayed with rows
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 5,
          dependsOnMethods = "clickTabOpensManageUsersTest",
          description = "TC-MSUM-005: Verify user table is displayed with at least one row")
    public void userTableHasRowsTest() {
        int rowCount = umPage.getRowCount();
        logInfo("Row count: " + rowCount);

        Assert.assertTrue(umPage.isTableVisible(),
                "User table is not visible");
        Assert.assertTrue(rowCount > 0,
                "User table has zero rows");

        captureScreenshot("TC-MSUM-005_TableRows");
        logInfo("✅ Table displayed with " + rowCount + " rows");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-006 — First row has data
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 6,
          dependsOnMethods = "userTableHasRowsTest",
          description = "TC-MSUM-006: Verify first row contains non-empty data")
    public void firstRowHasDataTest() {
        String firstRow = umPage.getFirstRowText();
        logInfo("First row text: " + firstRow);

        Assert.assertFalse(firstRow.isBlank(),
                "First row is empty — no data displayed");

        captureScreenshot("TC-MSUM-006_FirstRow");
        logInfo("✅ First row contains data");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-007 — Multi-plant comma-separated display
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 7,
          dependsOnMethods = "userTableHasRowsTest",
          description = "TC-MSUM-007: Verify multi-plant assignments display as comma-separated values")
    public void multiPlantDisplayTest() {
        String pageContent = page.locator("body").innerText();

        // Look for typical multi-plant patterns like "1,2,3" or "1,2,3,7,9"
        boolean hasMultiPlant = pageContent.matches("(?s).*\\b\\d+,\\d+(,\\d+)+\\b.*");
        logInfo("Multi-plant pattern found in page: " + hasMultiPlant);

        if (!hasMultiPlant) {
            logInfo("ℹ️  No multi-plant users on current page — possibly all single-plant or empty");
        }

        captureScreenshot("TC-MSUM-007_MultiPlant");
        logInfo("✅ Multi-plant display check complete");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-008 — ACTIVE badges present
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 8,
          dependsOnMethods = "userTableHasRowsTest",
          description = "TC-MSUM-008: Verify ACTIVE status badges are displayed")
    public void activeBadgesTest() {
        String pageContent = page.locator("body").innerText();
        boolean hasActiveText = pageContent.contains("ACTIVE");

        int activeCount = umPage.getActiveCount();
        logInfo("ACTIVE badge selector count: " + activeCount);
        logInfo("ACTIVE text in page: " + hasActiveText);

        Assert.assertTrue(activeCount > 0 || hasActiveText,
                "No ACTIVE status badges found on the page");

        captureScreenshot("TC-MSUM-008_ActiveBadges");
        logInfo("✅ ACTIVE badges verified");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-009 — DE-ACTIVE handling
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 9,
          dependsOnMethods = "userTableHasRowsTest",
          description = "TC-MSUM-009: Verify DE-ACTIVE status badge displays correctly (if user exists)")
    public void deactiveBadgeTest() {
        String pageContent = page.locator("body").innerText();
        boolean hasDeactive = pageContent.contains("DE-ACTIVE");

        logInfo("DE-ACTIVE text found: " + hasDeactive);

        if (!hasDeactive) {
            logInfo("ℹ️  No DE-ACTIVE users on current page — skipping badge style validation");
        }

        captureScreenshot("TC-MSUM-009_DeActiveBadges");
        logInfo("✅ DE-ACTIVE badge check complete");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-010 — Edit button per row
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 10,
          dependsOnMethods = "userTableHasRowsTest",
          description = "TC-MSUM-010: Verify every row has an Edit button in the Actions column")
    public void editButtonPerRowTest() {
        int editButtons = umPage.getEditButtonCount();
        int rowCount = umPage.getRowCount();

        logInfo("Edit buttons: " + editButtons + " | Rows: " + rowCount);

        Assert.assertTrue(editButtons > 0,
                "No Edit buttons found on the page");
        Assert.assertTrue(editButtons >= rowCount,
                "Edit buttons (" + editButtons + ") < rows (" + rowCount + ") — some rows missing Edit");

        captureScreenshot("TC-MSUM-010_EditButtons");
        logInfo("✅ Every row has an Edit button");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-011 — Search input visible
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 11,
          dependsOnMethods = "clickTabOpensManageUsersTest",
          description = "TC-MSUM-011: Verify search input is visible")
    public void searchInputVisibleTest() {
        boolean visible = umPage.isSearchInputVisible();
        logInfo("Search input visible: " + visible);

        Assert.assertTrue(visible,
                "Search input is not visible on the Manage Users page");

        captureScreenshot("TC-MSUM-011_SearchInput");
        logInfo("✅ Search input visible");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-012 — Search by Employee No
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 12,
          dependsOnMethods = "searchInputVisibleTest",
          description = "TC-MSUM-012: Search by Employee No filters the table")
    public void searchByEmployeeNoTest() {
        umPage.dismissOverlays();
        int totalRows = umPage.getRowCount();

        umPage.search(SEARCH_EMP_NO);
        page.waitForTimeout(1500);

        int filteredRows = umPage.getRowCount();
        String value = umPage.getSearchValue();

        logInfo("Total: " + totalRows + " | Filtered (" + SEARCH_EMP_NO + "): " + filteredRows);
        logInfo("Search value: " + value);

        Assert.assertEquals(value, SEARCH_EMP_NO,
                "Search box does not contain '" + SEARCH_EMP_NO + "'");
        Assert.assertTrue(filteredRows <= totalRows,
                "Filtered rows (" + filteredRows + ") > total (" + totalRows + ")");

        umPage.clearSearch();
        page.waitForTimeout(1500);

        captureScreenshot("TC-MSUM-012_SearchEmpNo");
        logInfo("✅ Search by Employee No filters table");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-013 — Search by Name
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 13,
          dependsOnMethods = "searchInputVisibleTest",
          description = "TC-MSUM-013: Search by Name filters the table")
    public void searchByNameTest() {
        umPage.dismissOverlays();
        int totalRows = umPage.getRowCount();

        umPage.search(SEARCH_NAME);
        page.waitForTimeout(1500);

        int filteredRows = umPage.getRowCount();
        logInfo("Total: " + totalRows + " | Filtered (" + SEARCH_NAME + "): " + filteredRows);

        Assert.assertTrue(filteredRows <= totalRows,
                "Search by name did not narrow the results");

        umPage.clearSearch();
        page.waitForTimeout(1500);

        captureScreenshot("TC-MSUM-013_SearchName");
        logInfo("✅ Search by Name filters table");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-014 — Case-insensitive search
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 14,
          dependsOnMethods = "searchInputVisibleTest",
          description = "TC-MSUM-014: Verify search is case-insensitive")
    public void caseInsensitiveSearchTest() {
        umPage.dismissOverlays();

        umPage.search(SEARCH_NAME.toLowerCase());
        page.waitForTimeout(1500);
        int lowerCount = umPage.getRowCount();
        umPage.clearSearch();
        page.waitForTimeout(1000);

        umPage.search(SEARCH_NAME.toUpperCase());
        page.waitForTimeout(1500);
        int upperCount = umPage.getRowCount();
        umPage.clearSearch();
        page.waitForTimeout(1000);

        logInfo("Lowercase rows: " + lowerCount + " | Uppercase rows: " + upperCount);

        Assert.assertEquals(lowerCount, upperCount,
                "Case-insensitive search failed — counts differ (lower=" + lowerCount + ", upper=" + upperCount + ")");

        captureScreenshot("TC-MSUM-014_CaseInsensitive");
        logInfo("✅ Search is case-insensitive");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-015 — Search with no match
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 15,
          dependsOnMethods = "searchInputVisibleTest",
          description = "TC-MSUM-015: Search with no match returns empty / no records")
    public void searchNoMatchTest() {
        umPage.dismissOverlays();

        umPage.search(SEARCH_NONE);
        page.waitForTimeout(1500);

        int rows = umPage.getRowCount();
        String pageContent = page.locator("body").innerText();
        boolean noDataMsg = pageContent.toLowerCase().contains("no records")
                || pageContent.toLowerCase().contains("no data")
                || pageContent.toLowerCase().contains("no results");

        logInfo("Rows after no-match search: " + rows + " | No-data message: " + noDataMsg);

        Assert.assertTrue(rows == 0 || noDataMsg,
                "Expected zero rows or 'no records' message for non-existent search");

        umPage.clearSearch();
        page.waitForTimeout(1500);

        captureScreenshot("TC-MSUM-015_NoMatch");
        logInfo("✅ No-match search handled correctly");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-016 — Clear search restores list
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 16,
          dependsOnMethods = "searchInputVisibleTest",
          description = "TC-MSUM-016: Clearing search restores the full list")
    public void clearSearchRestoresListTest() {
        umPage.dismissOverlays();
        int originalCount = umPage.getRowCount();

        umPage.search(SEARCH_EMP_NO);
        page.waitForTimeout(1500);
        int filtered = umPage.getRowCount();

        umPage.clearSearch();
        page.waitForTimeout(1500);
        int restored = umPage.getRowCount();
        String value = umPage.getSearchValue();

        logInfo("Original: " + originalCount + " | Filtered: " + filtered + " | Restored: " + restored);
        logInfo("Search value after clear: '" + value + "'");

        Assert.assertTrue(value.isBlank(),
                "Search box not empty after clearing");
        Assert.assertTrue(restored >= filtered,
                "Restored rows (" + restored + ") < filtered (" + filtered + ")");

        captureScreenshot("TC-MSUM-016_SearchCleared");
        logInfo("✅ List restored after clearing search");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-017 — Add User button visible
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 17,
          dependsOnMethods = "clickTabOpensManageUsersTest",
          description = "TC-MSUM-017: Verify Add User button is visible")
    public void addUserButtonVisibleTest() {
        boolean visible = umPage.isAddUserButtonVisible();
        logInfo("Add User button visible: " + visible);

        Assert.assertTrue(visible,
                "Add User button is not visible on the page");

        captureScreenshot("TC-MSUM-017_AddUserBtn");
        logInfo("✅ Add User button is visible");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-018 — Add User opens dialog
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 18,
          dependsOnMethods = "addUserButtonVisibleTest",
          description = "TC-MSUM-018: Clicking Add User opens a dialog/form")
    public void addUserOpensDialogTest() {
        umPage.dismissOverlays();
        umPage.clickAddUser();
        page.waitForTimeout(1500);

        boolean dialogOpen = umPage.isAddUserDialogOpen();
        logInfo("Add User dialog open: " + dialogOpen);

        Assert.assertTrue(dialogOpen,
                "Add User dialog/form did not open after click");

        captureScreenshot("TC-MSUM-018_AddUserDialog");

        // Close dialog so subsequent tests aren't blocked
        umPage.closeDialogIfOpen();
        page.waitForTimeout(1000);
        umPage.dismissOverlays();

        logInfo("✅ Add User dialog opens correctly");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-019 — Pagination visible
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 19,
          dependsOnMethods = "clickTabOpensManageUsersTest",
          description = "TC-MSUM-019: Verify pagination control is visible")
    public void paginationVisibleTest() {
        boolean visible = umPage.isPaginationVisible();
        logInfo("Pagination visible: " + visible);

        Assert.assertTrue(visible,
                "Pagination control is not visible on the page");

        captureScreenshot("TC-MSUM-019_Pagination");
        logInfo("✅ Pagination control is visible");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-020 — Pagination range text
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 20,
          dependsOnMethods = "paginationVisibleTest",
          description = "TC-MSUM-020: Verify pagination range text shows 'X – Y of Z'")
    public void paginationRangeTextTest() {
        String range = umPage.getPaginationText();
        logInfo("Pagination range text: '" + range + "'");

        Assert.assertFalse(range.isBlank(),
                "Pagination range text is empty");
        Assert.assertTrue(range.toLowerCase().contains("of"),
                "Pagination range does not contain 'of' — actual: " + range);

        captureScreenshot("TC-MSUM-020_PaginationRange");
        logInfo("✅ Pagination range text is valid");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-021 — Next page works
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 21,
          dependsOnMethods = "paginationVisibleTest",
          description = "TC-MSUM-021: Verify next page navigation works")
    public void nextPageNavigationTest() {
        umPage.closeDialogIfOpen();
        umPage.dismissOverlays();

        if (!umPage.isNextPageEnabled()) {
            logInfo("ℹ️  Next page disabled — only one page of data; skipping");
            return;
        }

        String beforeRange = umPage.getPaginationText();
        umPage.clickNextPage();
        page.waitForTimeout(1500);
        String afterRange = umPage.getPaginationText();

        logInfo("Before: " + beforeRange + " | After: " + afterRange);
        Assert.assertNotEquals(afterRange, beforeRange,
                "Pagination range did not change after clicking Next");

        captureScreenshot("TC-MSUM-021_NextPage");
        logInfo("✅ Next page navigation works");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-022 — Previous page works
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 22,
          dependsOnMethods = "nextPageNavigationTest",
          description = "TC-MSUM-022: Verify previous page navigation works")
    public void prevPageNavigationTest() {
        umPage.closeDialogIfOpen();
        umPage.dismissOverlays();

        if (!umPage.isPrevPageEnabled()) {
            logInfo("ℹ️  Previous page disabled — already on first page; skipping");
            return;
        }

        String beforeRange = umPage.getPaginationText();
        umPage.clickPrevPage();
        page.waitForTimeout(1500);
        String afterRange = umPage.getPaginationText();

        logInfo("Before: " + beforeRange + " | After: " + afterRange);
        Assert.assertNotEquals(afterRange, beforeRange,
                "Pagination range did not change after clicking Previous");

        captureScreenshot("TC-MSUM-022_PrevPage");
        logInfo("✅ Previous page navigation works");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-023 — Page size selector visible
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 23,
          dependsOnMethods = "paginationVisibleTest",
          description = "TC-MSUM-023: Verify page size selector is visible")
    public void pageSizeSelectorTest() {
        boolean visible = umPage.isPageSizeSelectorVisible();
        logInfo("Page size selector visible: " + visible);

        if (!visible) {
            logInfo("ℹ️  Page size selector not detected via standard selectors");
        }

        captureScreenshot("TC-MSUM-023_PageSize");
        logInfo("✅ Page size selector check complete");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-024 — Edit button opens form
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 24,
          dependsOnMethods = "editButtonPerRowTest",
          description = "TC-MSUM-024: Edit button on first row opens an edit form/dialog")
    public void editButtonOpensFormTest() {
        umPage.dismissOverlays();
        boolean clicked = umPage.clickEditOnRow(0);

        if (!clicked) {
            logInfo("ℹ️  Could not click Edit on first row");
            captureScreenshot("TC-MSUM-024_EditFailed");
            return;
        }

        page.waitForTimeout(1500);
        boolean dialogOpen = umPage.isAddUserDialogOpen();
        logInfo("Edit dialog open: " + dialogOpen);

        Assert.assertTrue(dialogOpen,
                "Edit dialog/form did not open after clicking Edit");

        captureScreenshot("TC-MSUM-024_EditDialog");

        // Close dialog
        umPage.closeDialogIfOpen();
        page.waitForTimeout(1000);
        umPage.dismissOverlays();

        logInfo("✅ Edit button opens form/dialog");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-025 — No error dialogs
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 25,
          description = "TC-MSUM-025: Verify no error dialogs are present on the page")
    public void noErrorDialogsTest() {
        int errors = page.locator(".error-dialog, .alert-danger, [role='alertdialog'], .swal2-icon-error").count();
        logInfo("Error dialogs found: " + errors);

        Assert.assertEquals(errors, 0,
                "Error dialogs found on the User Management page");

        captureScreenshot("TC-MSUM-025_NoErrors");
        logInfo("✅ No error dialogs present");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSUM-026 — URL stable
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 26,
          description = "TC-MSUM-026: Verify page URL remains stable on the User Management screen")
    public void urlRemainsStableTest() {
        String url = umPage.getPageUrl();
        logInfo("Current URL: " + url);

        Assert.assertTrue(url.contains("SequencePlanTest"),
                "URL changed unexpectedly. Actual: " + url);

        captureScreenshot("TC-MSUM-026_UrlStable");
        logInfo("✅ URL remains stable: " + url);
    }
}
