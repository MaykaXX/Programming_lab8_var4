package application;

import domain.Book;
import domain.BookCategory;
import domain.Loan;
import domain.LoanReceipt;
import domain.Reader;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Use-Case Controller (GRASP Controller): перший об'єкт після UI.
 * Завантажує сутності, делегує їм бізнес-операцію, зберігає, сповіщає.
 * Не містить ні консольного вводу/виводу, ні обчислень.
 */
public class IssueBookController {
    public static final int DEFAULT_LOAN_DAYS = 14;

    private final ReaderRepository readers;
    private final BookCatalog catalog;
    private final List<LoanIssuedListener> listeners;
    private final Clock clock;

    public IssueBookController(ReaderRepository readers, BookCatalog catalog,
                               List<LoanIssuedListener> listeners, Clock clock) {
        this.readers = readers;
        this.catalog = catalog;
        this.listeners = List.copyOf(listeners);
        this.clock = clock;
    }

    public LoanReceipt issueBook(IssueBookCommand command) {
        BookCategory category = BookCategory.parse(command.categoryText());
        Reader reader = readers.findByName(command.readerName())
                .orElseThrow(() -> new IllegalArgumentException("Читача не знайдено: " + command.readerName()));
        Book book = catalog.findFirstByCategory(category)
                .orElseThrow(() -> new IllegalArgumentException("У каталозі немає книг категорії " + category));

        Loan loan = reader.borrow(book, DEFAULT_LOAN_DAYS, LocalDate.now(clock));
        readers.save(reader);

        LoanReceipt receipt = loan.toReceipt();
        listeners.forEach(listener -> listener.onLoanIssued(receipt));
        return receipt;
    }
}