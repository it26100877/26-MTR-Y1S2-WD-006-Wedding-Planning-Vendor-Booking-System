# Member 02 — Wedding Planning

**Responsibility:** wedding plans and the planning information used to organise an event.

## Main classes
- `Plannable`: common plan behaviour interface.
- `WeddingPlan`: abstract wedding-plan base.
- `BeachWedding`, `GardenWedding`, `HotelWedding`, `TraditionalWedding`, `StandardWedding`: concrete plan variants.
- `PlanItem`: abstract base for data associated with a plan.
- `ChecklistTask`, `BudgetItem`, `Guest`: plan item specialisations.
- `WeddingPlanFactory`: construction of plan instances.
- `WeddingPlanService`, `ChecklistService`, `BudgetService`, `GuestService`: business operations and corresponding repositories.

## Main relationships and OOP
- Each concrete wedding type inherits from `WeddingPlan`; the base implements `Plannable`.
- Checklist tasks, budget items and guests inherit from `PlanItem`.
- A wedding plan groups multiple plan items.
- Overridden operations such as estimated-cost calculation demonstrate polymorphism.

## Class diagram
![Member 02 — Wedding Planning UML](diagrams/02-wedding-planning-class-diagram.png)
