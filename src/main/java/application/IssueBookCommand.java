package application;

/** Команда від UI: сирий текст, без бізнес-логіки. */
public record IssueBookCommand(String readerName, String categoryText) {
}