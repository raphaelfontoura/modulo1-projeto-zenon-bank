package br.com.zenom.repository;

import br.com.zenom.fraud.Currency;
import br.com.zenom.fraud.Customer;
import br.com.zenom.fraud.Transaction;
import br.com.zenom.fraud.TransactionType;
import br.com.zenom.infra.DatabaseConnector;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                select tr.step, tr.type, tr.amount,\s
                    co.name, co.old_balance, co.new_balance,\s
                    cr.name, cr.old_balance, cr.new_balance,
                    tr.is_fraud, tr.is_flagged_fraud
                from transactions tr
                    inner join customers co on co.id = tr.origin_id
                    inner join customers cr on cr.id = tr.recipient_id\s
                where tr.origin_id in (select id from customers where name = ?)
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
                insert into transactions (step, type, amount, origin_id, recipient_id, is_fraud, is_flagged_fraud)
                values (?, ?, ?, ?, ?, ?, ?);
                """;

        try (var conn = connector.getDbConnection()) {
            long originId = insertCustomer(conn, transaction.origin());
            long recipientId = insertCustomer(conn, transaction.recipient());

            try (var pstmt = conn.prepareStatement(insertTransaction)) {
                pstmt.setInt(1, transaction.step());
                pstmt.setString(2, transaction.type().name());
                pstmt.setBigDecimal(3, transaction.amount().toBigDecimal());
                pstmt.setLong(4, originId);
                pstmt.setLong(5, recipientId);
                pstmt.setBoolean(6, transaction.isFraud());
                pstmt.setBoolean(7, transaction.isFlaggedFraud());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private long insertCustomer(Connection conn, Customer customer) throws SQLException {
        String insertCustomer = """
                insert into customers (name, old_balance, new_balance)
                values (?, ?, ?);
                """;
        try (var pstmt = conn.prepareStatement(insertCustomer, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, customer.name());
            pstmt.setBigDecimal(2, customer.oldBalance().toBigDecimal());
            pstmt.setBigDecimal(3, customer.newBalance().toBigDecimal());
            pstmt.executeUpdate();
            try (var rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new SQLException("no generated key returned for customer " + customer.name());
            }
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
