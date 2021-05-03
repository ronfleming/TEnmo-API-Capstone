package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

public class AccountService {

    private RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL;
    private String AUTH_TOKEN = "";

    public AccountService(String url) {
        this.BASE_URL = url;
    }

    public BigDecimal viewCurrentBalance(String token) {
        HttpEntity entity = makeAuthEntity(token);
        ResponseEntity<BigDecimal> response = null;

        try {
            response = restTemplate.exchange(BASE_URL + "accounts",
                    HttpMethod.GET, entity, BigDecimal.class);
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getMessage());
        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return response.getBody();
    }

    public User[] listOtherUsers(String token) {
        HttpEntity entity = makeAuthEntity(token);
        ResponseEntity<User[]> response = null;

        try {
            response = restTemplate.exchange(BASE_URL + "users/other",
                    HttpMethod.GET, entity, User[].class);
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getMessage());
        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return response.getBody();
    }

    public String sendBucks(String token, Transfer transfer) {
        HttpEntity entity = makeTransferEntity(token, transfer);
        String response = "";
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(BASE_URL + "transfers",
                    HttpMethod.POST, entity, String.class);
            response = responseEntity.getBody();
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getMessage());
        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return response;
    }

    public String requestBucks(String token, Transfer transfer) {
        HttpEntity entity = makeTransferEntity(token, transfer);
        String response = "";
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(BASE_URL + "transfers/request",
                    HttpMethod.POST, entity, String.class);
            response = responseEntity.getBody();
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getMessage());
        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return response;
    }

    public Transfer[] viewTransferHistory(String token) {
        HttpEntity entity = makeAuthEntity(token);
        ResponseEntity<Transfer[]> response = null;
        try {
            response = restTemplate.exchange(BASE_URL + "transfers", HttpMethod.GET,
                    entity, Transfer[].class);
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getMessage());
        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return response.getBody();
    }

    public Transfer viewTransferById(String token, Integer transferId) {
        ResponseEntity<Transfer> response = null;
        try {
            response = restTemplate.exchange(BASE_URL + "transfers/" + transferId,
                    HttpMethod.GET, makeAuthEntity(token), Transfer.class);
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getMessage());
        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return response.getBody();
    }

    public Integer getAccountIdByUsername(String token) {
        ResponseEntity<Integer> response = null;
        try {
            response = restTemplate.exchange(BASE_URL + "users/accounts",
                    HttpMethod.GET, makeAuthEntity(token), Integer.class);
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getMessage());
        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return response.getBody();
    }

    public Transfer[] viewPendingRequests(String token) {
        HttpEntity entity = makeAuthEntity(token);
        ResponseEntity<Transfer[]> response = null;
        try {
            response = restTemplate.exchange(BASE_URL + "transfers/pending", HttpMethod.GET,
                    entity, Transfer[].class);
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getMessage());
        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return response.getBody();
    }

    public String processRequest(String token, Transfer transfer) {
        HttpEntity entity = makeTransferEntity(token, transfer);
        String response = "";
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(BASE_URL + "transfers", HttpMethod.PUT,
                    entity, String.class);
            response = responseEntity.getBody();
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getMessage());
        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return response;
    }

    private HttpEntity makeAuthEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }

    private HttpEntity<Transfer> makeTransferEntity(String token, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }


}
