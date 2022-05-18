package com.boot.user.service;

import com.boot.services.dto.ProductDTO;
import com.boot.services.dto.UserDTO;
import com.boot.services.mapper.UserMapper;
import com.boot.services.model.User;
import com.boot.user.client.ProductServiceClient;
import com.boot.user.exception.DuplicateEntryException;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class UserFavoritesService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductServiceClient productServiceClient;

    public UserDTO addProductToUserFavorites(String email, String productName) throws DuplicateEntryException {

        ProductDTO productDTO = productServiceClient.callGetProductByProductName(productName).getBody();

        User user = userRepository.getUserByEmail(email);

        UserDTO userDTOMapped = UserMapper.UserEntityToDto(user);

        if (userDTOMapped.getFavoriteProductList().stream().anyMatch(p -> productName.equals(p.getProductName()))) {
            throw new DuplicateEntryException("Product: " + productName + " was already added to favorites!");
        } else {
            userDTOMapped.getFavoriteProductList().add(productDTO);
            userRepository.save(UserMapper.updateDtoToUserEntity(user, userDTOMapped));

            return UserMapper.UserEntityToDto(user);
        }
    }

    public UserDTO removeProductFromUserFavorites(String email, String productName) throws EntityNotFoundException {

        ProductDTO productDTO = productServiceClient.callGetProductByProductName(productName).getBody();

        User user = userRepository.getUserByEmail(email);

        UserDTO userDTOMapped = UserMapper.UserEntityToDto(user);

        if (userDTOMapped.getFavoriteProductList().stream().anyMatch(p -> productName.equals(p.getProductName()))) {

            userDTOMapped.getFavoriteProductList().remove(productDTO);
            user = userRepository.save(UserMapper.updateDtoToUserEntity(user, userDTOMapped));

            return UserMapper.UserEntityToDto(user);
        } else {
            throw new EntityNotFoundException("Product: " + productName + " was not found in the favorites list!");
        }
    }

    public Set<ProductDTO> getAllProductsFromUserFavorites(String email){

        User user = userRepository.getUserByEmail(email);

        UserDTO userDTOMapped = UserMapper.UserEntityToDto(user);

        return userDTOMapped.getFavoriteProductList();
    }
}
