package com.boot.user.client;

import com.boot.user.dto.PagedProductsResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    @GetMapping
    @ResponseBody
    PagedProductsResponseDTO callGetAllProductsFromUserFavorites(@RequestParam("productNames") String productNames, @RequestParam("includeInactive") Boolean includeInactive);
}