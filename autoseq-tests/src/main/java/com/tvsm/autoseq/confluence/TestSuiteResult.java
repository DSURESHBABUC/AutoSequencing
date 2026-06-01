package com.tvsm.autoseq.confluence;

import java.util.ArrayList;
import java.util.List;

/** Holds aggregated results for one TestNG &lt;test&gt; block. */
public class TestSuiteResult {

    public final String suiteName;
    public int passed  = 0;
    public int failed  = 0;
    public int skipped = 0;
    public final List<TestCaseResult> testCases = new ArrayList<>();

    public TestSuiteResult(String suiteName) {
        this.suiteName = suiteName;
    }

    public void add(TestCaseResult tc) {
        testCases.add(tc);
        switch (tc.status) {
            case "PASSED"  -> passed++;
            case "FAILED"  -> failed++;
            default        -> skipped++;
        }
    }
}
