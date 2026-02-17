package model;

public enum TransactionType {
    INCOME("Entrada"),
    EXPENSE("Saída");

    private String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
