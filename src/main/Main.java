package main;

import model.Transaction;
import model.TransactionType;
import repository.FileRepository;
import service.TransactionService;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Padronizacao para garantir que o ponto seja o separador decimal
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);
        TransactionService service = new TransactionService();
        FileRepository repository = new FileRepository();

        // Carregamento inicial de dados
        service.getTransactions().addAll(repository.load());

        String input = "";
        // Agora o loop encerra apenas na opcao 7
        while (!input.equals("7")) {
            System.out.println("\n===== CONTROLE FINANCEIRO =====");
            System.out.println("1 - Registrar entrada");
            System.out.println("2 - Registrar saída");
            System.out.println("3 - Listar extrato");
            System.out.println("4 - Filtrar por categoria");
            System.out.println("5 - Exibir saldo");
            System.out.println("6 - Salvar progresso");
            System.out.println("7 - Salvar e sair");
            System.out.print("Escolha uma opção: ");

            input = sc.nextLine().toUpperCase();

            switch (input) {
                case "1" -> registrarTransacao(sc, service, TransactionType.INCOME);

                case "2" -> {
                    // Validacao preventiva de saldo zero antes de iniciar o registro
                    if (service.calculateBalance() <= 0) {
                        System.out.println("OPERACAO BLOQUEADA: Seu saldo atual e R$ 0.00.");
                        System.out.println("ERRO: Voce precisa registrar uma entrada primeiro.");
                    } else {
                        registrarTransacao(sc, service, TransactionType.EXPENSE);
                    }
                }

                case "3" -> {
                    System.out.println("\n---- EXTRATO ----");
                    if (service.getTransactions().isEmpty()) {
                        System.out.println("Nenhuma transacao encontrada no sistema.");
                    } else {
                        service.getTransactions().forEach(System.out::println);
                    }
                }

                case "4" -> {
                    if (service.getTransactions().isEmpty()) {
                        System.out.println("ERRO: Nao ha dados para filtrar.");
                    } else {
                        System.out.print("Digite a categoria para busca: ");
                        String busca = sc.nextLine();
                        var resultado = service.filterBycategory(busca);

                        if (resultado.isEmpty()) {
                            System.out.println("ERRO: Nenhuma transacao encontrada para a categoria informada.");
                            // Mostramos todas as categorias possiveis para ajudar o usuario
                            System.out.println("Dica: Verifique se escreveu corretamente.");
                            service.mostrarCategorias(TransactionType.INCOME);
                            service.mostrarCategorias(TransactionType.EXPENSE);
                        } else {
                            System.out.println("\n---- RESULTADO ----");
                            resultado.forEach(System.out::println);
                        }
                    }
                }

                case "5" -> System.out.printf("\nSALDO ATUAL: R$ %.2f\n", service.calculateBalance());

                case "6" -> {
                    repository.save(service.getTransactions(), service.calculateBalance());
                    System.out.println("DADOS SALVOS: As alteracoes foram gravadas no arquivo.");
                }

                case "7" -> {
                    repository.save(service.getTransactions(), service.calculateBalance());
                    System.out.println("DADOS SALVOS: Saindo do sistema. Ate logo!");
                }

                default -> System.out.println("ERRO: OPCAO INVALIDA!");
            }
        }
        sc.close();
    }

    /**
     * Metodo auxiliar para centralizar a logica de criacao de transacoes.
     */
    private static void registrarTransacao(Scanner sc, TransactionService service, TransactionType type) {
        try {
            System.out.print("Valor: R$ ");
            double val = sc.nextDouble();
            sc.nextLine();

            if (val <= 0) {
                System.out.println("ERRO: O valor deve ser positivo.");
                return;
            }

            if (type == TransactionType.EXPENSE && !service.temSaldoSuficiente(val)) {
                System.out.printf("ERRO: Saldo insuficiente! Disponivel: R$ %.2f\n", service.calculateBalance());
                return;
            }

            System.out.print("Descricao: ");
            String desc = sc.nextLine();

            // Passamos o 'type' para mostrar as categorias corretas (Entrada ou Saida)
            service.mostrarCategorias(type);
            System.out.print("Categoria: ");
            String cat = sc.nextLine();

            // Passamos o 'type' para validar na lista correta
            if (service.categoriaExiste(cat, type)) {
                service.addTransaction(new Transaction(desc, val, LocalDate.now(), type, cat));
                System.out.println("Registro realizado com sucesso!");
            } else {
                System.out.println("ERRO: Categoria invalida para este tipo de operacao!");
            }

        } catch (Exception e) {
            System.out.println("ERRO: Entrada invalida!");
            sc.nextLine();
        }
    }
}