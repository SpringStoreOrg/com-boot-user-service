package com.boot.user.service;

import com.boot.user.dto.UserDTO;
import com.boot.user.exception.DuplicateEntryException;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.model.User;
import com.boot.user.model.UserFavorite;
import com.boot.user.repository.UserFavoriteRepository;
import com.boot.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.boot.user.model.User.userEntityToDto;

@Slf4j
@Service
public class UserFavoritesService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;


    public UserDTO addProductToUserFavorites(String email, String productName) throws DuplicateEntryException {

        User user = userRepository.getUserByEmail(email);

        UserDTO userDTO = userEntityToDto(user);

        if (userDTO.getUserFavorites().stream().anyMatch(p -> productName.equals(p.getProductName()))) {
            throw new DuplicateEntryException("Product: " + productName + " was already added to favorites!");
        } else {
            UserFavorite userFavorite = new UserFavorite();

            userFavorite.setUser(user);
            userFavorite.setProductName(productName);

            userDTO.getUserFavorites().add(userFavorite);
            userFavoriteRepository.save(userFavorite);

            return userEntityToDto(user);
        }
    }

    public UserDTO removeProductFromUserFavorites(String email, String productName) throws EntityNotFoundException {

        User user = this.userRepository.getUserByEmail(email);

        userFavoriteRepository.deleteByUserAndProductName(user, productName);

        User userUpdated = this.userRepository.getUserByEmail(email);
        if (userUpdated.getUserFavorites().stream().anyMatch(p -> productName.equals(p.getProductName()))) {
            throw new EntityNotFoundException("Product: " + productName + " was not succesfully deleted from the favorites list!");
        }
        return User.userEntityToDto(userUpdated);
    }


    public List<String> getAllProductsFromUserFavorites(String email){

        User user = userRepository.getUserByEmail(email);

        List<String> productFavorites = user.getUserFavorites().stream().map(UserFavorite :: getProductName).collect(Collectors.toList());

        return productFavorites;
    }
}
