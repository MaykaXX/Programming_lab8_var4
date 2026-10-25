package application;

import domain.BookCategory;

/** Команда від UI: ім'я читача й розібрана категорія. Бізнес-логіки немає. */
public record IssueBookCommand(String readerName, BookCategory category) {
}
