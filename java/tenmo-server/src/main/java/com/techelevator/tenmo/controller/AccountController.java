package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Transfer;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {

    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private TransferDAO transferDAO;
    @Autowired
    private UserDAO userDAO;

    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(path = "/accounts", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal) {
        Integer userId = userDAO.findIdByUsername(principal.getName());
        return accountDAO.getBalance(userId);
    }

    // transfer
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfers", method = RequestMethod.POST)
    public String createTransfer(@Valid @RequestBody Transfer transfer, Principal principal) {
        String statusMessage;

        Integer userFromId = userDAO.findIdByUsername(principal.getName());    //gets userFromId from current user username
        transfer.setAccountFromId(accountDAO.getAccountIdByUserId(userFromId)); // gets userFrom Account Id from userId

        Integer userToId = userDAO.findIdByUsername(transfer.getRecipientName());  // gets userToId from recipient name (send  from client)
        transfer.setAccountToId(accountDAO.getAccountIdByUserId(userToId)); // gets userTo Account Id from userToId

        BigDecimal balance = accountDAO.getBalance(userFromId);     // gets balance from current user
        if (balance.compareTo(transfer.getAmount()) != -1) {
            accountDAO.withdraw(transfer.getAccountFromId(), transfer.getAmount());    // withdraw amount from userFrom account
            accountDAO.deposit(transfer.getAccountToId(), transfer.getAmount());       // deposit amount to userTo account
            transfer.setStatusId(2);  // set status to approved
            statusMessage = "Transaction approved."; // status Approved
        } else {
            transfer.setStatusId(3);    // set status to rejected
            statusMessage = "Transaction rejected: insufficient funds."; // status Rejected
        }

        transferDAO.create(transfer);   // creates transfer row in database
        return statusMessage;
    }

    // get transfers by user id
    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(path = "/transfers", method = RequestMethod.GET)
    public List<Transfer> getTransfersByUserId(Principal principal) {
        Integer userId = userDAO.findIdByUsername(principal.getName());
        Integer accountId = accountDAO.getAccountIdByUserId(userId);
        List<Transfer> transfers = transferDAO.listByAccountId(accountId);

        for (Transfer transfer : transfers) {
            String userFromName = accountDAO.getUsernameByAccountId(transfer.getAccountFromId());
            transfer.setUserName(userFromName);

            String userToName = accountDAO.getUsernameByAccountId(transfer.getAccountToId());
            transfer.setRecipientName(userToName);
        }

        return transfers;
    }

    // get transfer by transfer id
    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(path = "/transfers/{id}", method = RequestMethod.GET)
    public Transfer getTransferByTransferId(@PathVariable Integer id, Principal principal) {
        Integer userId = userDAO.findIdByUsername(principal.getName());
        Integer accountId = accountDAO.getAccountIdByUserId(userId);

        Transfer transfer = transferDAO.getTransferByTransferId(id, accountId);
        if (transfer.getId() != null ) {
            String userFromName = accountDAO.getUsernameByAccountId(transfer.getAccountFromId());
            transfer.setUserName(userFromName);

            String userToName = accountDAO.getUsernameByAccountId(transfer.getAccountToId());
            transfer.setRecipientName(userToName);
        }
        return transfer;
    }


    // transfer request
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfers/request", method = RequestMethod.POST)
    public String createTransferRequest(@Valid @RequestBody Transfer transfer, Principal principal) {
        transfer.setStatusId(1);

        Integer recipientId = userDAO.findIdByUsername(principal.getName());    //gets recipient ID from current user username
        transfer.setAccountToId(accountDAO.getAccountIdByUserId(recipientId)); // gets recipient Account Id from recipient ID

        Integer requesteeId = userDAO.findIdByUsername(transfer.getUserName());  // gets requestee ID from transfer.username (send from client)
        transfer.setAccountFromId(accountDAO.getAccountIdByUserId(requesteeId)); // gets requestee Account Id from requestee ID

        transferDAO.create(transfer);   // creates transfer row in database
        return "Request created.";
    }

    // get transfer where status pending
    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(path = "/transfers/pending", method = RequestMethod.GET)
    public List<Transfer> getPendingTransfers(Principal principal) {
        Integer userId = userDAO.findIdByUsername(principal.getName());
        Integer accountId = accountDAO.getAccountIdByUserId(userId);

        List<Transfer> transfers = transferDAO.listPendingByAccountId(accountId);
        for (Transfer transfer : transfers) {
            String userFromName = accountDAO.getUsernameByAccountId(transfer.getAccountFromId());
            transfer.setUserName(userFromName);

            String userToName = accountDAO.getUsernameByAccountId(transfer.getAccountToId());
            transfer.setRecipientName(userToName);
        }

        return transfers;
    }

    // approve/reject request
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "/transfers", method = RequestMethod.PUT)
    public String processRequest(@Valid @RequestBody Transfer transfer, Principal principal) {
        String statusMessage;       // Message for user
        Integer userId = userDAO.findIdByUsername(principal.getName());    // gets userId from current username in principal
        Integer accountId = accountDAO.getAccountIdByUserId(userId);       // get accountId from userId

        System.out.println("transfer account: " + transfer.getAccountFromId());
        System.out.println("my account: " + accountId);


        if (transfer.getAccountFromId().equals(accountId)) {
            BigDecimal balance = accountDAO.getBalance(userId);     // gets balance from current user
            if (balance.compareTo(transfer.getAmount()) == -1   // if balance is less than amount to transfer
                    && transfer.getStatusId() == 2) {           // if they want to accept
                transfer.setStatusId(3);    // status id for rejected
                statusMessage = "Transfer Rejected: Insufficient funds";
            }

            boolean didUpdate = transferDAO.processRequest(transfer);
            if (didUpdate) {       // Check if process request worked
                if (transfer.getStatusId() == 2) {  // if user approved the request
                    accountDAO.withdraw(transfer.getAccountFromId(), transfer.getAmount());    // withdraw amount from current user's account
                    accountDAO.deposit(transfer.getAccountToId(), transfer.getAmount());       // deposit amount in requester's account
                    statusMessage = "Transfer Approved";
                } else {
                    statusMessage = "Transfer Rejected";
                }
            } else {
                statusMessage = "Transfer Not Processed";        // Transfer was not a request or was not of type pending
            }
        } else {
            statusMessage = "Process Request Failed: Unauthorized User";    // Current user does not own request/ is not money sender
        }

        return statusMessage;
    }


}
