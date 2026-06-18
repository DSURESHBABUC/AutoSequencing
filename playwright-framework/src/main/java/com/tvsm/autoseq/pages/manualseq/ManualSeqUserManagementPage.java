package com.tvsm.autoseq.pages.manualseq;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.tvsm.autoseq.base.BasePage;

import java.util.ArrayList;
import java.util.List;

/**
 * ManualSeqUserManagementPage — Page Object for the User Management / Manage Users screen.
 *
 * Application: https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest/groupmaster
 * Screen     : User Management tab → Manage Users
 *
 * Covers: top tab navigation, Manage Users table, search box, Add User button,
 * Edit buttons, pagination, status badges (ACTIVE / DE-ACTIVE), role / plant cells.
 */
public class ManualSeqUserManagementPage extends BasePage {

    // ── Top navigation tab ────────────────────────────────────────────────
    private static final String[] TAB_SELECTORS = {
        ".mat-tab-label-content:has-text('User Management')",
        ".mat-mdc-tab-label-content:has-text('User Management')",
        "[role='tab']:has-text('User Management')",
        "a:has-text('User Management')",
        "button:has-text('User Management')",
        "li:has-text('User Management')",
        ".nav-link:has-text('User Management')",
        ".tab:has-text('User Management')"
    };

    // ── Page heading ──────────────────────────────────────────────────────
    private static final String[] MANAGE_USERS_HEADING_SELECTORS = {
        "h1:has-text('Manage Users')",
        "h2:has-text('Manage Users')",
        "h3:has-text('Manage Users')",
        ".heading:has-text('Manage Users')",
        ":text-is('Manage Users')"
    };

    // ── Search box ────────────────────────────────────────────────────────
    private static final String[] SEARCH_SELECTORS = {
        "input[placeholder='Search']",
        "input[placeholder*='Search' i]",
        "input[type='search']",
        "input[matinput][placeholder*='earch']",
        "mat-form-field input[type='text']"
    };

    // ── Add User button ───────────────────────────────────────────────────
    private static final String ADD_USER_BTN =
            "button:has-text('Add User'), button:has-text('+ Add User'), " +
            "button.add-user, [aria-label*='Add User']";

    // ── Table & rows ──────────────────────────────────────────────────────
    private static final String[] ROW_SELECTORS = {
        "table tbody tr",
        "mat-row",
        ".cdk-row",
        ".user-row",
        "[class*='user-row']"
    };

    private static final String[] HEADER_SELECTORS = {
        "table thead th",
        "mat-header-cell",
        ".cdk-header-cell",
        "th"
    };

    // ── Status badges ─────────────────────────────────────────────────────
    private static final String STATUS_ACTIVE   =
            ".badge:has-text('ACTIVE'), .status-active, " +
            "[class*='active']:has-text('ACTIVE'), :text-is('ACTIVE')";
    private static final String STATUS_INACTIVE =
            ".badge:has-text('DE-ACTIVE'), .status-inactive, " +
            "[class*='inactive']:has-text('DE-ACTIVE'), :text-is('DE-ACTIVE')";

    // ── Edit button ───────────────────────────────────────────────────────
    private static final String EDIT_BTN =
            "button:has-text('Edit'), [aria-label*='Edit'], .edit-btn";

    // ── Pagination ────────────────────────────────────────────────────────
    private static final String PAGINATION         = ".mat-paginator, mat-paginator, .pagination";
    private static final String PAGINATION_RANGE   = ".mat-paginator-range-label, .mat-mdc-paginator-range-label";
    private static final String NEXT_PAGE_BTN      = "button.mat-paginator-navigation-next, button[aria-label='Next page']";
    private static final String PREV_PAGE_BTN      = "button.mat-paginator-navigation-previous, button[aria-label='Previous page']";
    private static final String FIRST_PAGE_BTN     = "button.mat-paginator-navigation-first, button[aria-label='First page']";
    private static final String LAST_PAGE_BTN      = "button.mat-paginator-navigation-last, button[aria-label='Last page']";
    private static final String PAGE_SIZE_SELECTOR = ".mat-paginator-page-size-select, .mat-mdc-paginator-page-size-select, mat-select[aria-label*='page']";

    public ManualSeqUserManagementPage(Page page) {
        super(page);
    }

    // ── Navigation ────────────────────────────────────────────────────────

    public boolean isUserManagementTabVisible() {
        for (String sel : TAB_SELECTORS) {
            try {
                if (page.locator(sel).count() > 0) {
                    return true;
                }
            } catch (Exception ignored) {}
        }
        // Last-resort plain text lookup
        try {
            return page.locator("text=User Management").count() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickUserManagementTab() {
        for (String sel : TAB_SELECTORS) {
            try {
                if (page.locator(sel).count() > 0) {
                    Locator tab = page.locator(sel).first();
                    tab.click(new Locator.ClickOptions().setForce(true));
                    System.out.println("✅ Clicked User Management tab: " + sel);
                    page.waitForTimeout(2000);
                    return;
                }
            } catch (Exception ignored) {}
        }
        // Fallback: click by text
        try {
            page.locator("text=User Management").first()
                .click(new Locator.ClickOptions().setForce(true));
            System.out.println("✅ Clicked User Management via text= fallback");
        } catch (Exception e) {
            System.out.println("⚠️  Could not click User Management tab: " + e.getMessage());
        }
        page.waitForTimeout(2000);
    }

    public String getPageUrl() {
        return page.url();
    }

    public boolean isOnGroupMasterPage() {
        return page.url().contains("groupmaster") || page.url().contains("SequencePlanTest");
    }

    public boolean isManageUsersHeadingVisible() {
        for (String sel : MANAGE_USERS_HEADING_SELECTORS) {
            try {
                if (page.locator(sel).count() > 0) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    // ── Search ────────────────────────────────────────────────────────────

    public boolean isSearchInputVisible() {
        for (String sel : SEARCH_SELECTORS) {
            if (page.locator(sel).count() > 0) return true;
        }
        return false;
    }

    public void search(String keyword) {
        for (String sel : SEARCH_SELECTORS) {
            if (page.locator(sel).count() > 0) {
                Locator input = page.locator(sel).first();
                input.click();
                input.clear();
                input.fill(keyword);
                System.out.println("🔍 Searched: " + keyword);
                return;
            }
        }
    }

    public String getSearchValue() {
        for (String sel : SEARCH_SELECTORS) {
            if (page.locator(sel).count() > 0) {
                return page.locator(sel).first().inputValue();
            }
        }
        return "";
    }

    public void clearSearch() {
        for (String sel : SEARCH_SELECTORS) {
            if (page.locator(sel).count() > 0) {
                Locator input = page.locator(sel).first();
                input.click();
                input.clear();
                System.out.println("🧹 Search cleared");
                return;
            }
        }
    }

    // ── Add User ──────────────────────────────────────────────────────────

    public boolean isAddUserButtonVisible() {
        return page.locator(ADD_USER_BTN).count() > 0;
    }

    public void clickAddUser() {
        if (page.locator(ADD_USER_BTN).count() > 0) {
            page.locator(ADD_USER_BTN).first().click();
            System.out.println("✅ Clicked Add User");
            page.waitForTimeout(1500);
        }
    }

    public boolean isAddUserDialogOpen() {
        return page.locator("mat-dialog-container, .modal, [role='dialog']").count() > 0;
    }

    public void closeDialogIfOpen() {
        try {
            if (isAddUserDialogOpen()) {
                if (page.locator("button:has-text('Cancel')").count() > 0) {
                    page.locator("button:has-text('Cancel')").first()
                        .click(new Locator.ClickOptions().setForce(true));
                } else if (page.locator("button[aria-label='Close']").count() > 0) {
                    page.locator("button[aria-label='Close']").first()
                        .click(new Locator.ClickOptions().setForce(true));
                } else {
                    page.keyboard().press("Escape");
                }
                page.waitForTimeout(800);
            }
        } catch (Exception ignored) {}
        // Hard-cleanup: remove any remaining overlay containers that block interaction
        try {
            page.evaluate("document.querySelectorAll('mat-dialog-container, .cdk-overlay-backdrop, .cdk-overlay-pane').forEach(e => e.remove())");
            page.evaluate("var c = document.querySelector('.cdk-overlay-container'); if(c) c.innerHTML = '';");
            page.keyboard().press("Escape");
            page.waitForTimeout(300);
        } catch (Exception ignored) {}
    }

    // ── Table ─────────────────────────────────────────────────────────────

    public boolean isTableVisible() {
        for (String sel : ROW_SELECTORS) {
            if (page.locator(sel).count() > 0) return true;
        }
        return false;
    }

    public int getRowCount() {
        for (String sel : ROW_SELECTORS) {
            int count = page.locator(sel).count();
            if (count > 0) return count;
        }
        return 0;
    }

    public List<String> getColumnHeaders() {
        List<String> headers = new ArrayList<>();
        for (String sel : HEADER_SELECTORS) {
            int count = page.locator(sel).count();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    headers.add(page.locator(sel).nth(i).innerText().trim());
                }
                break;
            }
        }
        return headers;
    }

    public int getColumnCount() {
        return getColumnHeaders().size();
    }

    public String getFirstRowText() {
        for (String sel : ROW_SELECTORS) {
            if (page.locator(sel).count() > 0) {
                return page.locator(sel).first().innerText().trim();
            }
        }
        return "";
    }

    public String getCellText(int rowIndex, int colIndex) {
        for (String sel : ROW_SELECTORS) {
            int rowCount = page.locator(sel).count();
            if (rowCount > rowIndex) {
                Locator row = page.locator(sel).nth(rowIndex);
                int cellCount = row.locator("td, mat-cell, .mat-cell, .cdk-cell").count();
                if (cellCount > colIndex) {
                    return row.locator("td, mat-cell, .mat-cell, .cdk-cell")
                              .nth(colIndex).innerText().trim();
                }
            }
        }
        return "";
    }

    // ── Status badges ─────────────────────────────────────────────────────

    public int getActiveCount() {
        return page.locator(STATUS_ACTIVE).count();
    }

    public int getInactiveCount() {
        return page.locator(STATUS_INACTIVE).count();
    }

    public boolean hasActiveBadges() {
        return getActiveCount() > 0;
    }

    public boolean hasInactiveBadges() {
        return getInactiveCount() > 0;
    }

    // ── Edit button ───────────────────────────────────────────────────────

    public int getEditButtonCount() {
        return page.locator(EDIT_BTN).count();
    }

    public boolean clickEditOnRow(int rowIndex) {
        int count = getEditButtonCount();
        if (count > rowIndex) {
            try {
                page.locator(EDIT_BTN).nth(rowIndex).click();
                System.out.println("✅ Clicked Edit on row " + rowIndex);
                page.waitForTimeout(1500);
                return true;
            } catch (Exception e) {
                System.out.println("⚠️  Edit click failed: " + e.getMessage());
            }
        }
        return false;
    }

    // ── Pagination ────────────────────────────────────────────────────────

    public boolean isPaginationVisible() {
        return page.locator(PAGINATION).count() > 0;
    }

    public String getPaginationText() {
        if (page.locator(PAGINATION_RANGE).count() > 0) {
            return page.locator(PAGINATION_RANGE).first().innerText().trim();
        }
        return "";
    }

    public boolean isNextPageEnabled() {
        if (page.locator(NEXT_PAGE_BTN).count() == 0) return false;
        return page.locator(NEXT_PAGE_BTN).first().isEnabled();
    }

    public boolean isPrevPageEnabled() {
        if (page.locator(PREV_PAGE_BTN).count() == 0) return false;
        return page.locator(PREV_PAGE_BTN).first().isEnabled();
    }

    public void clickNextPage() {
        if (page.locator(NEXT_PAGE_BTN).count() > 0) {
            page.locator(NEXT_PAGE_BTN).first()
                .click(new Locator.ClickOptions().setForce(true));
            page.waitForTimeout(1500);
        }
    }

    public void clickPrevPage() {
        if (page.locator(PREV_PAGE_BTN).count() > 0) {
            page.locator(PREV_PAGE_BTN).first()
                .click(new Locator.ClickOptions().setForce(true));
            page.waitForTimeout(1500);
        }
    }

    public void clickLastPage() {
        if (page.locator(LAST_PAGE_BTN).count() > 0) {
            page.locator(LAST_PAGE_BTN).first()
                .click(new Locator.ClickOptions().setForce(true));
            page.waitForTimeout(1500);
        }
    }

    public void clickFirstPage() {
        if (page.locator(FIRST_PAGE_BTN).count() > 0) {
            page.locator(FIRST_PAGE_BTN).first()
                .click(new Locator.ClickOptions().setForce(true));
            page.waitForTimeout(1500);
        }
    }

    public boolean isPageSizeSelectorVisible() {
        return page.locator(PAGE_SIZE_SELECTOR).count() > 0;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    public void dismissOverlays() {
        try {
            page.evaluate("document.querySelectorAll('.cdk-overlay-backdrop').forEach(el => el.remove())");
            page.evaluate("document.querySelectorAll('.cdk-overlay-pane:empty').forEach(el => el.remove())");
            page.keyboard().press("Escape");
            page.waitForTimeout(300);
        } catch (Exception ignored) {}
    }

    public void dismissSwalIfPresent() {
        try {
            if (page.locator(".swal2-container").count() > 0) {
                if (page.locator(".swal2-confirm").count() > 0) {
                    page.locator(".swal2-confirm").evaluate("el => el.click()");
                } else {
                    page.keyboard().press("Escape");
                }
                page.waitForTimeout(1000);
            }
        } catch (Exception ignored) {}
    }
}
