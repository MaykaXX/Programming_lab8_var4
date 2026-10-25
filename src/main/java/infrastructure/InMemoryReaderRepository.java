package infrastructure;

import application.ReaderRepository;
import domain.Reader;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Pure Fabrication: сховище в пам'яті з демонстраційними читачами. Глобального стану немає. */
public final class InMemoryReaderRepository implements ReaderRepository {
    private static final int FIRST_LOAN_ID = 8801;

    private final Map<String, Reader> readersByName = new HashMap<>();

    public InMemoryReaderRepository() {
        add(Reader.register(7, "Іван Петренко", "T-8841", "reader@univ.edu", 2));
        add(Reader.register(8, "Марія Мельник", "T-9102", "maria@univ.edu", 5));
        add(Reader.register(9, "Олег Бондар", "T-9377", "oleg@univ.edu", 0));
    }

    private void add(Reader reader) {
        readersByName.put(reader.getName(), reader);
    }

    @Override
    public Optional<Reader> findByName(String name) {
        return name == null ? Optional.empty() : Optional.ofNullable(readersByName.get(name.trim()));
    }

    @Override
    public void save(Reader reader) {
        readersByName.put(reader.getName(), reader);
    }

    /** Номер наступної видачі = перший номер + кількість видач, уже збережених у сховищі. */
    @Override
    public int nextLoanId() {
        int issued = readersByName.values().stream().mapToInt(reader -> reader.getLoans().size()).sum();
        return FIRST_LOAN_ID + issued;
    }
}
