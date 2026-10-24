package domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

/* Видача книги. Створюється лише всередині домену (метод issue видимий тільки в пакеті). */
public final class Loan {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(8801);

    private final int loanId;
    private final Reader reader;
    private final Book book;
    private final int days;
    private final LocalDate issuedOn;
    private final BigDecimal depositFee;
    private final BigDecimal serviceFee;

    private Loan(Reader reader, Book book, int days, LocalDate issuedOn) {
        this.loanId = NEXT_ID.getAndIncrement();
        this.reader = reader;
        this.book = book;
        this.days = days;
        this.issuedOn = issuedOn;
        this.depositFee = book.depositFee();
        this.serviceFee = book.serviceFee();
    }

    static Loan issue(Reader reader, Book book, int days, LocalDate issuedOn) {
        if (reader == null || book == null || issuedOn == null) {
            throw new IllegalArgumentException("Читач, книга і дата видачі обов'язкові.");
        }
        if (days <= 0) {
            throw new IllegalArgumentException("Термін видачі має бути додатним.");
        }
        return new Loan(reader, book, days, issuedOn);
    }

    /** Information Expert: дата повернення залежить від власних даних видачі. */
    public LocalDate dueDate() {
        return issuedOn.plusDays(days);
    }

    public BigDecimal totalPayable() {
        return depositFee.add(serviceFee);
    }

    public LoanReceipt toReceipt() {
        return new LoanReceipt(
                loanId,
                reader.getId(), reader.getName(), reader.getTicketNumber(), reader.getEmail(),
                book.getId(), book.getTitle(),
                depositFee, serviceFee, totalPayable(), dueDate());
    }
}