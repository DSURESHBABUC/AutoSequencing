package com.tvsm.autoseq.confluence;

/** Holds the result of a single @Test method. */
public class TestCaseResult {

    public final String testName;
    public final String status;          // PASSED | FAILED | SKIPPED
    public final long   durationMs;
    public final String failureReason;   // null when passed/skipped

    public TestCaseResult(String testName, String status,
                          long durationMs, String failureReason) {
        this.testName      = testName;
        this.status        = status;
        this.durationMs    = durationMs;
        this.failureReason = failureReason;
    }
}
