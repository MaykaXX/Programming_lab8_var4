package application;

import domain.Book;
import domain.BookCategory;

import java.util.Optional;

/** Indirection: застосунок не знає, звідки беруться книги. */
public interface BookCatalog {
    Optional<Book> findFirstByCategory(BookCategory category);
}
