# ADR-002: Тонкий CLI, Use-Case Controller, Indirection та Pure Fabrication

## Статус
Прийнято (Accepted)

## Принципи GRASP
Controller, Indirection, Pure Fabrication, High Cohesion, Low Coupling

## Контекст
Весь потік «введення → логіка → збереження → вивід» був в одному методі. Змінити спосіб введення, формат квитанції чи канал сповіщення було неможливо без правки розрахунків.

Перша версія рефакторингу мала контролер із **CBO = 9** (команда, два репозиторії, три сутності, `BookCategory`, квитанція, слухач), що порушує норматив **CBO ≤ 4 для контролерів**. Це рішення виправляє її й замінює варіант без окремого сервісу застосунку.

## Рішення
Потік розділено на шари `cli → application → domain`, а `infrastructure` реалізує порти (інтерфейси) застосунку. Точка входу `app.Main` збирає всі частини докупи (Composition Root).

| Шар | Клас | Відповідальність | GRASP |
|---|---|---|---|
| CLI | `LibraryConsoleUi` | меню, виклик контролера, виведення результату | Thin UI |
| CLI | `ConsoleCommandParser` | читає рядки зі `Scanner`, формує `IssueBookCommand` | Pure Fabrication |
| Application | `IssueBookController` | приймає команду, делегує сценарій, сповіщає слухачів | Controller |
| Application | `LoanIssuer` | сценарій «видати книгу»: знайти читача й книгу, делегувати `Reader.borrow`, зберегти | Pure Fabrication (Application Service) |
| Application | `ReaderRepository`, `BookCatalog`, `LoanIssuedListener` | порти | Indirection |
| Infrastructure | `InMemoryReaderRepository`, `InMemoryBookCatalog` | сховища в пам'яті | Pure Fabrication |
| Infrastructure | `FileAuditLogger`, `EmailNotificationService` | журнал (запис у файл), імітація email | Pure Fabrication |

Контролер не містить `System.out`/`Scanner` і обчислень. Він знає лише `IssueBookCommand`, `LoanIssuer`, `LoanReceipt`, `LoanIssuedListener` — **CBO = 4**.

## Альтернативи (QOC)

| Question | Option | High Cohesion | Low Coupling (CBO контролера) | Простота |
|---|---|:---:|:---:|:---:|
| Де обробляти команду? | A. Усе в UI-класі (як було) | − | − | + |
| | B. Thin UI + контролер, що сам працює з репозиторіями й сутностями | + | − (CBO = 9) | 0 |
| | **C. Thin UI + контролер + `LoanIssuer`** | **+** | **+ (CBO = 4)** | **−** |
| Як сповіщати? | A. Контролер викликає журнал і email напряму | − | − | + |
| | **B. Список `LoanIssuedListener`** | **+** | **+** | **+** |

Conclusion: обрано **C** і **B**. Варіант B для першого питання відхилено, бо він не виконує норматив CBO ≤ 4 для контролера (виміряно: 9).

## Наслідки
* (+) Заміна консолі на веб чи тести не зачіпає контролер; новий канал сповіщення — це новий `LoanIssuedListener`, доданий у `Main`.
* (+) UI не має бізнес-логіки, контролер не має вводу-виводу, сутності нічого не знають про консоль і сховища.
* (−) **`LoanIssuer` має CBO = 6** (`ReaderRepository`, `BookCatalog`, `IssueBookCommand`, `Reader`, `Book`, `LoanReceipt`). Норматив методички стосується доменних сутностей і контролерів; `LoanIssuer` — сервіс застосунку. Його зв'язки зосереджені в одному місці, яке оркеструє два порти й дві сутності; зменшувати значення штучно (приховуючи типи ланцюжками чи лямбдами) не можна.
* (−) Додався один клас (`LoanIssuer`) і один рівень делегування.
