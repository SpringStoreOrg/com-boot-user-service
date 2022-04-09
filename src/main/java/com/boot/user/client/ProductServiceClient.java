package com.boot.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.boot.services.dto.ProductDTO;
import com.boot.user.util.Constants;

public class ProductServiceClient {


	@Autowired
	private RestTemplate productServiceRestTemplate;

	public ResponseEntity<ProductDTO> callGetProductByProductName(String productName) {

		return productServiceRestTemplate.getForEntity(Constants.GET_PRODUCT_BY_PRODUCT_NAME + productName, ProductDTO.class);
	}
}