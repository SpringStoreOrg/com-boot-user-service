package com.boot.user.client;

import com.boot.user.dto.ProductDTO;
import com.boot.user.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    public List<ProductDTO> callGetAllProductsFromUserFavorites(String productNames, Boolean includeInactive ) {

        return Arrays.asList(Objects.requireNonNull(productServiceRestTemplate.getForEntity(Constants.GET_ALL_PRODUCTS_FOR_USER, ProductDTO[].class, productNames, includeInactive).getBody()));
    }
}