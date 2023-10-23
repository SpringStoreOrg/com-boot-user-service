package com.boot.user.service;

import com.boot.user.client.ProductServiceClient;
import com.boot.user.dto.ProductDTO;
import com.boot.user.exception.DuplicateEntryException;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.model.User;
import com.boot.user.model.UserFavorite;
import com.boot.user.repository.UserFavoriteRepository;
import com.boot.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class UserFavoritesService{

    private UserRepository userRepository;

    private ProductServiceClient productServiceClient;

    private UserFavoriteRepository userFavoriteRepository;

    private ProductRetrieverService productRetrieverService;

    private static final String INVALID_EMAIL_ERROR = "Invalid Email address!";

    @Transactional
    public ProductDTO addProductToUserFavorites(long userId, String productName){

        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new EntityNotFoundException(INVALID_EMAIL_ERROR);
        }

        List<UserFavorite> userFavorites = userFavoriteRepository.findAllByUser(user);

        if (CollectionUtils.isNotEmpty(userFavorites) && userFavorites.stream().anyMatch(p -> productName.equals(p.getProductName()))) {
            throw new DuplicateEntryException("Product: " + productName + " was already added to favorites!");
        }

        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setUser(user);
        userFavorite.setProductName(productName);
        userFavoriteRepository.save(userFavorite);

        List<ProductDTO> productList = productServiceClient.callGetAllProductsFromUserFavorites(productName, true).getProducts();

        return productList.get(0);
    }

    @Transactional
    public List<ProductDTO> addProductsToUserFavorites(long userId, List<String> productNames){
        log.info("addProductsToUserFavorites - process started");
        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new EntityNotFoundException(INVALID_EMAIL_ERROR);
        }

        user.setUserFavorites(userFavoriteRepository.findAllByUser(user));
        Set<String> userFavoritesSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(user.getUserFavorites())) {
            user.getUserFavorites().stream().forEach(userFavorite -> userFavoritesSet.add(userFavorite.getProductName()));
        } else {
            user.setUserFavorites(new ArrayList<>());
        }

        productNames.forEach(productName -> {
            if (!userFavoritesSet.contains(productName)) {
                UserFavorite userFavorite = new UserFavorite();
                userFavorite.setUser(user);
                userFavorite.setProductName(productName);
                user.getUserFavorites().add(userFavorite);
                userFavoriteRepository.save(userFavorite);
            }
        });

        return productRetrieverService.getProductDTOS(user);
    }

    @Transactional
    public void removeProductFromUserFavorites(long userId, String productName){

        User user = this.userRepository.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException(INVALID_EMAIL_ERROR);
        }

        UserFavorite userFavorite = userFavoriteRepository.findByUserAndProductName(user, productName);
        if (userFavorite == null) {
            throw new EntityNotFoundException("User Favorite could not be found!");
        }

        userFavoriteRepository.delete(userFavorite);
    }

    @Transactional
    public List<ProductDTO> getAllProductsFromUserFavorites(long userId){

        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new EntityNotFoundException(INVALID_EMAIL_ERROR);
        }
        user.setUserFavorites(userFavoriteRepository.findAllByUser(user));
        return productRetrieverService.getProductDTOS(user);
    }
}
