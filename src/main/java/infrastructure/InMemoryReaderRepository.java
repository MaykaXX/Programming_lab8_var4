package infrastructure;

import application.ReaderRepository;
import domain.Reader;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Pure Fabrication: сховище в пам'яті з демонстраційними читачами. */
public class InMemoryReaderRepository implements ReaderRepository {
    private final Map<String, Reader> readersByName = new HashMap<>();

    public InMemoryReaderRepository() {
        save(Reader.register(7, "Іван Петренко", "T-8841", "reader@univ.edu", 2));
        save(Reader.register(8, "Марія Мельник", "T-9102", "maria@univ.edu", 5));
        save(Reader.register(9, "Олег Бондар", "T-9377", "oleg@univ.edu", 0));
    }

    @Override
    public Optional<Reader> findByName(String name) {
        return Optional.ofNullable(readersByName.get(name == null ? null : name.trim()));
    }

    @Override
    public void save(Reader reader) {
        readersByName.put(reader.getName(), reader);
    }
}