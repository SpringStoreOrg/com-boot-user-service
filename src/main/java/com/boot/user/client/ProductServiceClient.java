package com.boot.user.client;

import com.boot.user.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    @GetMapping
    @ResponseBody
    List<ProductDTO> callGetAllProductsFromUserFavorites(@RequestParam("productNames") String productNames, @RequestParam("includeInactive") Boolean includeInactive);
}