package domain;

import domain.policy.LoanFee;
import domain.policy.LoanFeePolicy;
import domain.policy.LoanPolicies;

import java.math.BigDecimal;

/**
 * Категорія книги. Кожна константа несе власну політику (Polymorphism): каскаду if/else немає,
 * а нова категорія без політики просто не скомпілюється (Protected Variations).
 */
public enum BookCategory {
    STANDARD(LoanPolicies.STANDARD),
    RARE(LoanPolicies.RARE),
    ACADEMIC(LoanPolicies.ACADEMIC);

    private final LoanFeePolicy policy;

    BookCategory(LoanFeePolicy policy) {
        this.policy = policy;
    }

    /** Делегування політиці: зовні політику не видно (Закон Деметри). */
    public LoanFee feeFor(BigDecimal bookValue) {
        return policy.feeFor(bookValue);
    }

    public static BookCategory parse(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Категорія книги не вказана.");
        }
        for (BookCategory category : values()) {
            if (category.name().equalsIgnoreCase(text.trim())) {
                return category;
            }
        }
        throw new IllegalArgumentException("Невідома категорія книги: " + text.trim()
                + " (доступні: Standard, Rare, Academic).");
    }
}
