package br.com.zenom.cli;

import br.com.zenom.fraud.Customer;
import br.com.zenom.fraud.Transaction;
import br.com.zenom.fraud.TransactionType;

import java.math.BigDecimal;

public class Main {
    static void main() {
        Transaction transacao1 = new Transaction(
                1,
                TransactionType.PAYMENT,
                new BigDecimal("9839.64"),
                new Customer("C1231006815",
                        new BigDecimal("170136.0"),
                        new BigDecimal("160296.36")),
                new Customer("M1979787155",
                        new BigDecimal("0.0"),
                        new BigDecimal("0.0")),
                false,
                false
        );
        Transaction transacao2 = new Transaction(
                743,
                TransactionType.CASH_OUT,
                new BigDecimal("850002.52"),
                new Customer("C1280323807",
                        new BigDecimal("850002.52"),
                        new BigDecimal("0.0")),
                new Customer("C873221189",
                        new BigDecimal("6510099.11"),
                        new BigDecimal("7360101.63")),
                true,
                false
        );
        System.out.println(transacao1);
        System.out.println(transacao2);
    }
}
