package lk.weddingly.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WeddingPlan extends WeddingRecord {
    public WeddingPlan(String id, String coupleNames, LocalDate weddingDate, String venue,
                       int guestCount, BigDecimal budget, String theme, String notes) {
        super(id, coupleNames, weddingDate, venue, guestCount, budget, theme, notes);
    }

    @Override
    public String getPlanLabel() { return "Wedding Plan"; }
}
