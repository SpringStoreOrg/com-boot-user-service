package com.boot.user.client;

import com.boot.user.dto.UserDTO;
import com.boot.user.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CartServiceClient {


    @Autowired
    private RestTemplate cartServiceRestTemplate;

    public void callDeleteCartByEmail(String email) {

        cartServiceRestTemplate.exchange(Constants.DELETE_CART_BY_EMAIL, HttpMethod.DELETE,
                new HttpEntity<>(UserDTO.class), String.class, email);
    }
}