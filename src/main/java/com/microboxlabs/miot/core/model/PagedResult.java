package com.microboxlabs.miot.core.model;

import java.util.Collections;
import java.util.List;

/**
 * Generic pagination wrapper for list results.
 *
 * @param <T> the type of items in the page
 */
public final class PagedResult<T> {

    private final List<T> items;
    private final long totalItems;
    private final int page;
    private final int pageSize;

    public PagedResult(List<T> items, long totalItems, int page, int pageSize) {
        this.items = items != null ? Collections.unmodifiableList(items) : Collections.emptyList();
        this.totalItems = totalItems;
        this.page = page;
        this.pageSize = pageSize;
    }

    public List<T> getItems() {
        return items;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean hasMore() {
        return (long) page * pageSize + items.size() < totalItems;
    }
}
