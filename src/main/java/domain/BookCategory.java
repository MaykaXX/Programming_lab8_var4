package domain;

/** Категорія книги. Сама вміє розпізнати себе з тексту (Information Expert для власних імен). */
public enum BookCategory {
    STANDARD, RARE, ACADEMIC;

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