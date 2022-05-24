package com.boot.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.boot.services.dto.ProductDTO;
import com.boot.user.util.Constants;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class ProductServiceClient {


    @Autowired
    private RestTemplate productServiceRestTemplate;

    public ResponseEntity<ProductDTO> callGetProductByProductName(String productName) {

        return productServiceRestTemplate.getForEntity(Constants.GET_PRODUCT_BY_PRODUCT_NAME, ProductDTO.class, productName);
    }

    public List<ProductDTO> callGetAllProductsFromUserFavorites(List<String> productNames) {

        return Arrays.asList(Objects.requireNonNull(productServiceRestTemplate.getForEntity(Constants.GET_ALL_PRODUCTS_FOR_USER, ProductDTO[].class, productNames).getBody()));
    }
}