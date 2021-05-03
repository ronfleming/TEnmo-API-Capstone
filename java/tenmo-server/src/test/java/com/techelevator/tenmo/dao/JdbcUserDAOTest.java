package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class JdbcUserDAOTest {

    private UserDAO dao;
    private static SingleConnectionDataSource dataSource;

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
        File scriptFile = new File(JdbcUserDAOTest.class.getClassLoader().getResource("test-data.sql").getFile());
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
                "INSERT INTO transfers(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (3001, 2, 2, 2001, 2002, 100.00);" +
                "\n" +
                "INSERT INTO transfers(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (3002, 2, 2, 2002, 2001, 200.00);\n" +
                "\n" +
                "INSERT INTO transfers(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (3003, 2, 3, 2001, 2002, 300.00);\n" +
                "\n" +
                "INSERT INTO transfers(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (3004, 1, 1, 2001, 2002, 400.00);\n" +
                "\n" +
                "INSERT INTO transfers(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (3005, 2, 3, 2002, 2001, 500.00);\n" +
                "\n" +
                "INSERT INTO transfers(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (3006, 1, 1, 2002, 2001, 600.00);\n";

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        //jdbcTemplate.update(testDataSQL);
        jdbcTemplate.update(sql);
        dao = new JdbcUserDAO(jdbcTemplate);
    }

    /* After all tests have finished running, this method will close the DataSource */

    @Test
    void findIdByUsernameTest() {
        // User 1001 is johncleese.

        Integer actual = dao.findIdByUsername("johncleese");

        Integer expected = 1001;

        System.out.println(actual);
        System.out.println(expected);

        assertEquals(expected, actual);
    }

    @Test
    void findAllOthersTest() {
        // johncleese is the user
        List<User> userList = dao.findAllOthers("johncleese");
        System.out.println(userList.size());

        Integer expectedListSize = 2;
        Integer actualListSize = userList.size();
        assertEquals(expectedListSize, actualListSize);
    }

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


}