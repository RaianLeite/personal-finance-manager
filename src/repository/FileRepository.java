package repository;

import model.Transaction;
import model.TransactionType;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileRepository {
    private final String fileName = "financas.csv";
    // Cabecalho organizado conforme solicitado
    private final String HEADER = "Data;Tipo;Descricao;Valor;Categoria;SALDO TOTAL";

    public void save(List<Transaction> transactions, double currentBalance) {
        try (FileOutputStream fos = new FileOutputStream(fileName);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {

            fos.write(0xef); fos.write(0xbb); fos.write(0xbf); // BOM para acentos

            writer.write(HEADER);
            writer.newLine();

            for (int i = 0; i < transactions.size(); i++) {
                Transaction t = transactions.get(i);
                String linha = t.toCsv();

                // PULO DO GATO: Se for a primeira linha de dados, adiciona o saldo ao lado
                if (i == 0) {
                    linha += ";" + String.format("%.2f", currentBalance);
                } else {
                    linha += ";"; // Nas outras linhas, a coluna de saldo fica vazia
                }

                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("ERRO ao salvar dados: " + e.getMessage());
        }
    }

    // Ler do arquivo e retornar a lista
    public List<Transaction> load() {
        List<Transaction> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), StandardCharsets.UTF_8))) {

            reader.readLine(); // Pula o cabecalho

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(";");

                // Traducao do tipo
                TransactionType tipoEnum = data[1].equalsIgnoreCase("Entrada")
                        ? TransactionType.INCOME
                        : TransactionType.EXPENSE;

                // O Valor esta na posicao 3
                double valor = Double.parseDouble(data[3].replace(",", "."));

                // Note que o data[5] (saldo total) e ignorado pelo Java aqui
                Transaction t = new Transaction(
                        data[2],                  // Descricao
                        valor,                    // Valor
                        LocalDate.parse(data[0]), // Data
                        tipoEnum,                 // Tipo
                        data[4]                   // Categoria
                );
                list.add(t);
            }
        } catch (Exception e) {
            System.err.println("ERRO ao carregar dados: " + e.getMessage());
        }
        return list;
    }
}