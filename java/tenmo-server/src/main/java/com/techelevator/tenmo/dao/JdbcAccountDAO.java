package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDAO implements AccountDAO {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDAO (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getBalance(Integer userId) {
        String sqlGetBalance = "SELECT balance FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetBalance, userId);
        results.next();
        return results.getBigDecimal("balance");
    }

    @Override
    public void withdraw(Integer accountId, BigDecimal amount) {
        String sqlWithdraw = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
        jdbcTemplate.update(sqlWithdraw, amount, accountId);
    }

    @Override
    public void deposit(Integer accountId, BigDecimal amount) {
        String sqlDeposit = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        jdbcTemplate.update(sqlDeposit, amount, accountId);
    }

    @Override
    public Integer getAccountIdByUserId(Integer userId) {
        String sqlGetAccountIdByUserId = "SELECT account_id FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAccountIdByUserId, userId);
        results.next();
        return results.getInt("account_id");
    }

    @Override
    public String getUsernameByAccountId(Integer accountId) {
        String sqlGetUsernameByAccountId = "SELECT users.username FROM accounts ac " +
                "JOIN users ON ac.user_id = users.user_id WHERE ac.account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetUsernameByAccountId, accountId);
        results.next();
        return results.getString("username");
    }


}
