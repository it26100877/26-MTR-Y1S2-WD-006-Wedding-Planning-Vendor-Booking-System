package com.wedding.member02_wedding_plan.repository;

import com.wedding.common.repository.AbstractFileRepository;
import com.wedding.member02_wedding_plan.model.PlanItem;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Shared base for the repositories of everything that belongs to a wedding plan
 * (checklist tasks, budget items, guests).
 *
 * Inheritance chain: AbstractFileRepository -> PlanItemRepository -> concrete repository.
 * This class implements getId() for every PlanItem and adds the queries all of them need;
 * each concrete repository only supplies serialize() and deserialize().
 *
 * @param <T> the PlanItem subtype stored in the file
 * @author Member 02
 */
public abstract class PlanItemRepository<T extends PlanItem> extends AbstractFileRepository<T> {

    protected static final String SEPARATOR = "|";
    protected static final String SAFE_SEP = "\\|"; // regex split

    protected PlanItemRepository(String filename) {
        super(filename);
    }

    @Override
    protected String getId(T item) {
        return item.getItemId();
    }

    /** Returns all items that belong to the given plan. */
    public List<T> findByPlanId(String planId) {
        return findAll().stream()
                .filter(item -> item.getPlanId() != null && item.getPlanId().equals(planId))
                .collect(Collectors.toList());
    }

    /** Saves many items and writes the file only once. */
    public synchronized void saveAll(Collection<T> items) {
        for (T item : items) {
            cache.put(getId(item), item);
        }
        persist();
    }

    /** Deletes every item of a plan and writes the file once; returns how many were removed. */
    public synchronized int deleteByPlanId(String planId) {
        int before = cache.size();
        cache.values().removeIf(item -> item.getPlanId() != null && item.getPlanId().equals(planId));
        int removed = before - cache.size();
        if (removed > 0) persist();
        return removed;
    }

    /** Keeps the file format safe: the separator and line breaks cannot appear in a value. */
    protected String safe(String value) {
        return value == null ? "" : value.replace("|", " ").replace("\n", " ").replace("\r", " ");
    }
}
