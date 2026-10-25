package application;

import domain.Book;
import domain.LoanReceipt;
import domain.Reader;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Application Service (Pure Fabrication): оркеструє сценарій «видати книгу» -
 * знаходить читача й книгу, делегує сутності бізнес-операцію, зберігає результат.
 * Усі знання про порти й сутності зосереджені тут, щоб контролер лишався тонким.
 */
public class LoanIssuer {
    public static final int DEFAULT_LOAN_DAYS = 14;

    private final ReaderRepository readers;
    private final BookCatalog catalog;
    private final Clock clock;

    public LoanIssuer(ReaderRepository readers, BookCatalog catalog, Clock clock) {
        this.readers = readers;
        this.catalog = catalog;
        this.clock = clock;
    }

    public LoanReceipt issue(IssueBookCommand command) {
        Reader reader = readers.findByName(command.readerName())
                .orElseThrow(() -> new IllegalArgumentException("Читача не знайдено: " + command.readerName()));
        Book book = catalog.findFirstByCategory(command.category())
                .orElseThrow(() -> new IllegalArgumentException("У каталозі немає книг категорії " + command.category()));

        LoanReceipt receipt = reader.borrow(readers.nextLoanId(), book, DEFAULT_LOAN_DAYS, LocalDate.now(clock));
        readers.save(reader);
        return receipt;
    }
}
