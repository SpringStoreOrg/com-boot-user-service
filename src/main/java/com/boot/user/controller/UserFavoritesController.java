package com.boot.user.controller;

import com.boot.user.dto.ProductDTO;
import com.boot.user.exception.DuplicateEntryException;
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
    public ResponseEntity<List<ProductDTO>> addProductToUserFavorites(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email,
                                                             @Size(min = 2, max = 30, message = "Product Name size has to be between 2 and 30 characters!") @PathVariable("productName") String productName)
            throws DuplicateEntryException {
        List<ProductDTO> productList = userFavoritesService.addProductToUserFavorites(email, productName);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @PutMapping("/{email}")
    public ResponseEntity<List<ProductDTO>> addProductsToUserFavorites(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email,
                                                                       @Size(min = 2, max = 90, message = "Products size has to be between 2 and 90 characters!") @RequestBody List<String> productNames) {
        List<ProductDTO> productList = userFavoritesService.addProductsToUserFavorites(email, productNames);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @DeleteMapping("/{email}/{productName}")
    public ResponseEntity<List<ProductDTO>> removeProductFromUserFavorites(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email,
                                                                  @Size(min = 2, max = 30, message = "Product Name size has to be between 2 and 30 characters!") @PathVariable("productName") String productName) {
        List<ProductDTO> productList = userFavoritesService.removeProductFromUserFavorites(email, productName);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @GetMapping("/{email}")
    @ResponseBody
    public ResponseEntity<List<ProductDTO>> getAllProductsFromUserFavorites(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email)
             {
        List<ProductDTO> productList = userFavoritesService.getAllProductsFromUserFavorites(email);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
}
