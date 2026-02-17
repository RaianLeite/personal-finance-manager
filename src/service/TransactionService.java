package service;

import model.Transaction;
import model.TransactionType;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    private List<Transaction> transactions = new ArrayList<>();

    // Listas separadas para cada tipo
    private List<String> incomeCategories = new ArrayList<>(List.of("Salário", "Investimentos", "Presente", "Venda", "Outros"));
    private List<String> expenseCategories = new ArrayList<>(List.of("Alimentação", "Educação", "Lazer", "Saúde", "Transporte", "Moradia"));

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public double calculateBalance() {
        return transactions.stream()
                .mapToDouble(t -> t.getType() == TransactionType.INCOME ? t.getValue() : -t.getValue())
                .sum();
    }

    public List<Transaction> filterBycategory(String category) {
        return transactions.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    public boolean temSaldoSuficiente(double valorGasto) {
        return (calculateBalance() - valorGasto) >= 0;
    }

    // Agora o metodo precisa saber o TIPO para validar na lista certa
    public boolean categoriaExiste(String nome, TransactionType type) {
        List<String> listaParaValidar = (type == TransactionType.INCOME) ? incomeCategories : expenseCategories;
        return listaParaValidar.stream().anyMatch(c -> c.equalsIgnoreCase(nome));
    }

    // Agora o metodo mostra as categorias certas conforme o que a pessoa esta fazendo
    public void mostrarCategorias(TransactionType type) {
        if (type == TransactionType.INCOME) {
            System.out.println("Categorias de ENTRADA: " + incomeCategories);
        } else {
            System.out.println("Categorias de SAIDA: " + expenseCategories);
        }
    }
}