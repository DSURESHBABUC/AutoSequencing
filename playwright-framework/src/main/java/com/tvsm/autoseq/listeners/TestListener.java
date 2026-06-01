package com.tvsm.autoseq.listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestListener — wires into TestNG to print clean console output.
 * Extent logging is handled inside BaseTest.afterMethod().
 */
public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("▶  TEST STARTED : " + result.getMethod().getMethodName());
        System.out.println("══════════════════════════════════════════");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✅ TEST PASSED  : " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("❌ TEST FAILED  : " + result.getMethod().getMethodName());
        System.out.println("   Reason       : " + result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("⏭  TEST SKIPPED : " + result.getMethod().getMethodName());
    }
}
