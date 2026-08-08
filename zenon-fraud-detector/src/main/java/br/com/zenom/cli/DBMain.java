package br.com.zenom.cli;

import br.com.zenom.fraud.Transaction;
import br.com.zenom.infra.DatabaseConnector;
import br.com.zenom.ingestor.TransactionDBInitializer;
import br.com.zenom.ingestor.TransactionIngestor;
import br.com.zenom.repository.TransactionDBRepository;

import java.util.List;

public class DBMain {

    private static final String ERROR_MESSAGE = "Nenhuma transação encontrada";
    static void main() {
        TransactionIngestor ingestor = new TransactionIngestor();
        DatabaseConnector dbConnector = new DatabaseConnector();
        List<Transaction> transactions = ingestor.ingestorFileTransactions("data/PS_20174392719_1491204439457_log.csv");
        TransactionDBRepository repository = new TransactionDBRepository(dbConnector);
        TransactionDBInitializer.init(repository, transactions);

        repository.findByOriginCustomerName("C1231006815").ifPresent(System.out::println);
        repository.findByOriginCustomerName("C1674899618").ifPresentOrElse(IO::println, () -> IO.println(ERROR_MESSAGE));
        repository.findByOriginCustomerName("C12345").ifPresentOrElse(System.out::println, () -> System.out.println(ERROR_MESSAGE));

    }
}
