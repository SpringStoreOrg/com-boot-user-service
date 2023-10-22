package com.boot.user.service;

import com.boot.user.client.ProductServiceClient;
import com.boot.user.dto.ProductDTO;
import com.boot.user.model.User;
import com.boot.user.model.UserFavorite;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductRetrieverService {
    private ProductServiceClient productServiceClient;

    @Transactional
    public List<ProductDTO> getProductDTOS(@NotNull User user) {
        List<UserFavorite> userFavorites = user.getUserFavorites();
        if (CollectionUtils.isNotEmpty(userFavorites)) {
            String productParam = userFavorites.stream()
                    .map(UserFavorite::getProductName)
                    .collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(productParam)) {
                return productServiceClient.callGetAllProductsFromUserFavorites(productParam, true).getProducts();
            }
        }

        return new ArrayList<>();
    }
}
