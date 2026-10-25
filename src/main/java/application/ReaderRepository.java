package application;

import domain.Reader;

import java.util.Optional;

/** Indirection: застосунок не знає, де й як зберігаються читачі. */
public interface ReaderRepository {
    Optional<Reader> findByName(String name);

    void save(Reader reader);

    /** Ідентичність нової видачі генерує сховище (патерн nextIdentity), а не сутність. */
    int nextLoanId();
}
