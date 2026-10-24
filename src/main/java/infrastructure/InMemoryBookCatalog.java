package infrastructure;

import application.BookCatalog;
import domain.Book;
import domain.BookCategory;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryBookCatalog implements BookCatalog {
    private final Map<BookCategory, Book> booksByCategory = new EnumMap<>(BookCategory.class);

    public InMemoryBookCatalog() {
        booksByCategory.put(BookCategory.STANDARD,
                Book.of(101, "Тіні забутих предків", BookCategory.STANDARD, new BigDecimal("1500.00")));
        booksByCategory.put(BookCategory.RARE,
                Book.of(102, "Кобзар (прижиттєве видання)", BookCategory.RARE, new BigDecimal("1500.00")));
        booksByCategory.put(BookCategory.ACADEMIC,
                Book.of(104, "Алгоритми та структури даних", BookCategory.ACADEMIC, new BigDecimal("1500.00")));
    }

    @Override
    public Optional<Book> findFirstByCategory(BookCategory category) {
        return Optional.ofNullable(booksByCategory.get(category));
    }
}