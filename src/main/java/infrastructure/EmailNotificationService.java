package infrastructure;

import application.LoanIssuedListener;
import domain.LoanReceipt;

import java.io.PrintStream;

/** Pure Fabrication: імітація надсилання email-нагадування (вивід у переданий потік). */
public class EmailNotificationService implements LoanIssuedListener {
    private final PrintStream out;

    public EmailNotificationService(PrintStream out) {
        this.out = out;
    }

    @Override
    public void onLoanIssued(LoanReceipt receipt) {
        out.println("[Email] Нагадування надіслано на " + receipt.readerEmail()
                + " (повернути до " + receipt.dueDate() + ")");
    }
}
