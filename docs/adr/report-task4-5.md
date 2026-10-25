# Лабораторна робота №8. Проєктування та рефакторинг за GRASP

**Варіант:** 4 (бібліотека) · **Мова:** Java · **Розділи:** Завдання 4 та Завдання 5

Усі числа в цьому документі отримано скриптом `tools/metrics.py` на коді з цього репозиторію (повні таблиці: `docs/metrics-before.md`, `docs/metrics-after.md`).

---

## Завдання 4. Динамічна поведінка та ізоляція змін (Polymorphism + Protected Variations)

### 4.1. Проблема вихідного коду

У `LegacyLibraryProcessor` застава й збір рахуються каскадом за текстовим полем категорії:

```java
// Фрагмент LegacyLibraryProcessor.issueBookFromConsole()
class LegacyLibraryProcessor {
    void issueBookFromConsole() {
        // ...
        if ("Rare".equalsIgnoreCase(loan.book.category)) {
            deposit = loan.book.valueAmount * 0.5;
            fee += 200.0;
        } else if ("Academic".equalsIgnoreCase(loan.book.category)) {
            deposit = loan.book.valueAmount * 0.2;
            fee += 50.0;
        } else if ("Standard".equalsIgnoreCase(loan.book.category)) {
            deposit = 0.0;
        }
        // ...
    }
}
```

Недоліки: нова категорія чи зміна відсотків вимагає правки методу (порушення OCP та Protected Variations); невідома категорія мовчки отримує збір 50 без застави; гроші в `double`; логіка розрахунку живе поза сутностями; консоль, журнал і email викликаються з того самого методу.

### 4.2. Protected Variations: стабільний контракт

Змінну частину закрито інтерфейсом з одним методом. Застава й збір рахуються разом і повертаються незмінним `LoanFee`.

Файл `domain/policy/LoanFeePolicy.java`
```java
package domain.policy;

import java.math.BigDecimal;

/**
 * Стабільний контракт розрахунку вартості видачі (Protected Variations).
 * Один метод: застава й збір рахуються разом, тому кожна реалізація має LCOM4 = 1.
 */
public interface LoanFeePolicy {
    BigDecimal BASE_SERVICE_FEE = new BigDecimal("50.00");

    LoanFee feeFor(BigDecimal bookValue);
}
```

Файл `domain/policy/LoanFee.java`
```java
package domain.policy;

import java.math.BigDecimal;

/** Незмінний результат розрахунку: застава + сервісний збір. */
public record LoanFee(BigDecimal deposit, BigDecimal serviceFee) {

    public LoanFee {
        if (deposit == null || serviceFee == null || deposit.signum() < 0 || serviceFee.signum() < 0) {
            throw new IllegalArgumentException("Застава й збір мають бути невід'ємними.");
        }
    }

    public BigDecimal total() {
        return deposit.add(serviceFee);
    }
}
```

### 4.3. Polymorphism: клас на кожну категорію

Файл `domain/policy/StandardLoanPolicy.java`
```java
package domain.policy;

import java.math.BigDecimal;

/** Звичайна книга: без застави, лише базовий збір. */
public final class StandardLoanPolicy implements LoanFeePolicy {
    @Override
    public LoanFee feeFor(BigDecimal bookValue) {
        return new LoanFee(new BigDecimal("0.00"), BASE_SERVICE_FEE);
    }
}
```

Файл `domain/policy/AcademicLoanPolicy.java`
```java
package domain.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Навчальна книга: застава 20% вартості, збір 50 + 50 грн. */
public final class AcademicLoanPolicy implements LoanFeePolicy {
    private static final BigDecimal DEPOSIT_RATE = new BigDecimal("0.20");
    private static final BigDecimal ADDITIONAL_FEE = new BigDecimal("50.00");

    @Override
    public LoanFee feeFor(BigDecimal bookValue) {
        return new LoanFee(
                bookValue.multiply(DEPOSIT_RATE).setScale(2, RoundingMode.HALF_UP),
                BASE_SERVICE_FEE.add(ADDITIONAL_FEE));
    }
}
```

Файл `domain/policy/RareLoanPolicy.java`
```java
package domain.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Рідкісна книга: застава 50% вартості, збір 50 + 200 грн. */
public final class RareLoanPolicy implements LoanFeePolicy {
    private static final BigDecimal DEPOSIT_RATE = new BigDecimal("0.50");
    private static final BigDecimal ADDITIONAL_FEE = new BigDecimal("200.00");

    @Override
    public LoanFee feeFor(BigDecimal bookValue) {
        return new LoanFee(
                bookValue.multiply(DEPOSIT_RATE).setScale(2, RoundingMode.HALF_UP),
                BASE_SERVICE_FEE.add(ADDITIONAL_FEE));
    }
}
```

### 4.4. Вибір політики без розгалужень

Політика належить константі enum; реєстр `LoanPolicies` — єдине місце її створення. `Book` не знає про політики, лише викликає `category.feeFor(value)` (Закон Деметри).

Файл `domain/policy/LoanPolicies.java`
```java
package domain.policy;

/**
 * Реєстр стратегій (Dictionary Dispatch): єдине місце, де створюються політики.
 * Додати нову політику = додати клас і один рядок тут.
 */
public final class LoanPolicies {
    public static final LoanFeePolicy STANDARD = new StandardLoanPolicy();
    public static final LoanFeePolicy ACADEMIC = new AcademicLoanPolicy();
    public static final LoanFeePolicy RARE = new RareLoanPolicy();

    private LoanPolicies() {
    }
}
```

Файл `domain/BookCategory.java`
```java
package domain;

import domain.policy.LoanFee;
import domain.policy.LoanFeePolicy;
import domain.policy.LoanPolicies;

import java.math.BigDecimal;

/**
 * Категорія книги. Кожна константа несе власну політику (Polymorphism): каскаду if/else немає,
 * а нова категорія без політики просто не скомпілюється (Protected Variations).
 */
public enum BookCategory {
    STANDARD(LoanPolicies.STANDARD),
    RARE(LoanPolicies.RARE),
    ACADEMIC(LoanPolicies.ACADEMIC);

    private final LoanFeePolicy policy;

    BookCategory(LoanFeePolicy policy) {
        this.policy = policy;
    }

    /** Делегування політиці: зовні політику не видно (Закон Деметри). */
    public LoanFee feeFor(BigDecimal bookValue) {
        return policy.feeFor(bookValue);
    }

    public static BookCategory parse(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Категорія книги не вказана.");
        }
        for (BookCategory category : values()) {
            if (category.name().equalsIgnoreCase(text.trim())) {
                return category;
            }
        }
        throw new IllegalArgumentException("Невідома категорія книги: " + text.trim()
                + " (доступні: Standard, Rare, Academic).");
    }
}
```

Нова категорія = новий клас політики + рядок у `LoanPolicies` + константа в `BookCategory`. Якщо забути політику, код не скомпілюється. Невідома категорія з консолі дає `IllegalArgumentException` (раніше — тиха помилка).

### 4.5. Ізоляція зовнішніх залежностей (Protected Variations + Pure Fabrication)

Усе, що може змінитися поза доменом, закрито контрактами застосунку:

| Точка нестабільності | Стабільний контракт | Реалізації |
|---|---|---|
| Сховище читачів | `ReaderRepository` | `InMemoryReaderRepository` |
| Каталог книг | `BookCatalog` | `InMemoryBookCatalog` |
| Журнал і сповіщення | `LoanIssuedListener` | `FileAuditLogger` (пише у файл), `EmailNotificationService` |

Контролер залежить лише від інтерфейсу `LoanIssuedListener`, тому новий канал сповіщення додається без зміни контролера й домену.

Файл `application/LoanIssuedListener.java`
```java
package application;

import domain.LoanReceipt;

/** Точка варіацій (Protected Variations): журнал, email, SMS... додаються без зміни контролера. */
public interface LoanIssuedListener {
    void onLoanIssued(LoanReceipt receipt);
}
```

Файл `infrastructure/FileAuditLogger.java`
```java
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
```

---

## Завдання 5. Метричний аналіз та інженерна верифікація (Low Coupling + High Cohesion)

### 5.1. Правила підрахунку

**CBO(C)** — кількість *інших* класів проєкту, пов'язаних із `C`: тип є полем, параметром чи поверненням, локальною змінною, на ньому викликаються методи, клас його реалізує чи розширює, перехоплює як виняток. Типи JDK (`java.*`) не враховуються. Практично: імена класів проєкту, що трапляються в тексті класу (скрипт скасовує коментарі й рядкові літерали).

**LCOM4(C)** — кількість зв'язних компонентів графа методів. Вершини — нестатичні методи; **виключено** конструктори, статичні фабрики й тривіальні акцесори (`return field;`), як дозволяє методичка. Ребро — спільне поле екземпляра або прямий виклик. Статичні константи полями екземпляра **не вважаються**. Клас без таких методів позначено «—».

**Instability** `I = Ce / (Ca + Ce)` рахується для шарів (перший сегмент пакета). Пакет `app` (Composition Root, `Main`) у цей розрахунок не входить; `domain.policy` — частина шару `domain`.

Нормативи: **CBO ≤ 4** для доменних сутностей і контролерів, **LCOM4 = 1**, залежності йдуть від нестабільних шарів до стабільних.

### 5.2. Метрики до рефакторингу (legacy)

| Клас | CBO | LCOM4 | Найдовший метод (рядків) | Цикломатична складність |
|---|:---:|:---:|:---:|:---:|
| `LegacyLibraryProcessor` | 3 (`Reader`, `Book`, `LoanRecord`) | 1 | 40 | 4 |
| `LoanRecord` | 2 (`Reader`, `Book`) | — (методів немає) | — | — |
| `Reader` | 0 | — (методів немає) | — | — |
| `Book` | 0 | — (методів немає) | — | — |

**Чесне зауваження про легасі.** CBO = 3 формально вкладається в норматив, а LCOM4 = 1, бо метод один. Метрики *не виявляють* проблеми: клас виконує 6 обов'язків (консоль, створення об'єктів, розрахунок, форматування, журнал, email) в одному 40-рядковому методі, а три інші класи — анемічні структури без жодного методу. Тому для «до» важливі додаткові показники: кількість обов'язків і довжина методу.

### 5.3. Метрики після рефакторингу

| Клас | Шар | CBO | Норматив CBO ≤ 4 | LCOM4 | Норматив LCOM4 = 1 |
|---|---|:---:|:---:|:---:|:---:|
| `Book` | domain | 2 | виконано | 1 | виконано |
| `BookCategory` | domain | 3 | виконано | 1 | виконано |
| `Loan` | domain | 4 | виконано | 1 | виконано |
| `Reader` | domain | 3 | виконано | 1 | виконано |
| `LoanReceipt` (record) | domain | 0 | виконано | — | — |
| `LoanFee` (record) | domain.policy | 0 | виконано | 1 | виконано |
| `StandardLoanPolicy` | domain.policy | 2 | виконано | 1 | виконано |
| `AcademicLoanPolicy` | domain.policy | 2 | виконано | 1 | виконано |
| `RareLoanPolicy` | domain.policy | 2 | виконано | 1 | виконано |
| `LoanPolicies` | domain.policy | 4 | виконано | — | — |
| `IssueBookController` (контролер) | application | **4** | **виконано** | 1 | виконано |
| `LoanIssuer` (сервіс застосунку) | application | **6** | не поширюється (див. нижче) | 1 | виконано |
| `ConsoleCommandParser` | cli | 2 | виконано | 1 | виконано |
| `LibraryConsoleUi` | cli | 3 | виконано | 1 | виконано |
| `FileAuditLogger` | infrastructure | 2 | виконано | 1 | виконано |
| `EmailNotificationService` | infrastructure | 2 | виконано | 1 | виконано |
| `InMemoryReaderRepository` | infrastructure | 2 | виконано | 1 | виконано |
| `InMemoryBookCatalog` | infrastructure | 3 | виконано | 1 | виконано |
| `Main` (Composition Root) | app | 8 | не поширюється | — | — |

**Про `LoanIssuer` (CBO = 6) і `Main` (CBO = 8).** Норматив ≤ 4 методичка ставить доменним сутностям і контролерам. `LoanIssuer` — сервіс застосунку: він оркеструє два порти (`ReaderRepository`, `BookCatalog`) і дві сутності (`Reader`, `Book`), тому його зв'язки природні й зосереджені в одному місці (обґрунтування — `ADR-002`). `Main` за означенням знає всі частини системи, бо збирає їх докупи. Обидва значення наведено відкрито, а не приховано.

### 5.4. Динаміка ключового класу

| Контролер | CBO | Зв'язки |
|---|:---:|---|
| Перша версія рефакторингу | 9 | команда, два репозиторії, `Reader`, `Book`, `Loan`, `BookCategory`, `LoanReceipt`, слухач |
| Поточна версія (`IssueBookController`) | **4** | `IssueBookCommand`, `LoanIssuer`, `LoanReceipt`, `LoanIssuedListener` |

### 5.5. Нестабільність шарів

| Шар | Ca | Ce | I |
|---|:---:|:---:|:---:|
| domain | 12 | 0 | **0.00** |
| application | 6 | 4 | 0.40 |
| cli | 0 | 4 | **1.00** |
| infrastructure | 0 | 7 | 1.00 |

`I(cli) = 1.00 > I(application) = 0.40 > I(domain) = 0.00`: залежності йдуть від нестабільних шарів до стабільних (принцип Stable Dependencies). Доменне ядро абсолютно стабільне — воно не залежить ні від чого (в першій версії домен залежав від окремого пакета `policy`, тому `I > 0`).

### 5.6. Інженерні висновки

1. **Low Coupling.** Контролер зменшив зчеплення з 9 до 4 завдяки виділенню `LoanIssuer`. Усі доменні сутності й контролер мають CBO ≤ 4. Виняток для сервісу застосунку (6) і `Main` (17) пояснено й задокументовано.
2. **High Cohesion.** Кожен клас, що має нетривіальні методи, має LCOM4 = 1. Для політик це досягнуто природно — контракт із одним методом `feeFor`, без штучних припущень про статичні константи.
3. **Вплив на проблему легасі.** Метод на 40 рядків із 6 обов'язками замінено класами, де найдовший метод займає не більше 13 рядків (`Reader`, `LibraryConsoleUi`). Максимальна цикломатична складність зросла в статичних фабриках (`Book.of`: 6, `Reader.register`: 8), бо в них з'явилася валідація, якої не було в легасі; бізнес-розрахунок тепер не має жодного розгалуження.
4. **Напрямок залежностей.** `I(domain) = 0`, `I(cli) = 1`, залежності спрямовані «до стабільного».
5. **Обмеження.** Метрики рахує евристичний скрипт за наведеними правилами, а не повний аналізатор; результат відтворюваний командою з `README.md`.
