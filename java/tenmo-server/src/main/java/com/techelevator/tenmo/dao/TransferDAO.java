package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import io.swagger.models.auth.In;

import java.util.List;

public interface TransferDAO {

    void create(Transfer transfer);

    List<Transfer> listByAccountId(Integer accountId);

    Transfer getTransferByTransferId(Integer transferId, Integer accountId);

    List<Transfer> listPendingByAccountId(Integer accountId);

    boolean processRequest(Transfer transfer);



}
