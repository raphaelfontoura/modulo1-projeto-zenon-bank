package br.com.zenom.cli;

import br.com.zenom.ingestor.EfficientTransactionIngestor;
import br.com.zenom.repository.TransactionDBRepository;

public class IngestionMain {

    static void main() {
        EfficientTransactionIngestor ingestor = new EfficientTransactionIngestor();
        TransactionDBRepository repository = new TransactionDBRepository();
        String fileName = "data/PS_20174392719_1491204439457_log.csv";

        long firstTime = System.currentTimeMillis();
//        ingestor.readAsStream(fileName, repository::save); // Elapsed time: 170610 ms
        ingestor.readBatch(fileName, repository::save); //Elapsed time: 46635
        long lastTime = System.currentTimeMillis();
        long elapsedTime = lastTime - firstTime;


        IO.println("Elapsed time: " + elapsedTime + " ms");

    }
}
