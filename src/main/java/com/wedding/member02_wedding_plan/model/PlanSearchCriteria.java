package com.wedding.member02_wedding_plan.model;

/**
 * Search, filter and sort options for the wedding plan list.
 * All setters normalise their input so the service never sees null values.
 *
 * @author Member 02
 */
public class PlanSearchCriteria {

    public static final String SORT_DATE_ASC = "date_asc";
    public static final String SORT_DATE_DESC = "date_desc";
    public static final String SORT_NAME = "name_asc";
    public static final String SORT_BUDGET = "budget_desc";
    public static final String SORT_NEWEST = "newest";

    private String keyword = "";
    private String status = "ALL";
    private String type = "ALL";
    private String fromDate = "";
    private String toDate = "";
    private String sortBy = SORT_DATE_ASC;

    public String getKeyword() { return keyword; }
    public String getStatus() { return status; }
    public String getType() { return type; }
    public String getFromDate() { return fromDate; }
    public String getToDate() { return toDate; }
    public String getSortBy() { return sortBy; }

    public void setKeyword(String keyword) { this.keyword = keyword == null ? "" : keyword.trim(); }

    public void setStatus(String status) {
        this.status = (status == null || status.trim().isEmpty()) ? "ALL" : status.trim().toUpperCase();
    }

    public void setType(String type) {
        this.type = (type == null || type.trim().isEmpty()) ? "ALL" : type.trim().toUpperCase();
    }

    public void setFromDate(String fromDate) { this.fromDate = fromDate == null ? "" : fromDate.trim(); }
    public void setToDate(String toDate) { this.toDate = toDate == null ? "" : toDate.trim(); }

    public void setSortBy(String sortBy) {
        if (sortBy == null) { this.sortBy = SORT_DATE_ASC; return; }
        switch (sortBy.trim()) {
            case SORT_DATE_DESC: case SORT_NAME: case SORT_BUDGET: case SORT_NEWEST:
                this.sortBy = sortBy.trim();
                break;
            default:
                this.sortBy = SORT_DATE_ASC;
        }
    }

    /** True when any filter (not just the sort order) is active. */
    public boolean isFiltering() {
        return !keyword.isEmpty() || !"ALL".equals(status) || !"ALL".equals(type)
                || !fromDate.isEmpty() || !toDate.isEmpty();
    }
}
