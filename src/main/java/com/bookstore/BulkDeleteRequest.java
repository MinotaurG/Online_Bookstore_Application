package com.bookstore;

import java.util.List;

/**
 * Request for bulk delete operations
 * Can delete by IDs or ASINs
 */
public class BulkDeleteRequest {
    private List<String> ids;
    private List<String> asins;

    public List<String> getIds() { return ids; }
    public void setIds(List<String> ids) { this.ids = ids; }

    public List<String> getAsins() { return asins; }
    public void setAsins(List<String> asins) { this.asins = asins; }
}