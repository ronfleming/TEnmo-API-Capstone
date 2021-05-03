package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class UserController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    AccountDAO accountDAO;

    @RequestMapping(path = "/users/other", method = RequestMethod.GET)
    public List<User> getOtherUsers(Principal principal) {

        return userDAO.findAllOthers(principal.getName());
    }

    @RequestMapping(path = "/users/accounts", method = RequestMethod.GET)
    public Integer getAccountIdByUsername(Principal principal) {
        Integer userId= userDAO.findIdByUsername(principal.getName());
        return accountDAO.getAccountIdByUserId(userId);
    }
}
