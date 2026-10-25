package domain;

import domain.policy.LoanFee;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Видача книги. Створюється лише всередині домену (метод issue видимий тільки в пакеті). */
public final class Loan {
    private final int loanId;
    private final Reader reader;
    private final Book book;
    private final int days;
    private final LocalDate issuedOn;
    private final LoanFee fee;

    private Loan(int loanId, Reader reader, Book book, int days, LocalDate issuedOn) {
        this.loanId = loanId;
        this.reader = reader;
        this.book = book;
        this.days = days;
        this.issuedOn = issuedOn;
        this.fee = book.fee();
    }

    static Loan issue(int loanId, Reader reader, Book book, int days, LocalDate issuedOn) {
        if (reader == null || book == null || issuedOn == null) {
            throw new IllegalArgumentException("Читач, книга і дата видачі обов'язкові.");
        }
        if (days <= 0) {
            throw new IllegalArgumentException("Термін видачі має бути додатним.");
        }
        return new Loan(loanId, reader, book, days, issuedOn);
    }

    /** Information Expert: дата повернення залежить від власних даних видачі. */
    public LocalDate dueDate() {
        return issuedOn.plusDays(days);
    }

    public BigDecimal totalPayable() {
        return fee.total();
    }

    public LoanReceipt toReceipt() {
        return new LoanReceipt(
                loanId,
                reader.getId(), reader.getName(), reader.getTicketNumber(), reader.getEmail(),
                book.getId(), book.getTitle(),
                fee.deposit(), fee.serviceFee(), totalPayable(), dueDate());
    }
}
