package domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Незмінна квитанція (DTO): усе, що потрібно зовнішнім шарам, без доступу до внутрішніх об'єктів. */
public record LoanReceipt(
        int loanId,
        int readerId,
        String readerName,
        String ticketNumber,
        String readerEmail,
        int bookId,
        String bookTitle,
        BigDecimal depositFee,
        BigDecimal serviceFee,
        BigDecimal totalPayable,
        LocalDate dueDate) {
}
