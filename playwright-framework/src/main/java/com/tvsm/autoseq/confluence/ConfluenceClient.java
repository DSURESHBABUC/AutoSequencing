package com.tvsm.autoseq.confluence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tvsm.autoseq.config.ConfigReader;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * ConfluenceClient — thin wrapper around the Confluence REST API v2.
 *
 * Supports:
 *  - Finding a page by title in a space
 *  - Creating a new page
 *  - Updating an existing page (increments version)
 *
 * Authentication: HTTP Basic with email + API token.
 */
public class ConfluenceClient {

    private final String baseUrl;
    private final String authHeader;
    private final ObjectMapper mapper = new ObjectMapper();

    public ConfluenceClient() {
        this.baseUrl = ConfigReader.confluenceBaseUrl().replaceAll("/$", "");
        String credentials = ConfigReader.confluenceUsername()
                + ":" + ConfigReader.confluenceApiToken();
        this.authHeader = "Basic " + Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    // ── Public API ────────────────────────────────────────────────────────

    /**
     * Publishes the HTML content to Confluence.
     * Creates the page if it doesn't exist; updates it if it does.
     *
     * @param spaceKey   Confluence space key e.g. "QA"
     * @param title      Page title
     * @param parentId   Parent page ID (empty string = root of space)
     * @param htmlBody   Storage-format HTML content
     */
    public void publishPage(String spaceKey, String title,
                            String parentId, String htmlBody) throws Exception {

        String existingPageId = findPageId(spaceKey, title);

        if (existingPageId == null) {
            createPage(spaceKey, title, parentId, htmlBody);
            System.out.println("📄 Confluence page created: " + title);
        } else {
            int currentVersion = getPageVersion(existingPageId);
            updatePage(existingPageId, title, htmlBody, currentVersion + 1);
            System.out.println("📄 Confluence page updated: " + title
                    + " (version " + (currentVersion + 1) + ")");
        }
    }

    // ── Internal helpers ──────────────────────────────────────────────────

    /** Returns the page ID if a page with the given title exists in the space, else null. */
    private String findPageId(String spaceKey, String title) throws Exception {
        String encodedTitle = java.net.URLEncoder.encode(title, StandardCharsets.UTF_8);
        String url = baseUrl + "/rest/api/content?spaceKey=" + spaceKey
                + "&title=" + encodedTitle + "&expand=version";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", authHeader);
            get.setHeader("Accept", "application/json");

            return client.execute(get, response -> {
                String body = new String(response.getEntity().getContent().readAllBytes(),
                        StandardCharsets.UTF_8);
                JsonNode root = mapper.readTree(body);
                JsonNode results = root.path("results");
                if (results.isArray() && results.size() > 0) {
                    return results.get(0).path("id").asText();
                }
                return null;
            });
        }
    }

    /** Returns the current version number of a page. */
    private int getPageVersion(String pageId) throws Exception {
        String url = baseUrl + "/rest/api/content/" + pageId + "?expand=version";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", authHeader);
            get.setHeader("Accept", "application/json");

            return client.execute(get, response -> {
                String body = new String(response.getEntity().getContent().readAllBytes(),
                        StandardCharsets.UTF_8);
                JsonNode root = mapper.readTree(body);
                return root.path("version").path("number").asInt(1);
            });
        }
    }

    /** Creates a new Confluence page. */
    private void createPage(String spaceKey, String title,
                            String parentId, String htmlBody) throws Exception {
        String url = baseUrl + "/rest/api/content";

        ObjectNode payload = mapper.createObjectNode();
        payload.put("type", "page");
        payload.put("title", title);

        ObjectNode space = mapper.createObjectNode();
        space.put("key", spaceKey);
        payload.set("space", space);

        // Set parent page if provided
        if (parentId != null && !parentId.isBlank()) {
            ObjectNode ancestors = mapper.createObjectNode();
            ancestors.put("id", parentId);
            payload.putArray("ancestors").add(ancestors);
        }

        ObjectNode body = mapper.createObjectNode();
        ObjectNode storage = mapper.createObjectNode();
        storage.put("value", htmlBody);
        storage.put("representation", "storage");
        body.set("storage", storage);
        payload.set("body", body);

        postOrPut("POST", url, payload.toString());
    }

    /** Updates an existing Confluence page to a new version. */
    private void updatePage(String pageId, String title,
                            String htmlBody, int newVersion) throws Exception {
        String url = baseUrl + "/rest/api/content/" + pageId;

        ObjectNode payload = mapper.createObjectNode();
        payload.put("type", "page");
        payload.put("title", title);

        ObjectNode version = mapper.createObjectNode();
        version.put("number", newVersion);
        payload.set("version", version);

        ObjectNode body = mapper.createObjectNode();
        ObjectNode storage = mapper.createObjectNode();
        storage.put("value", htmlBody);
        storage.put("representation", "storage");
        body.set("storage", storage);
        payload.set("body", body);

        postOrPut("PUT", url, payload.toString());
    }

    private void postOrPut(String method, String url, String json) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            if ("POST".equals(method)) {
                HttpPost req = new HttpPost(url);
                req.setHeader("Authorization", authHeader);
                req.setHeader("Content-Type", "application/json");
                req.setHeader("Accept", "application/json");
                req.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
                client.execute(req, response -> {
                    int status = response.getCode();
                    if (status < 200 || status >= 300) {
                        String body = new String(response.getEntity().getContent().readAllBytes(),
                                StandardCharsets.UTF_8);
                        throw new RuntimeException("Confluence POST failed [" + status + "]: " + body);
                    }
                    return null;
                });
            } else {
                HttpPut req = new HttpPut(url);
                req.setHeader("Authorization", authHeader);
                req.setHeader("Content-Type", "application/json");
                req.setHeader("Accept", "application/json");
                req.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
                client.execute(req, response -> {
                    int status = response.getCode();
                    if (status < 200 || status >= 300) {
                        String body = new String(response.getEntity().getContent().readAllBytes(),
                                StandardCharsets.UTF_8);
                        throw new RuntimeException("Confluence PUT failed [" + status + "]: " + body);
                    }
                    return null;
                });
            }
        }
    }
}
