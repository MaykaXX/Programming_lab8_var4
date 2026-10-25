package domain;

import domain.policy.LoanFee;

import java.math.BigDecimal;

/** Книга (Rich Domain Model, Information Expert): сама знає, скільки коштує її видача. */
public final class Book {
    private final int id;
    private final String title;
    private final BookCategory category;
    private final BigDecimal valueAmount;

    private Book(int id, String title, BookCategory category, BigDecimal valueAmount) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.valueAmount = valueAmount;
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
        return new Book(id, title, category, valueAmount);
    }

    /** Information Expert: вартість видачі знає той, хто володіє вартістю й категорією. */
    public LoanFee fee() {
        return category.feeFor(valueAmount);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BookCategory getCategory() {
        return category;
    }
}
