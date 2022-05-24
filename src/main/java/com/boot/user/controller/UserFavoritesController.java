package com.boot.user.controller;

import com.boot.user.dto.UserDTO;
import com.boot.user.exception.DuplicateEntryException;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.service.UserFavoritesService;
import com.boot.user.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/userFavorites")
public class UserFavoritesController {

    private UserFavoritesService userFavoritesService;

    @PostMapping ("/{email}/{productName}")
    public ResponseEntity<UserDTO> addProductToUserFavorites(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email,
                                                             @Size(min = 2, max = 30, message = "Product Name size has to be between 2 and 30 characters!") @PathVariable("productName") String productName)
            throws DuplicateEntryException {
        UserDTO user = userFavoritesService.addProductToUserFavorites(email, productName);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{email}/{productName}")
    public ResponseEntity<UserDTO> removeProductFromUserFavorites(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email,
                                                                  @Size(min = 2, max = 30, message = "Product Name size has to be between 2 and 30 characters!") @PathVariable("productName") String productName)
            throws EntityNotFoundException {
        UserDTO user = userFavoritesService.removeProductFromUserFavorites(email, productName);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{email}")
    @ResponseBody
    public ResponseEntity<List<String>> getAllProductsFromUserFavorites(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email)
             {
        List<String> productList = userFavoritesService.getAllProductsFromUserFavorites(email);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
}
