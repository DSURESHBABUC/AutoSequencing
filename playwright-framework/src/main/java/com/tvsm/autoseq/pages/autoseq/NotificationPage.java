package com.tvsm.autoseq.pages.autoseq;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tvsm.autoseq.base.BasePage;

/**
 * NotificationPage — Page Object for the Notification Popup and Notification Screen.
 * Application: https://uat-sns.tvsmotor.net/Autoseq/groupmaster
 *
 * Covers:
 *   - Notification popup that appears after sequence/MPS actions
 *   - OK button navigation to Notification screen
 *   - Notification screen details (type, timestamp, message)
 */
public class NotificationPage extends BasePage {

    // ── Notification Popup selectors ──────────────────────────────────────
    private static final String NOTIFICATION_POPUP       = ".notification-popup, .swal2-popup, .mat-dialog-container, .cdk-overlay-pane .mat-dialog-container";
    private static final String NOTIFICATION_POPUP_MSG   = ".notification-popup .message, .swal2-html-container, .mat-dialog-content, .notification-message";
    private static final String NOTIFICATION_OK_BTN      = ".notification-popup .ok-btn, .swal2-confirm, .mat-dialog-actions button:has-text('OK'), button:has-text('Ok'), button:has-text('ok')";
    private static final String NOTIFICATION_CLOSE_BTN   = ".notification-popup .close-btn, .swal2-close, .mat-dialog-close, button[aria-label='Close']";

    // ── Notification Screen selectors ─────────────────────────────────────
    private static final String NOTIFICATION_SCREEN      = ".notification-screen, .notification-list, .notification-container, app-notification";
    private static final String NOTIFICATION_ROW         = ".notification-row, .notification-item, .notification-card, table tbody tr";
    private static final String NOTIFICATION_TYPE        = ".notification-type, .notif-type, td:nth-child(1)";
    private static final String NOTIFICATION_TIMESTAMP   = ".notification-timestamp, .notif-time, td:nth-child(2)";
    private static final String NOTIFICATION_MESSAGE     = ".notification-message, .notif-msg, td:nth-child(3)";
    private static final String NOTIFICATION_BELL_ICON   = ".notification-bell, mat-icon:has-text('notifications'), [mattooltip='Notifications']";

    // ── Page navigation indicator ─────────────────────────────────────────
    private static final String NOTIFICATION_PAGE_HEADER = "h1:has-text('Notification'), h2:has-text('Notification'), .page-title:has-text('Notification')";

    public NotificationPage(Page page) {
        super(page);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Popup interactions
    // ─────────────────────────────────────────────────────────────────────

    /** Checks if the notification popup is currently visible. */
    public boolean isPopupVisible() {
        return page.locator(NOTIFICATION_POPUP).count() > 0
                && page.locator(NOTIFICATION_POPUP).first().isVisible();
    }

    /** Waits for the notification popup to appear within timeout. */
    public boolean waitForPopup(int timeoutMs) {
        try {
            page.waitForSelector(NOTIFICATION_POPUP,
                    new Page.WaitForSelectorOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(timeoutMs));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Gets the message text displayed in the notification popup. */
    public String getPopupMessage() {
        try {
            return page.locator(NOTIFICATION_POPUP_MSG).first().innerText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Checks if the OK button is visible in the popup. */
    public boolean isOkButtonVisible() {
        return page.locator(NOTIFICATION_OK_BTN).count() > 0
                && page.locator(NOTIFICATION_OK_BTN).first().isVisible();
    }

    /** Clicks the OK button in the notification popup. */
    public NotificationPage clickOkButton() {
        Locator okBtn = page.locator(NOTIFICATION_OK_BTN).first();
        try {
            okBtn.click();
        } catch (Exception e) {
            okBtn.evaluate("el => el.click()");
        }
        page.waitForTimeout(2000);
        System.out.println("✅ Clicked OK on notification popup");
        return this;
    }

    /** Checks if popup is still visible (for auto-dismiss verification). */
    public boolean isPopupStillVisibleAfterWait(int waitMs) {
        page.waitForTimeout(waitMs);
        return isPopupVisible();
    }

    /** Checks if OK button is focusable via keyboard (Tab). */
    public boolean isOkButtonFocusableViaKeyboard() {
        try {
            page.keyboard().press("Tab");
            page.waitForTimeout(500);
            // Check if active element matches OK button
            Object isOkFocused = page.evaluate(
                    "() => { const el = document.activeElement; " +
                    "return el && (el.textContent.trim().toLowerCase() === 'ok' || " +
                    "el.classList.contains('swal2-confirm') || " +
                    "el.classList.contains('ok-btn')); }");
            return Boolean.TRUE.equals(isOkFocused);
        } catch (Exception e) {
            return false;
        }
    }

    /** Presses Enter on the currently focused element. */
    public NotificationPage pressEnterOnFocused() {
        page.keyboard().press("Enter");
        page.waitForTimeout(2000);
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Notification Screen interactions
    // ─────────────────────────────────────────────────────────────────────

    /** Checks if the user has navigated to the Notification screen. */
    public boolean isOnNotificationScreen() {
        return page.url().contains("notification")
                || page.locator(NOTIFICATION_SCREEN).count() > 0
                || page.locator(NOTIFICATION_PAGE_HEADER).count() > 0;
    }

    /** Gets the count of notification rows on the Notification screen. */
    public int getNotificationRowCount() {
        return page.locator(NOTIFICATION_ROW).count();
    }

    /** Gets the type/category of the first notification row. */
    public String getFirstNotificationType() {
        try {
            return page.locator(NOTIFICATION_ROW).first()
                    .locator(NOTIFICATION_TYPE).innerText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Gets the timestamp of the first notification row. */
    public String getFirstNotificationTimestamp() {
        try {
            return page.locator(NOTIFICATION_ROW).first()
                    .locator(NOTIFICATION_TIMESTAMP).innerText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Gets the message text of the first notification row. */
    public String getFirstNotificationMessage() {
        try {
            return page.locator(NOTIFICATION_ROW).first()
                    .locator(NOTIFICATION_MESSAGE).innerText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Checks if the notification bell icon is visible (for navigation). */
    public boolean isNotificationBellVisible() {
        return page.locator(NOTIFICATION_BELL_ICON).count() > 0;
    }

    /** Clicks the notification bell icon. */
    public NotificationPage clickNotificationBell() {
        safeClick(NOTIFICATION_BELL_ICON);
        page.waitForTimeout(2000);
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Navigation helpers
    // ─────────────────────────────────────────────────────────────────────

    /** Navigates back using browser back button. */
    public NotificationPage navigateBack() {
        page.goBack();
        page.waitForTimeout(2000);
        return this;
    }

    /** Gets the current page URL. */
    public String getPageUrl() {
        return getCurrentUrl();
    }

    /** Dismisses any SweetAlert modal if present. */
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
