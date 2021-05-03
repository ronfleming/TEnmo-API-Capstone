package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDAO implements TransferDAO {

    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Transfer transfer) {
        String sqlCreateTransfer = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from," +
                " account_to, amount) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlCreateTransfer, transfer.getTypeId(), transfer.getStatusId(), transfer.getAccountFromId(),
            transfer.getAccountToId(), transfer.getAmount());
    }

    @Override
    public List<Transfer> listByAccountId(Integer accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sqlListByUserId = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, " +
                "account_to, amount FROM transfers WHERE (account_from = ? OR account_to = ?) AND transfer_type_id = 2";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlListByUserId, accountId, accountId);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }

    @Override
    public List<Transfer> listPendingByAccountId(Integer accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sqlListByUserId = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, " +
                "account_to, amount FROM transfers WHERE account_from = ? AND transfer_type_id = 1";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlListByUserId, accountId);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }

    @Override
    public Transfer getTransferByTransferId(Integer transferId, Integer accountId) {
        Transfer transfer = new Transfer();
        String sqlGetTransferByTransferId = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, " +
                "account_to, amount FROM transfers WHERE transfer_id = ? AND (account_from = ? OR account_to = ?)";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetTransferByTransferId, transferId, accountId, accountId);
        //System.out.println(results.next());
        if(results.next()){
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    @Override
    public boolean processRequest(Transfer transfer) {
        String sqlProcessRequest = "UPDATE transfers SET transfer_type_id = 2, transfer_status_id = ? " +
                "WHERE transfer_id = ? " +          // finding by transfer id
                "AND transfer_type_id = 1 " +       // transfer_type_id = 1 means is Request
                "AND transfer_status_id = 1";       // transfer_status_id = 1 means is Pending
        return jdbcTemplate.update(sqlProcessRequest, transfer.getStatusId(), transfer.getId()) == 1;
        // returns true if rows affected = 1
    }

    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer theTransfer = new Transfer();
        theTransfer.setId(results.getInt("transfer_id"));
        theTransfer.setTypeId(results.getInt("transfer_type_id"));
        theTransfer.setStatusId(results.getInt("transfer_status_id"));
        theTransfer.setAccountFromId(results.getInt("account_from"));
        theTransfer.setAccountToId(results.getInt("account_to"));
        theTransfer.setAmount(new BigDecimal(results.getString("amount")));

        return theTransfer;
    }
}
