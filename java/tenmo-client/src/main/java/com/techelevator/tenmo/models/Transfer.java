package com.techelevator.tenmo.models;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class Transfer {
    private Integer id;
    private int typeId;
    private int statusId;
    private Integer accountFromId;
    private Integer accountToId;
    private BigDecimal amount;
    private String recipientName;
    private String userName;

    public Transfer() {

    }

    public Transfer(int typeId, int statusId, Integer accountFromId, Integer accountToId, BigDecimal amount) {
        this.typeId = typeId;
        this.statusId = statusId;
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public Integer getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(Integer accountFromId) {
        this.accountFromId = accountFromId;
    }

    public Integer getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(Integer accountToId) {
        this.accountToId = accountToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {  //Should be in a child class Request(?), but that's not worth the time for this project
        return String.format("%-10d %7s %-15s $%.2f", id, "To:", recipientName, amount);
    }
}
