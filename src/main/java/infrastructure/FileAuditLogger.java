package infrastructure;

import application.LoanIssuedListener;
import domain.LoanReceipt;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.LocalDateTime;

/** Pure Fabrication: справді дописує рядок про видачу у файл журналу. */
public class FileAuditLogger implements LoanIssuedListener {
    private final Path logFile;
    private final Clock clock;

    public FileAuditLogger(Path logFile, Clock clock) {
        this.logFile = logFile;
        this.clock = clock;
    }

    @Override
    public void onLoanIssued(LoanReceipt receipt) {
        String line = LocalDateTime.now(clock) + " | видача #" + receipt.loanId()
                + " | книга #" + receipt.bookId() + " | читач #" + receipt.readerId() + System.lineSeparator();
        try {
            Files.writeString(logFile, line, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Не вдалося записати журнал " + logFile, e);
        }
    }
}
