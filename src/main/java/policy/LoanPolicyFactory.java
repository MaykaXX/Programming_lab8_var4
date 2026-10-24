package policy;

import java.util.Map;

public class LoanPolicyFactory {
    private static final Map<String, LoanFeePolicy> POLICIES = Map.of(
            "standard", new StandardLoanPolicy(),
            "academic", new AcademicLoanPolicy(),
            "rare", new RareLoanPolicy()
    );

    public static LoanFeePolicy getPolicy(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Категорія книги не може бути порожньою.");
        }
        LoanFeePolicy policy = POLICIES.get(category.trim().toLowerCase());
        if (policy == null) {
            throw new IllegalArgumentException("Невідома категорія книги: " + category);
        }
        return policy;
    }
}