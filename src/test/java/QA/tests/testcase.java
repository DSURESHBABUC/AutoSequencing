package QA.tests;

import org.testng.annotations.Test;
import base.BaseTest;
import PageEvent.HomePageEvents;

public class testcase extends BaseTest {

    HomePageEvents homePage = new HomePageEvents();

    @Test
    public void loginTest() {
        homePage.loginCredentials();
    }

    @Test(dependsOnMethods = "loginTest")
    public void signInTest() {
        homePage.signIn();
    }

    @Test(dependsOnMethods = "signInTest")
    public void sequenceLiveTabTest() throws InterruptedException {
        homePage.goToSequenceLive();
    }

    @Test(dependsOnMethods = "sequenceLiveTabTest")
    public void mpsTabTest() throws InterruptedException {
        homePage.clickMainTab("MPS");
        Thread.sleep(1500);
        homePage.clickMpsPlan();
    }

    @Test(dependsOnMethods = "mpsTabTest")
    public void dataMaintenanceTabTest() throws InterruptedException {
        homePage.goToDataMaintenance();
    }

    // ---------------- Data Maintenance Screens ----------------
    @Test(dependsOnMethods = "dataMaintenanceTabTest")
    public void stockMaintenanceDetailsTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Stock Maintenance Details");
    }

    @Test(dependsOnMethods = "stockMaintenanceDetailsTest")
    public void shiftActiveDetailsTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Shift active details");
    }

    @Test(dependsOnMethods = "shiftActiveDetailsTest")
    public void conveyorHourMasterTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Conveyor Hour Master");
    }

    @Test(dependsOnMethods = "conveyorHourMasterTest")
    public void conveyorActiveHourReportTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Conveyor Active Hour Report");
    }

    @Test(dependsOnMethods = "conveyorActiveHourReportTest")
    public void conveyorLocationMasterTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Conveyor Location Master");
    }

    @Test(dependsOnMethods = "conveyorLocationMasterTest")
    public void modelLineBalanceTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Model Line Balance");
    }

    @Test(dependsOnMethods = "modelLineBalanceTest")
    public void stagewiseLeadtimeTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Stagewise Leadtime");
    }

    @Test(dependsOnMethods = "stagewiseLeadtimeTest")
    public void engineSkuConveyorPriorityTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Engine SKU Conveyor Priority");
    }

    @Test(dependsOnMethods = "engineSkuConveyorPriorityTest")
    public void modelLevelPrioritizationTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Model Level Prioritization");
    }

    @Test(dependsOnMethods = "modelLevelPrioritizationTest")
    public void variantLevelPrioritizationTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Variant Level Prioritization");
    }

    @Test(dependsOnMethods = "variantLevelPrioritizationTest")
    public void skuLevelPriorityTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("SKU Level Priority");
    }

    @Test(dependsOnMethods = "skuLevelPriorityTest")
    public void rejectionBufferTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Rejection Buffer");
    }

    @Test(dependsOnMethods = "rejectionBufferTest")
    public void jigMasterTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("JIG Master");
    }

    @Test(dependsOnMethods = "jigMasterTest")
    public void paintPartTypeJigAllocationTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Paint Part Type JIG Allocation");
    }

    @Test(dependsOnMethods = "paintPartTypeJigAllocationTest")
    public void jigDensityTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("JIG Density");
    }

    @Test(dependsOnMethods = "jigDensityTest")
    public void packingTypeTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Packing Type");
    }

    @Test(dependsOnMethods = "packingTypeTest")
    public void paintShopStockTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Paint Shop Stock");
    }

    @Test(dependsOnMethods = "paintShopStockTest")
    public void partVsPartGroupTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Part Vs Part Group");
    }

    @Test(dependsOnMethods = "partVsPartGroupTest")
    public void storageLocationMasterTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Storage Location Master");
    }

    @Test(dependsOnMethods = "storageLocationMasterTest")
    public void variantDayBatchCheckTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Variant Day Batch Check");
    }

    @Test(dependsOnMethods = "variantDayBatchCheckTest")
    public void conveyorCapacityCheckTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Conveyor Capacity Check");
    }

    @Test(dependsOnMethods = "conveyorCapacityCheckTest")
    public void maxDemandSkuCheckTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Max Demand SKU Check");
    }

    @Test(dependsOnMethods = "maxDemandSkuCheckTest")
    public void openWorkorderTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Open Workorder");
    }

    @Test(dependsOnMethods = "openWorkorderTest")
    public void resourceGroupingTest() throws InterruptedException {
        homePage.clickDataMaintenanceItem("Resource Grouping");
    }

}
