package br.com.zenom.ingestor;

import br.com.zenom.fraud.Transaction;
import br.com.zenom.repository.TransactionDBRepository;

import java.util.List;

public class TransactionDBInitializer {

    public static void init(TransactionDBRepository repository,  List<Transaction> transactions) {
        repository.initDatabase(transactions);
    }
}
