package br.com.zenom.cli;

import br.com.zenom.fraud.*;
import br.com.zenom.ingestor.TransactionIngestor;
import br.com.zenom.repository.TransactionListRepositoryImpl;
import br.com.zenom.repository.TransactionRepository;
import br.com.zenom.repository.TransactionMapRepositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());

    static void main() {
//        testTransactionsRecords();

        IO.println("====== Transactions file error test ======");
        TransactionIngestor ingestor = new TransactionIngestor();
        List<Transaction> transactionsError = ingestor.ingestorFileTransactions("data/paysim_with_bad_data.csv");
        IO.println(transactionsError.size());
        transactionsError.stream().limit(10).forEach(IO::println);
        IO.println();

        List<Transaction> transactions = ingestor.ingestorFileTransactions("data/PS_20174392719_1491204439457_log.csv");

        IO.println("====== Fraud Analyzer =======");
        FraudAnalyzer fraudAnalyzer = new FraudAnalyzer(transactions);
        IO.println("1. total de fraudes: " + fraudAnalyzer.countFrauds());

        IO.println("2. Top 3 Fraudes de Maior Valor:");
        fraudAnalyzer.findHighestValueFrauds(3).forEach(transaction -> IO.println(transaction.value().toPlainString()));

        IO.println("3. Clientes suspeitos:");
        fraudAnalyzer.findTopSuspiciousClients(5).forEach(IO::println);

        IO.println("4. Prejuízo total: " + fraudAnalyzer.totalAmountFrauds().toPlainString());

        IO.println("5. Fraudes por Tipo:");
        fraudAnalyzer.countFraudsByType().forEach((k, v) -> IO.println("- " + k.name() + ": " + v));

//        memoryDatabaseTests(transactions);

    }

    private static void memoryDatabaseTests(List<Transaction> transactions) {
        IO.println();
        IO.println("====== Transactions memory list database =======");
        TransactionRepository repository = new TransactionListRepositoryImpl(transactions);
        var client1 = "C12345";
        var client2 = "C1231006815";

        repository.findByOriginCustomerName(client1)
                .ifPresentOrElse(IO::println, () -> IO.println("Transação não encontrada para o cliente " + client1));

        repository.findByOriginCustomerName(client2)
                .ifPresentOrElse(IO::println, () -> IO.println("Transação não encontrada para o cliente " + client2));

        long init = System.nanoTime();
        repository.findByOriginCustomerName("C1868032458").ifPresent(IO::println);
        long last = System.nanoTime();
        IO.println("A pesquisa com list levou %d nano segundos".formatted((last - init)));

        repository = new TransactionMapRepositoryImpl(transactions);

        init = System.nanoTime();
        repository.findByOriginCustomerName("C1868032458").ifPresent(IO::println);
        last = System.nanoTime();
        IO.println("A pesquisa com map levou %d nano segundos".formatted((last - init)));
        // A pesquisa levou 20167229 nano segundos (ArrayList)
        // A pesquisa levou 835022 nano segundos (HashMap)
        // A pesquisa levou 391020 nano segundos (TreeMap)
    }

    private static void testTransactionsRecords() {
        Transaction transacao1 = new Transaction(
                1,
                TransactionType.PAYMENT,
                new Currency("9839.64"),
                new Customer("C1231006815",
                        new Currency("170136.0"),
                        new Currency("160296.36")),
                new Customer("M1979787155",
                        new Currency("0.0"),
                        new Currency("0.0")),
                false,
                false
        );
        Transaction transacao2 = new Transaction(
                743,
                TransactionType.CASH_OUT,
                new Currency("850002.52"),
                new Customer("C1280323807",
                        new Currency("850002.52"),
                        new Currency("0.0")),
                new Customer("C873221189",
                        new Currency("6510099.11"),
                        new Currency("7360101.63")),
                true,
                false
        );
        log.info(transacao1.toString());
        log.info(transacao2.toString());
    }
}
