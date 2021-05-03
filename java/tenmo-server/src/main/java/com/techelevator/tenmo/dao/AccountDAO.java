package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface AccountDAO {

    BigDecimal getBalance(Integer userId);

    void withdraw(Integer accountId, BigDecimal amount);

    void deposit(Integer accountId, BigDecimal amount);

    Integer getAccountIdByUserId(Integer userId);

    String getUsernameByAccountId(Integer accountId);
}
