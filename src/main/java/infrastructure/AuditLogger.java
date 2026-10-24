package infrastructure;

public interface AuditLogger {
    void logLoan(int bookId, int readerId);
}