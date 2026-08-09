package br.com.zenom.cli;

import br.com.zenom.ingestor.EfficientTransactionIngestor;
import br.com.zenom.repository.TransactionDBRepository;

/*
    Total de Transações: 6.362.620
    Total de Fraudes: 8.213
    Valor Total das Transações: US$ 1.144.392.944.759,77
*/
public class IngestionMain {

    static void main() {
        EfficientTransactionIngestor ingestor = new EfficientTransactionIngestor();
        TransactionDBRepository repository = new TransactionDBRepository();
        String fileName = "data/PS_20174392719_1491204439457_log.csv";

        long firstTime = System.currentTimeMillis();
//        ingestor.readAsStream(fileName, repository::save); // Elapsed time: 170610 ms
        ingestor.readBatch(fileName, repository::saveAll); //Elapsed time: 46635 | full file: Elapsed time: 224883 ms
        long lastTime = System.currentTimeMillis();
        long elapsedTime = lastTime - firstTime;

        System.out.println("Elapsed time: " + elapsedTime + " ms");

    }
}
