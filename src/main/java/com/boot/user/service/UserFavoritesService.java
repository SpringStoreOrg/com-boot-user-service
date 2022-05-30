package com.boot.user.service;

import com.boot.user.client.ProductServiceClient;
import com.boot.user.dto.ProductDTO;
import com.boot.user.dto.UserDTO;
import com.boot.user.exception.DuplicateEntryException;
import com.boot.user.model.User;
import com.boot.user.model.UserFavorite;
import com.boot.user.repository.UserFavoriteRepository;
import com.boot.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.boot.user.model.User.userEntityToDto;

@Slf4j
@Service
@AllArgsConstructor
public class UserFavoritesService {

    private UserRepository userRepository;

    private ProductServiceClient productServiceClient;

    private UserFavoriteRepository userFavoriteRepository;

    @Transactional
    public UserDTO addProductToUserFavorites(String email, String productName) throws DuplicateEntryException {

        User user = userRepository.getUserByEmail(email);

        if (user.getUserFavorites().stream().anyMatch(p -> productName.equals(p.getProductName()))) {
            throw new DuplicateEntryException("Product: " + productName + " was already added to favorites!");
        } else {
            UserFavorite userFavorite = new UserFavorite();

            userFavorite.setUser(user);
            userFavorite.setProductName(productName);

            userFavoriteRepository.save(userFavorite);

            return userEntityToDto(user);
        }
    }

    @Transactional
    public UserDTO removeProductFromUserFavorites(String email, String productName) {

        User user = this.userRepository.getUserByEmail(email);

        userFavoriteRepository.deleteByUserAndProductName(user, productName);

        User userUpdated = this.userRepository.getUserByEmail(email);

        return userEntityToDto(userUpdated);
    }


    public List<ProductDTO> getAllProductsFromUserFavorites(String email){

        User user = userRepository.getUserByEmail(email);

        List<String> userFavoriteProducts =  user.getUserFavorites().stream().map(UserFavorite :: getProductName).collect(Collectors.toList());

        String productParam = userFavoriteProducts.stream().collect(Collectors.joining(","));

        return  productServiceClient.callGetAllProductsFromUserFavorites(productParam, true);
    }
}
