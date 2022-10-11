package com.boot.user.service;

import com.boot.user.client.ProductServiceClient;
import com.boot.user.dto.ProductDTO;
import com.boot.user.exception.DuplicateEntryException;
import com.boot.user.model.User;
import com.boot.user.model.UserFavorite;
import com.boot.user.repository.UserFavoriteRepository;
import com.boot.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserFavoritesService {

    private UserRepository userRepository;

    private ProductServiceClient productServiceClient;

    private UserFavoriteRepository userFavoriteRepository;

    @Transactional
    public List<ProductDTO> addProductToUserFavorites(String email, String productName) throws DuplicateEntryException {

        User user = userRepository.getUserByEmail(email);

        if (user.getUserFavorites().stream().anyMatch(p -> productName.equals(p.getProductName()))) {
            throw new DuplicateEntryException("Product: " + productName + " was already added to favorites!");
        } else {
            UserFavorite userFavorite = new UserFavorite();

            userFavorite.setUser(user);
            userFavorite.setProductName(productName);
            user.getUserFavorites().add(userFavorite);
            userFavoriteRepository.save(userFavorite);

            return getProductDTOS(user);
        }
    }

    @Transactional
    public List<ProductDTO> addProductsToUserFavorites(String email, List<String> productNames) {
        log.info("addProductsToUserFavorites - process started");
        User user = userRepository.getUserByEmail(email);

            for(String productName : productNames ) {

                if (!user.getUserFavorites().stream().anyMatch(p -> productName.equals(p.getProductName()))) {
                    UserFavorite userFavorite = new UserFavorite();
                    userFavorite.setUser(user);
                    userFavorite.setProductName(productName);
                    user.getUserFavorites().add(userFavorite);
                    userFavoriteRepository.save(userFavorite);
                }
            }
            return getProductDTOS(user);
        }

    @Transactional
    public List<ProductDTO> removeProductFromUserFavorites(String email, String productName) {

        User user = this.userRepository.getUserByEmail(email);
        UserFavorite userFavorite = userFavoriteRepository.findByUserAndProductName(user, productName);
        user.getUserFavorites().remove(userFavorite);

        userFavoriteRepository.delete(userFavorite);

        return getProductDTOS(user);
    }

    public List<ProductDTO> getAllProductsFromUserFavorites(String email) {

        User user = userRepository.getUserByEmail(email);

        return getProductDTOS(user);
    }

    private List<ProductDTO> getProductDTOS(@NotNull User user) {
        List<String> userFavoriteProducts = user.getUserFavorites().stream().map(UserFavorite::getProductName).collect(Collectors.toList());

        String productParam = String.join(",", userFavoriteProducts);

        if (StringUtils.isNotBlank(productParam)) {
            return productServiceClient.callGetAllProductsFromUserFavorites(productParam, true);
        } else {
            return new ArrayList<>();
        }
    }



}
