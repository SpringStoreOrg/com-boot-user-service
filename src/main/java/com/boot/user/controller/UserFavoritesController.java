package com.boot.user.controller;

import com.boot.user.dto.ProductDTO;
import com.boot.user.exception.DuplicateEntryException;
import com.boot.user.service.UserFavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.util.List;

import static com.boot.user.util.Constants.USER_ID_HEADER;

@Controller
@AllArgsConstructor
@Validated
@Tag(name = "userFavorites", description = "the User Favorites API")
@RequestMapping("/userFavorites")
public class UserFavoritesController {

    private UserFavoritesService userFavoritesService;

    @Operation(summary = "Add product to user favorites", description = "Add a product to user favorites list", tags = {"userFavorites"})
    @PostMapping ("/{productName}")
    public ResponseEntity<ProductDTO> addProductToUserFavorites(@Size(min = 2, max = 30, message = "Product Name size has to be between 2 and 30 characters!") @PathVariable("productName") String productName, @RequestHeader(value = USER_ID_HEADER) long userId)
            throws DuplicateEntryException{
        ProductDTO product = userFavoritesService.addProductToUserFavorites(userId, productName);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @Operation(summary = "Add products to user favorites", description = "Add a list of products to user favorites list", tags = {"userFavorites"})
    @PutMapping
    public ResponseEntity<List<ProductDTO>> addProductsToUserFavorites(@Size(min = 1, max = 90, message = "Products size has to be between 1 and 90 characters!") @RequestBody List<String> productNames, @RequestHeader(value = USER_ID_HEADER) long userId){
        List<ProductDTO> productList = userFavoritesService.addProductsToUserFavorites(userId, productNames);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @Operation(summary = "Remove product from user favorites", description = "Remove a product from the user favorites list", tags = {"userFavorites"})
    @DeleteMapping("/{productName}")
    public ResponseEntity<List<ProductDTO>> removeProductFromUserFavorites(@Size(min = 2, max = 30, message = "Product Name size has to be between 2 and 30 characters!") @PathVariable("productName") String productName, @RequestHeader(value = USER_ID_HEADER) long userId){
        userFavoritesService.removeProductFromUserFavorites(userId, productName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get all products from user favorites", tags = {"userFavorites"})
    @GetMapping
    @ResponseBody
    public ResponseEntity<List<ProductDTO>> getAllProductsFromUserFavorites(@RequestHeader(value = USER_ID_HEADER) long userId){
        List<ProductDTO> productList = userFavoritesService.getAllProductsFromUserFavorites(userId);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
}
