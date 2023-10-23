package com.boot.user.controller;

import com.boot.user.dto.PhotoDTO;
import com.boot.user.dto.ProductDTO;
import com.boot.user.service.UserFavoritesService;
import com.boot.user.testDataUtils.TestDataUtils;
import com.boot.user.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserFavoritesController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserFavoritesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserFavoritesService userFavoritesService;

    @Test
    void testAddProductToUserFavorites() throws Exception {
        ProductDTO productDTO = getProductDTO(1, "Green wood Chair 1");

        when(userFavoritesService.addProductToUserFavorites(3, productDTO.getName())).thenReturn(productDTO);

        mockMvc.perform(post("/userFavorites/" + productDTO.getName())
                .header(Constants.USER_ID_HEADER, 3)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(TestDataUtils.readFileAsString("./src/test/resources/testdata/controller/addProductToUserFavorites.json")));

        verify(userFavoritesService).addProductToUserFavorites(3, productDTO.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "asdkasdjlasjkdalskdjasldakjdalkdjaslk"})
    void testAddProductToUserFavorites_invalidProductName(String productName) throws Exception {
        mockMvc.perform(post("/userFavorites/" +  productName)
                .header(Constants.USER_ID_HEADER, 3)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.containsString("Product Name size has to be between 2 and 30 characters!")));

        verifyNoInteractions(userFavoritesService);
    }

    @Test
    void testAddProductsToUserFavorites() throws Exception {
        List<String> products = Arrays.asList("Green wood Chair 1", "Green wood Chair 2", "Green wood Chair 3");

        when(userFavoritesService.addProductsToUserFavorites(3, products)).thenReturn(Arrays.asList(
                getProductDTO(1, "Green wood Chair 1"),
                getProductDTO(2, "Green wood Chair 2"),
                getProductDTO(3, "Green wood Chair 3")));

        mockMvc.perform(put("/userFavorites")
                .header(Constants.USER_ID_HEADER, 3)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(products)))
                .andExpect(status().isOk())
                .andExpect(content().json(Objects.requireNonNull(TestDataUtils.readFileAsString("./src/test/resources/testdata/controller/addProductsToUserFavorites.json"))));

        verify(userFavoritesService).addProductsToUserFavorites(3, products);
    }

    @Test
    void testRemoveProductToUserFavorites() throws Exception {
        ProductDTO productDTO = getProductDTO(1, "Green wood Chair 1");

        mockMvc.perform(delete("/userFavorites/" + productDTO.getName())
                        .header(Constants.USER_ID_HEADER, 4)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        verify(userFavoritesService).removeProductFromUserFavorites(4, productDTO.getName());
    }

    @Test
    void testGetAllProductsFromUserFavorites() throws Exception {
        when(userFavoritesService.getAllProductsFromUserFavorites(5)).thenReturn(Arrays.asList(
                getProductDTO(1, "Green wood Chair 1"),
                getProductDTO(2, "Green wood Chair 2"),
                getProductDTO(3, "Green wood Chair 3"),
                getProductDTO(4, "Green wood Chair 4")));

        mockMvc.perform(get("/userFavorites/")
                        .header(Constants.USER_ID_HEADER, 5)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(TestDataUtils.readFileAsString("./src/test/resources/testdata/controller/getAllProductsFromUserFavorites.json")));

        verify(userFavoritesService).getAllProductsFromUserFavorites(5);
    }

    private ProductDTO getProductDTO(long id, String productName) {
        ProductDTO productDTO = new ProductDTO();

        PhotoDTO  photoDTO = new PhotoDTO();
        photoDTO.setImage("test.trewredqw.com");

        List<PhotoDTO> photoDTOList = new ArrayList<>();
        photoDTOList.add(photoDTO);

        productDTO.setSlug(getSlug(productName))
                .setName(productName)
                .setPhotoLinks(photoDTOList)
                .setPrice(10000);

        return productDTO;
    }

    public static String getSlug(String name) {
        return name.toLowerCase().replace(" ", "-");
    }
}
