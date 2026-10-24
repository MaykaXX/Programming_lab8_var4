package infrastructure;

import application.LoanIssuedListener;
import domain.LoanReceipt;

public class EmailNotificationService implements LoanIssuedListener {
    @Override
    public void onLoanIssued(LoanReceipt receipt) {
        System.out.println("[Email] Нагадування надіслано на " + receipt.readerEmail() +
                " (повернути до " + receipt.dueDate() + ")");
    }
}