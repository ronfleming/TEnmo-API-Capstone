package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import io.swagger.models.auth.In;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class JdbcTransferDAOTest {

    private TransferDAO dao;
    static SingleConnectionDataSource dataSource;
    static private JdbcTemplate jdbcTemplate;

    @BeforeAll
    public static void setupDataSource() throws SQLException, IOException, FileNotFoundException {
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        /* The following line disables autocommit for connections
         * returned by this DataSource. This allows us to rollback
         * any changes after each test */
        dataSource.setAutoCommit(false);
    }

    @BeforeEach
    public void loadTestData() throws IOException {
        // Attempt at putting SQL code in separate file and calling it
        /*// load test data
        File scriptFile = new File(JdbcTransferDAOTest.class.getClassLoader().getResource("test-data.sql").getFile());
        Scanner scriptInput = new Scanner(scriptFile);

        String testDataSQL = "";
        while (scriptInput.hasNext()) {
            testDataSQL += scriptInput.nextLine();
        }
        scriptInput.close();*/

        String sql = "DELETE FROM transfers CASCADE;\n" +
                "DELETE FROM accounts CASCADE;\n" +
                "DELETE FROM users CASCADE;\n" +
                "\n" +
                "--/* test users */\n" +
                "INSERT INTO users (user_id, username, password_hash)\n" +
                "VALUES (1001, 'johncleese', '$2a$10$QOyqZ0Z9uyCTNxfPCbaskewPRAYZZ87LJFezDVoEIlLdK7I/c1rvy');\n" +
                "\n" +
                "INSERT INTO users (user_id, username, password_hash)\n" +
                "VALUES (1002, 'grahamchapman', '$2a$10$oXEeHIkJn74II9hpYfdPTev/QAFbJhGCZjLqtMYqWs9bY8S3VPnJ6');\n" +
                "\n" +
                "INSERT INTO users (user_id, username, password_hash)\n" +
                "VALUES (1003, 'michaelpalin', '$2a$10$jBPVw.2w1lcg4fWkBQctJOa6IOSADkzrZdHqS0O8cpTMRYSCcBT4i');\n" +
                "\n" +
                "\n" +
                "--/* test accounts */\n" +
                "INSERT INTO accounts(account_id, user_id, balance)\n" +
                "VALUES (2001, 1001, 1000.00);\n" +
                "\n" +
                "INSERT INTO accounts(account_id, user_id, balance)\n" +
                "VALUES (2002, 1002, 1000.00);\n" +
                "\n" +
                "INSERT INTO accounts(account_id, user_id, balance)\n" +
                "VALUES (2003, 1003, 1000.00);\n" +
                "\n" +
                "\n" +
                "/* test transfers */\n" +
                "INSERT INTO transfers(transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (2, 2, 2001, 2002, 100.00);" +
                "\n" +
                "INSERT INTO transfers(transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (2, 2, 2002, 2001, 200.00);\n" +
                "\n" +
                "INSERT INTO transfers(transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (2, 3, 2001, 2002, 300.00);\n" +
                "\n" +
                "INSERT INTO transfers(transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (1, 1, 2001, 2002, 400.00);\n" +
                "\n" +
                "INSERT INTO transfers(transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (2, 3, 2002, 2001, 500.00);\n" +
                "\n" +
                "INSERT INTO transfers(transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (1, 1, 2002, 2001, 600.00);\n";

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        //jdbcTemplate.update(testDataSQL);
        jdbcTemplate.update(sql);
        dao = new JdbcTransferDAO(jdbcTemplate);
    }


    /* After all tests have finished running, this method will close the DataSource */
    @AfterAll
    public static void closeDataSource() throws SQLException {
        dataSource.destroy();
    }

    /* After each test, we rollback any changes that were made to the database so that
     * everything is clean for the next test */
    @AfterEach
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

    @Test
    void createTest() {
        Transfer expected = new Transfer(2, 2, 2001, 2002, new BigDecimal(100.00));
        String sql = "SELECT nextval('seq_transfer_id')";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        result.next();
        Integer expectedTransferId = result.getInt("nextval");
        expected.setId(expectedTransferId);
        dao.create(expected);

        Transfer actual = dao.getTransferByTransferId(3007, 2001);
        assertTransfersAreEqual(expected, actual);
    }

    @Test
    void listByAccountIdTest() {

    }

    @Test
    void listPendingByAccountIdTest() {

    }

    @Test
    void getTransferByTransferIdTest() {

    }

    @Test
    void processRequestTest() {

    }

    private void assertTransfersAreEqual(Transfer expected, Transfer actual) {
        //assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTypeId(), actual.getTypeId());
        assertEquals(expected.getStatusId(), actual.getStatusId());
        assertEquals(expected.getAccountFromId(), actual.getAccountFromId());
        assertEquals(expected.getAccountToId(), actual.getAccountToId());
        assertEquals(expected.getAmount(), actual.getAmount());
    }

}