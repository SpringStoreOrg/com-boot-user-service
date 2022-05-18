package com.boot.user.controller;

import com.boot.services.dto.ProductDTO;
import com.boot.services.dto.UserDTO;
import com.boot.user.exception.DuplicateEntryException;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.service.UserFavoritesService;
import com.boot.user.service.UserService;
import com.boot.user.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/userFavorites")
public class UserFavoritesController {

    @Autowired
    private UserFavoritesService userFavoritesService;

    @PutMapping("/add/{email}/{productName}")
    public ResponseEntity<UserDTO> addProductToUserFavorites(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email,
                                                             @Size(min = 2, max = 30, message = "Product Name size has to be between 2 and 30 characters!") @PathVariable("productName") String productName)
            throws DuplicateEntryException {
        UserDTO user = userFavoritesService.addProductToUserFavorites(email, productName);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/remove/{email}/{productName}")
    public ResponseEntity<UserDTO> removeProductFromUserFavorites(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email,
                                                                  @Size(min = 2, max = 30, message = "Product Name size has to be between 2 and 30 characters!") @PathVariable("productName") String productName)
            throws EntityNotFoundException {
        UserDTO user = userFavoritesService.removeProductFromUserFavorites(email, productName);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{email}")
    @ResponseBody
    public ResponseEntity<Set<ProductDTO>> getAllProductsFromUserFavorites(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email)
            throws EntityNotFoundException {
        Set<ProductDTO> productList = userFavoritesService.getAllProductsFromUserFavorites(email);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
}
