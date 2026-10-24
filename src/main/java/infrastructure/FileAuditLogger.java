package infrastructure;

import application.LoanIssuedListener;
import domain.LoanReceipt;

public class FileAuditLogger implements LoanIssuedListener {
    @Override
    public void onLoanIssued(LoanReceipt receipt) {
        System.out.println("[Журнал логів] Видачу книги #" + receipt.bookId() +
                " читачу #" + receipt.readerId() + " записано в library_audit.log.");
    }
}