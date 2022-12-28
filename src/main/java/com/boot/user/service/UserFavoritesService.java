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
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserFavoritesService {

    private UserRepository userRepository;

    private ProductServiceClient productServiceClient;

    private UserFavoriteRepository userFavoriteRepository;

    private static final String INVALID_EMAIL_ERROR = "Invalid Email address!";

    @Transactional
    public List<ProductDTO> addProductToUserFavorites(long userId, String productName) throws DuplicateEntryException, EntityNotFoundException {

        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new EntityNotFoundException(INVALID_EMAIL_ERROR);
        }

        user.setUserFavorites(userFavoriteRepository.findAllByUser(user));

        if (CollectionUtils.isNotEmpty(user.getUserFavorites())) {
            if (user.getUserFavorites().stream().anyMatch(p -> productName.equals(p.getProductName()))) {
                throw new DuplicateEntryException("Product: " + productName + " was already added to favorites!");
            }
        }

        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setUser(user);
        userFavorite.setProductName(productName);
        userFavoriteRepository.save(userFavorite);

        user.getUserFavorites().add(userFavorite);

        return getProductDTOS(user);
    }

    @Transactional
    public List<ProductDTO> addProductsToUserFavorites(long userId, List<String> productNames) throws EntityNotFoundException {
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

        return getProductDTOS(user);
    }

    @Transactional
    public List<ProductDTO> removeProductFromUserFavorites(long userId, String productName) throws EntityNotFoundException {

        User user = this.userRepository.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException(INVALID_EMAIL_ERROR);
        }

        UserFavorite userFavorite = userFavoriteRepository.findByUserAndProductName(user, productName);
        if (userFavorite == null) {
            throw new EntityNotFoundException("User Favorite could not be found!");
        }

        user.setUserFavorites(userFavoriteRepository.findAllByUser(user));
        user.getUserFavorites().remove(userFavorite);

        userFavoriteRepository.delete(userFavorite);

        return getProductDTOS(user);
    }

    public List<ProductDTO> getAllProductsFromUserFavorites(long userId) throws EntityNotFoundException {

        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new EntityNotFoundException(INVALID_EMAIL_ERROR);
        }
        user.setUserFavorites(userFavoriteRepository.findAllByUser(user));
        return getProductDTOS(user);
    }

    private List<ProductDTO> getProductDTOS(@NotNull User user) {
        List<UserFavorite> userFavorites = user.getUserFavorites();
        if (CollectionUtils.isNotEmpty(userFavorites)) {
            String productParam = userFavorites.stream()
                    .map(UserFavorite::getProductName)
                    .collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(productParam)) {
                return productServiceClient.callGetAllProductsFromUserFavorites(productParam, true);
            }
        }

        return new ArrayList<>();
    }
}
