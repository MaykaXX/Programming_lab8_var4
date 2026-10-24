package infrastructure;

public class FileAuditLogger implements AuditLogger {
    @Override
    public void logLoan(int bookId, int readerId) {
        System.out.println("[Журнал логів] Видачу книги #" + bookId +
                " читачу #" + readerId + " записано в library_audit.log.");
    }
}