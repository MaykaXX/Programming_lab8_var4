package domain;

import policy.LoanFeePolicy;
import policy.LoanPolicyFactory;

import java.math.BigDecimal;

/** Книга (Rich Domain Model): сама знає, скільки коштує її видача. */
public final class Book {
    private final int id;
    private final String title;
    private final BigDecimal valueAmount;
    private final LoanFeePolicy policy;

    private Book(int id, String title, BigDecimal valueAmount, LoanFeePolicy policy) {
        this.id = id;
        this.title = title;
        this.valueAmount = valueAmount;
        this.policy = policy;
    }

    /** Static Factory Method (Creator): об'єкт одразу у валідному стані. */
    public static Book of(int id, String title, BookCategory category, BigDecimal valueAmount) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Назва книги не може бути порожньою.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Категорія книги обов'язкова.");
        }
        if (valueAmount == null || valueAmount.signum() <= 0) {
            throw new IllegalArgumentException("Вартість книги має бути додатною.");
        }
        return new Book(id, title, valueAmount, LoanPolicyFactory.getPolicy(category.name()));
    }

    /** Information Expert: вартість застави знає той, хто володіє вартістю й політикою. */
    public BigDecimal depositFee() {
        return policy.calculateDeposit(valueAmount);
    }

    public BigDecimal serviceFee() {
        return policy.calculateServiceFee();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}