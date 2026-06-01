package com.tvsm.autoseq.pages.eyeq;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tvsm.autoseq.base.BasePage;
import com.tvsm.autoseq.config.ConfigReader;

import java.util.ArrayList;
import java.util.List;

/**
 * KnowledgeGraphPage — Page Object for the Knowledge Graph 2.0 screen.
 * Application: https://uat-eyeq.tvsmotor.net/knowledgegraph2.0
 *
 * Covers:
 *  - Page load and URL verification
 *  - Graph canvas / SVG visualisation (circle nodes, line edges)
 *  - Search / filter functionality
 *  - Sidebar / detail panel
 *  - Toolbar actions (zoom in, zoom out, reset)
 *  - Legend / category panel
 *  - Navigation menu items
 */
public class KnowledgeGraphPage extends BasePage {

    // ── Graph canvas ──────────────────────────────────────────────────────
    private static final String GRAPH_CANVAS  = "svg";
    private static final String GRAPH_NODES   = "circle";  // SVG circles = graph nodes
    private static final String GRAPH_EDGES   = "line";    // SVG lines   = graph edges

    // ── Search / filter bar ───────────────────────────────────────────────
    // Exact selector confirmed via DOM inspection
    private static final String SEARCH_INPUT  = "input.autocomplete-input, "
                                              + "input[placeholder='Search Failure...'], "
                                              + "input[placeholder*='Search' i]";

    // ── Sidebar ───────────────────────────────────────────────────────────
    private static final String SIDEBAR_PANEL = ".sidebar-cont, .sidebar, [class*='sidebar']";
    private static final String SIDEBAR_TITLE = ".sidebar-title, .panel-title, h2, h3";
    private static final String SIDEBAR_CLOSE = "button[aria-label*='close' i], .close-panel";

    // ── Toolbar / top bar ─────────────────────────────────────────────────
    // Actual DOM classes: .topBar_m, .topBar_header
    private static final String TOOLBAR       = ".topBar_m, .topBar_header, [class*='topBar']";
    private static final String ZOOM_IN_BTN   = "button[aria-label*='zoom in' i], button[title*='zoom in' i], .zoom-in";
    private static final String ZOOM_OUT_BTN  = "button[aria-label*='zoom out' i], button[title*='zoom out' i], .zoom-out";
    private static final String RESET_BTN     = "button[aria-label*='reset' i], button[title*='reset' i], button:has-text('Reset')";

    // ── Legend ────────────────────────────────────────────────────────────
    private static final String LEGEND_PANEL  = ".legend, .category-legend, [class*='legend']";
    private static final String LEGEND_ITEMS  = ".legend-item, .legend li, [class*='legend-item']";

    // ── Loading ───────────────────────────────────────────────────────────
    private static final String LOADING       = "mat-spinner, .spinner, [class*='loading'], [class*='spinner']";

    // ── Navigation menu ───────────────────────────────────────────────────
    // Actual DOM class: .menu_item
    private static final String NAV_TABS      = ".menu_item, .mat-tab-label, [role='tab'], .nav-tab";

    public KnowledgeGraphPage(Page page) {
        super(page);
    }

    // ── Navigation ────────────────────────────────────────────────────────

    public KnowledgeGraphPage navigateTo() {
        page.navigate(ConfigReader.knowledgeGraphUrl());
        page.waitForLoadState();
        System.out.println("🌐 Navigated to Knowledge Graph 2.0");
        return this;
    }

    // ── Page state ────────────────────────────────────────────────────────

    public String getPageUrl()   { return getCurrentUrl(); }
    public String getPageTitle() { return getTitle(); }

    public boolean isGraphCanvasVisible() {
        if (page.locator("svg").count() > 0 && page.locator("circle").count() > 0) {
            System.out.println("🗺️  Graph: SVG with circle nodes");
            return true;
        }
        if (page.locator("svg").count() > 0) {
            System.out.println("🗺️  Graph: SVG element");
            return true;
        }
        if (page.locator("canvas").count() > 0) {
            System.out.println("🗺️  Graph: <canvas>");
            return true;
        }
        return false;
    }

    public KnowledgeGraphPage waitForGraphToLoad() {
        try { page.locator(LOADING).first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(timeout)); } catch (Exception ignored) {}
        try { page.locator("svg").first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout)); } catch (Exception ignored) {}
        try {
            page.locator("circle").first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
            System.out.println("✅ Graph nodes (circles) rendered");
        } catch (Exception ignored) { System.out.println("ℹ️  No circle nodes found"); }
        try {
            page.locator("input.autocomplete-input").first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(20000));
            System.out.println("✅ Search input ready");
        } catch (Exception ignored) { System.out.println("ℹ️  Search input not yet visible after 20s"); }
        System.out.println("✅ Graph load wait complete");
        return this;
    }

    public boolean isPageFullyLoaded() {
        return page.locator(LOADING).count() == 0 && isGraphCanvasVisible();
    }

    // ── Graph counts ──────────────────────────────────────────────────────

    public int getNodeCount() {
        int c = page.locator("circle").count();
        System.out.println("🔵 SVG circle nodes: " + c);
        return c;
    }

    public int getEdgeCount() {
        int c = page.locator("line").count();
        System.out.println("🔗 SVG line edges: " + c);
        return c;
    }

    // ── Search ────────────────────────────────────────────────────────────

    public boolean isSearchInputVisible() {
        if (page.locator("input.autocomplete-input").count() > 0
                && page.locator("input.autocomplete-input").first().isVisible()) {
            System.out.println("🔍 Search input: input.autocomplete-input");
            return true;
        }
        for (String sel : new String[]{"input[placeholder='Search Failure...']", "input[placeholder*='Search' i]"}) {
            if (page.locator(sel).count() > 0 && page.locator(sel).first().isVisible()) {
                System.out.println("🔍 Search input (fallback): " + sel);
                return true;
            }
        }
        return false;
    }

    public KnowledgeGraphPage search(String keyword) {
        Locator input = resolveSearchInput();
        input.click();
        input.fill(keyword);
        page.waitForTimeout(1500);
        System.out.println("🔍 Searched: " + keyword);
        return this;
    }

    public KnowledgeGraphPage clearSearch() {
        resolveSearchInput().clear();
        page.waitForTimeout(1000);
        System.out.println("🔍 Search cleared");
        return this;
    }

    public String getSearchValue() {
        try { return resolveSearchInput().inputValue().trim(); }
        catch (Exception e) { return ""; }
    }

    private Locator resolveSearchInput() {
        Locator primary = page.locator("input.autocomplete-input").first();
        try {
            primary.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
            return primary;
        } catch (Exception ignored) {}
        for (String sel : new String[]{"input[placeholder='Search Failure...']", "input[placeholder*='Search' i]", "input[type='search']"}) {
            Locator loc = page.locator(sel).first();
            try {
                loc.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                return loc;
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("No search input found on Knowledge Graph page");
    }

    // ── Toolbar ───────────────────────────────────────────────────────────

    public boolean isToolbarVisible()  { return page.locator(TOOLBAR).count() > 0; }
    public boolean clickZoomIn()       { return clickIfPresent(ZOOM_IN_BTN,  "Zoom In"); }
    public boolean clickZoomOut()      { return clickIfPresent(ZOOM_OUT_BTN, "Zoom Out"); }
    public boolean clickResetView()    { return clickIfPresent(RESET_BTN,    "Reset View"); }

    // ── Legend ────────────────────────────────────────────────────────────

    public boolean isLegendVisible()   { return page.locator(LEGEND_PANEL).count() > 0; }
    public int getLegendItemCount()    { return page.locator(LEGEND_ITEMS).count(); }

    public List<String> getLegendLabels() {
        List<String> labels = new ArrayList<>();
        Locator items = page.locator(LEGEND_ITEMS);
        for (int i = 0; i < items.count(); i++) {
            try { labels.add(items.nth(i).innerText().trim()); } catch (Exception e) { labels.add(""); }
        }
        return labels;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────

    public boolean isSidebarVisible() {
        return page.locator(SIDEBAR_PANEL).count() > 0 && page.locator(SIDEBAR_PANEL).first().isVisible();
    }

    public String getSidebarTitle() {
        try { return page.locator(SIDEBAR_TITLE).first().innerText().trim(); }
        catch (Exception e) { return ""; }
    }

    // ── Navigation tabs ───────────────────────────────────────────────────

    public int getNavTabCount() { return page.locator(NAV_TABS).count(); }

    public List<String> getNavTabLabels() {
        List<String> labels = new ArrayList<>();
        Locator tabs = page.locator(NAV_TABS);
        for (int i = 0; i < tabs.count(); i++) {
            try { labels.add(tabs.nth(i).innerText().trim()); } catch (Exception e) { labels.add(""); }
        }
        return labels;
    }

    // ── Utility ───────────────────────────────────────────────────────────

    private boolean clickIfPresent(String selector, String name) {
        if (page.locator(selector).count() > 0) {
            safeClick(selector);
            page.waitForTimeout(500);
            System.out.println("✅ Clicked: " + name);
            return true;
        }
        System.out.println("ℹ️  Not found (skipped): " + name);
        return false;
    }

    public boolean isElementPresent(String selector) { return page.locator(selector).count() > 0; }
}
