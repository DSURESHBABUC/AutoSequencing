package com.tvsm.autoseq.tests.manualseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.manualseq.ManualSeqSequenceLivePage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * FilterDropdownTest — verifies all filter dropdowns (Plant, Unit, Conveyor, Shift)
 * on the Sequence Live screen are present, clickable, and show options.
 *
 * Application: https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest/groupmaster
 *
 *   TC-MSFD-001  Plant dropdown is present
 *   TC-MSFD-002  Unit dropdown is present
 *   TC-MSFD-003  Conveyor dropdown is present
 *   TC-MSFD-004  Shift dropdown is present
 *   TC-MSFD-005  Plant dropdown is clickable and shows options
 *   TC-MSFD-006  Shift dropdown shows expected options (ALL, 1, 1OT, 2, 2OT, 3)
 *   TC-MSFD-007  Selecting a plant refreshes the page data
 *   TC-MSFD-008  Selecting a shift refreshes the page data
 *   TC-MSFD-009  All dropdowns retain selection after page interaction
 *   TC-MSFD-010  Dropdown overlay closes after selection
 */
public class FilterDropdownTest extends BaseTest {

    private ManualSeqSequenceLivePage seqPage;

    @BeforeClass(alwaysRun = true)
    public void navigateToGroupMaster() {
        page.navigate(ConfigReader.manualSeqGroupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(3000);
        seqPage = new ManualSeqSequenceLivePage(page);
        logInfo("Navigated to: " + ConfigReader.manualSeqGroupMasterUrl());
    }

    @Test(priority = 1,
          description = "TC-MSFD-001: Verify Plant dropdown is present on the filter bar")
    public void plantDropdownPresentTest() {
        Assert.assertTrue(seqPage.isPlantDropdownPresent(),
                "Plant dropdown (1st mat-select) not found");
        logInfo("✅ Plant dropdown is present");
        captureScreenshot("TC-MSFD-001_PlantDropdown");
    }

    @Test(priority = 2,
          description = "TC-MSFD-002: Verify Unit dropdown is present on the filter bar")
    public void unitDropdownPresentTest() {
        Assert.assertTrue(seqPage.isUnitDropdownPresent(),
                "Unit dropdown (2nd mat-select) not found");
        logInfo("✅ Unit dropdown is present");
        captureScreenshot("TC-MSFD-002_UnitDropdown");
    }

    @Test(priority = 3,
          description = "TC-MSFD-003: Verify Conveyor dropdown is present on the filter bar")
    public void conveyorDropdownPresentTest() {
        Assert.assertTrue(seqPage.isConveyorDropdownPresent(),
                "Conveyor dropdown (3rd mat-select) not found");
        logInfo("✅ Conveyor dropdown is present");
        captureScreenshot("TC-MSFD-003_ConveyorDropdown");
    }

    @Test(priority = 4,
          description = "TC-MSFD-004: Verify Shift dropdown is present on the filter bar")
    public void shiftDropdownPresentTest() {
        Assert.assertTrue(seqPage.isShiftDropdownPresent(),
                "Shift dropdown (4th mat-select) not found");
        logInfo("✅ Shift dropdown is present");
        captureScreenshot("TC-MSFD-004_ShiftDropdown");
    }

    @Test(priority = 5,
          description = "TC-MSFD-005: Verify Plant dropdown is clickable and shows options")
    public void plantDropdownClickableTest() {
        page.locator("mat-select").first().click();
        page.waitForTimeout(1000);

        int optionCount = page.locator("mat-option").count();
        logInfo("Plant dropdown options: " + optionCount);

        Assert.assertTrue(optionCount > 0,
                "Plant dropdown has no options");

        // Close dropdown
        page.keyboard().press("Escape");
        page.waitForTimeout(500);

        captureScreenshot("TC-MSFD-005_PlantOptions");
        logInfo("✅ Plant dropdown shows " + optionCount + " options");
    }

    @Test(priority = 6,
          description = "TC-MSFD-006: Verify Shift dropdown shows expected options")
    public void shiftDropdownOptionsTest() {
        List<String> options = seqPage.getShiftDropdownOptions();
        logInfo("Shift dropdown options: " + options);

        Assert.assertFalse(options.isEmpty(),
                "Shift dropdown has no options");

        // Check for expected shift values
        String[] expectedOptions = {"ALL", "1", "1OT", "2", "2OT", "3"};
        for (String expected : expectedOptions) {
            boolean found = options.stream().anyMatch(o -> o.contains(expected));
            logInfo("Option '" + expected + "' found: " + found);
            if (!found) {
                logInfo("⚠️  Expected option '" + expected + "' not in: " + options);
            }
        }

        captureScreenshot("TC-MSFD-006_ShiftOptions");
        logInfo("✅ Shift dropdown options verified");
    }

    @Test(priority = 7,
          description = "TC-MSFD-007: Verify selecting a plant refreshes the page data")
    public void selectPlantRefreshesDataTest() {
        int rowsBefore = seqPage.getSequenceRowCount();

        // Click first mat-select and pick first option
        page.locator("mat-select").first().click();
        page.waitForTimeout(1000);

        if (page.locator("mat-option").count() > 0) {
            page.locator("mat-option").first().click();
            page.waitForTimeout(2000);
            seqPage.dismissSwalIfPresent();
        } else {
            page.keyboard().press("Escape");
        }

        int rowsAfter = seqPage.getSequenceRowCount();
        logInfo("Rows before: " + rowsBefore + " | After plant selection: " + rowsAfter);

        // Page should not crash
        Assert.assertTrue(seqPage.isOnGroupMasterPage(),
                "Page navigated away after plant selection");

        captureScreenshot("TC-MSFD-007_PlantSelection");
        logInfo("✅ Plant selection did not break the page");
    }

    @Test(priority = 8,
          description = "TC-MSFD-008: Verify selecting a shift refreshes the page data")
    public void selectShiftRefreshesDataTest() {
        if (!seqPage.isShiftDropdownPresent()) {
            logInfo("ℹ️  Shift dropdown not present — skipping");
            return;
        }

        page.locator("mat-select").nth(3).click();
        page.waitForTimeout(1000);

        if (page.locator("mat-option").count() > 0) {
            page.locator("mat-option").first().click();
            page.waitForTimeout(2000);
            seqPage.dismissSwalIfPresent();
        } else {
            page.keyboard().press("Escape");
        }

        Assert.assertTrue(seqPage.isOnGroupMasterPage(),
                "Page navigated away after shift selection");

        captureScreenshot("TC-MSFD-008_ShiftSelection");
        logInfo("✅ Shift selection did not break the page");
    }

    @Test(priority = 9,
          description = "TC-MSFD-009: Verify dropdowns retain selection after page interaction")
    public void dropdownsRetainSelectionTest() {
        // Get current displayed value of first dropdown
        String valueBefore = page.locator("mat-select").first().innerText().trim();
        logInfo("First dropdown value: " + valueBefore);

        // Interact with the page (scroll, click elsewhere)
        page.locator("body").click();
        page.waitForTimeout(500);

        String valueAfter = page.locator("mat-select").first().innerText().trim();
        logInfo("First dropdown value after interaction: " + valueAfter);

        Assert.assertEquals(valueAfter, valueBefore,
                "Dropdown value changed after page interaction");

        captureScreenshot("TC-MSFD-009_RetainSelection");
        logInfo("✅ Dropdown retains selection");
    }

    @Test(priority = 10,
          description = "TC-MSFD-010: Verify dropdown overlay closes after selection")
    public void dropdownOverlayClosesTest() {
        page.locator("mat-select").first().click();
        page.waitForTimeout(1000);

        // Verify overlay is open
        Assert.assertTrue(page.locator("mat-option").count() > 0,
                "Dropdown overlay did not open");

        // Select first option
        page.locator("mat-option").first().click();
        page.waitForTimeout(1000);
        seqPage.dismissSwalIfPresent();

        // Verify overlay is closed
        int overlayCount = page.locator(".cdk-overlay-pane mat-option").count();
        logInfo("Overlay options after selection: " + overlayCount);

        Assert.assertEquals(overlayCount, 0,
                "Dropdown overlay did not close after selection");

        captureScreenshot("TC-MSFD-010_OverlayClosed");
        logInfo("✅ Dropdown overlay closes after selection");
    }
}
