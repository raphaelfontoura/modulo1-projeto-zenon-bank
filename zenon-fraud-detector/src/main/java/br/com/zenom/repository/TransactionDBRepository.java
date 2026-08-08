package br.com.zenom.repository;

import br.com.zenom.fraud.Currency;
import br.com.zenom.fraud.Customer;
import br.com.zenom.fraud.Transaction;
import br.com.zenom.fraud.TransactionType;
import br.com.zenom.infra.DatabaseConnector;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TransactionDBRepository implements TransactionRepository {

    DatabaseConnector connector;

    public TransactionDBRepository(DatabaseConnector connector) {
        this.connector = connector;
    }

    @Override
    public Optional<Transaction> findByOriginCustomerName(String name) {

        String query = """
                select step, type, amount,\s
                    origin_name, origin_old_balance, origin_new_balance,\s
                    recipient_name, recipient_old_balance, recipient_new_balance,
                    is_fraud, is_flagged_fraud
                from transactions_flat
                where origin_name = ?
                limit 1;
                """;
        try(var conn = connector.getDbConnection();
        var pstmt = conn.prepareStatement(query);) {
            pstmt.setString(1, name);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(from(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void save(Transaction transaction) {
        String insertTransaction = """
                insert into transactions_flat (step, type, amount,
                    origin_name, origin_old_balance, origin_new_balance,
                    recipient_name, recipient_old_balance, recipient_new_balance,
                    is_fraud, is_flagged_fraud)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        try (var conn = connector.getDbConnection();
             var pstmt = conn.prepareStatement(insertTransaction)) {
            pstmt.setInt(1, transaction.step());
            pstmt.setString(2, transaction.type().name());
            pstmt.setBigDecimal(3, transaction.amount().toBigDecimal());
            pstmt.setString(4, transaction.origin().name());
            pstmt.setBigDecimal(5, transaction.origin().oldBalance().toBigDecimal());
            pstmt.setBigDecimal(6, transaction.origin().newBalance().toBigDecimal());
            pstmt.setString(7, transaction.recipient().name());
            pstmt.setBigDecimal(8, transaction.recipient().oldBalance().toBigDecimal());
            pstmt.setBigDecimal(9, transaction.recipient().newBalance().toBigDecimal());
            pstmt.setBoolean(10, transaction.isFraud());
            pstmt.setBoolean(11, transaction.isFlaggedFraud());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Transaction from(ResultSet rs) throws SQLException {
        int step = rs.getInt(1);
        String type = rs.getString(2);
        BigDecimal amount = rs.getBigDecimal(3);
        String originName = rs.getString(4);
        BigDecimal originOldBalance = rs.getBigDecimal(5);
        BigDecimal originNewBalance = rs.getBigDecimal(6);
        String recipientName = rs.getString(7);
        BigDecimal recipientOldBalance = rs.getBigDecimal(8);
        BigDecimal recipientNewBalance = rs.getBigDecimal(9);
        boolean isFraud = rs.getBoolean(10);
        boolean isFlagged =  rs.getBoolean(11);

        Customer origin = new Customer(
                originName, new Currency(originOldBalance), new Currency(originNewBalance)
        );
        Customer recipient = new Customer(
                recipientName, new Currency(recipientOldBalance), new Currency(recipientNewBalance)
        );
        return new Transaction(
                step, TransactionType.valueOf(type), new Currency(amount), origin, recipient, isFraud, isFlagged
        );
    }

    public void initDatabase(List<Transaction> transactions) {
        transactions.forEach(this::save);
    }
}
