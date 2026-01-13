package com.nlu.store.core.jawire;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.PageRequest;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base class phân trang Generic.
 * Sử dụng thuật ngữ 'limit' thay cho 'pageSize'.
 *
 * @param <T> Kiểu dữ liệu của danh sách (User, Product, ...).
 */
public abstract class Pagination<T> extends Component implements Iterable<T> {

    // --- STATE ---
    @Model
    private int page = 1;

    @Model
    private int limit = 10;
    @Model
    private String sort;
    @Model
    private long totalItems = 0;


    @JsonIgnore
    private List<T> data = new ArrayList<>();

    // --- ABSTRACT ---
    public abstract Page<T> getPage(Pageable pageable);

    // --- LIFECYCLE ---
    @Override
    public void mount() {
        if (this.page < 1) this.page = 1;
        Pageable pageable = getHttpContext().getPageable();
        this.page = pageable.getPage();
        this.sort = pageable.getSort().isUnsorted() ? defaultSort().toString() : pageable.getSort().toString();
        this.limit = pageable.getLimit();
    }

    // --- GETTERS & SETTERS (Data) ---

    public List<T> getData() {
        return data != null ? data : Collections.emptyList();
    }


    // --- GETTERS & SETTERS (Config) ---

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(1, page);
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = (limit < 1) ? 10 : limit;
    }

    public long getTotalItems() {
        return totalItems;
    }


    // --- ACTIONS ---

    @Action
    public void gotoPage(int pageNumber) {
        int maxPage = getTotalPages();
        if (pageNumber < 1) pageNumber = 1;
        if (maxPage > 0 && pageNumber > maxPage) pageNumber = maxPage;

        this.page = pageNumber;
    }

    @Action
    public void previousPage() {
        if (hasPrevious()) gotoPage(this.page - 1);
    }

    @Action
    public void nextPage() {
        if (hasNext()) gotoPage(this.page + 1);
    }

    @Action
    public void resetPage() {
        this.page = 1;
    }

    /**
     * Thay đổi số lượng dòng hiển thị và reset về trang 1.
     * Dùng cho Dropdown "Show 10/20/50 items".
     */
    @Action
    public void changeLimit(int newLimit) {
        this.setLimit(newLimit);
        this.resetPage();
    }

    // --- HELPERS ---

    public int getTotalPages() {
        if (limit <= 0 || totalItems == 0) return 1;
        return (int) Math.ceil((double) totalItems / limit);
    }

    /**
     * Tính OFFSET cho SQL.
     * VD: LIMIT 10 OFFSET 20
     */
    public int getOffset() {
        return (page - 1) * limit;
    }

    public boolean hasPrevious() {
        return page > 1;
    }

    public boolean hasNext() {
        return page < getTotalPages();
    }

    public List<Integer> getVisiblePages(int range) {
        List<Integer> pages = new ArrayList<>();
        int total = getTotalPages();
        int start = Math.max(1, page - range / 2);
        int end = Math.min(total, start + range - 1);

        if (end - start + 1 < range) {
            start = Math.max(1, end - range + 1);
        }

        for (int i = start; i <= end; i++) {
            pages.add(i);
        }
        return pages;
    }

    // --- ITERABLE IMPLEMENTATION ---

    /**
     * Cho phép dùng for-each trực tiếp trên instance của Pagination.
     * VD: for (User u : userTable) { ... }
     */
    @Override
    public Iterator<T> iterator() {
        return getData().iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        getData().forEach(action);
    }

    @Override
    public void rendering() {
        super.rendering();
        Page<T> pageData = this.getPage(new PageRequest(page, limit, Sort.parse(sort)));
        if (pageData == null){
            data = Collections.emptyList();
            totalItems = 0;
            page = 1;
        } else {
            totalItems = pageData.totalElements();
            data = pageData.getContent();
            page = pageData.getPage();
        }

    }

    protected Sort defaultSort() {
        return Sort.UNSORTED;
    }

    @Override
    protected void clear() {
        data = Collections.emptyList();

    }
}
