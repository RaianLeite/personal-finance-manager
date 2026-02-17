package model;

import java.time.LocalDate;

public class Transaction {
    private String description;
    private double value;
    private LocalDate date;
    private TransactionType type;
    private String category;

    public Transaction(String description, double value, LocalDate date, TransactionType type, String category) {
        this.description = description;
        this.value = value;
        this.date = date;
        this.type = type;
        this.category = category;
    }

    // Getters
    public String getDescription() { return description; }
    public double getValue() { return value; }
    public LocalDate getDate() { return date; }
    public TransactionType getType() { return type; }
    public String getCategory() { return category; }

    // Metodo para converter o tipo para String em Portugues
    public String getTipoEmPortugues() {
        return (type == TransactionType.INCOME) ? "Entrada" : "Saída";
    }

    // Ordem: Data;Tipo;Descricao;Valor;Categoria
    public String toCsv() {
        return String.format("%s;%s;%s;%.2f;%s",
                date, getTipoEmPortugues(), description, value, category);
    }

    @Override
    public String toString() {
        String sinal = (type == TransactionType.INCOME) ? "[ + ]" : "[ - ]";
        return String.format("%s %s | %-7s | %-20s | %-15s | R$ %8.2f",
                date, sinal, getTipoEmPortugues(), description, category, value);
    }
}