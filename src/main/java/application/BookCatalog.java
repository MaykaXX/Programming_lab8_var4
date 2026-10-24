package application;

import domain.Book;
import domain.BookCategory;

import java.util.Optional;

public interface BookCatalog {
    Optional<Book> findFirstByCategory(BookCategory category);
}