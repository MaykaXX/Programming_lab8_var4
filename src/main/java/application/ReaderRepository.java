package application;

import domain.Reader;

import java.util.Optional;

/* Indirection: контролер не знає, де й як зберігаються читачі. */
public interface ReaderRepository {
    Optional<Reader> findByName(String name);

    void save(Reader reader);
}
