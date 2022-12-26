package com.boot.user.controller;

import com.boot.user.dto.ProductDTO;
import com.boot.user.enums.ProductStatus;
import com.boot.user.service.UserFavoritesService;
import com.boot.user.testDataUtils.TestDataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserFavoritesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserFavoritesService userFavoritesService;

    @Test
    void testAddProductToUserFavorites() throws Exception {

        String email = "test@email.com";
        ProductDTO productDTO = getProductDTO(1, "Green wood Chair 1");

        when(userFavoritesService.addProductToUserFavorites(email, productDTO.getName())).thenReturn(Arrays.asList(
                getProductDTO(1, "Green wood Chair 1"),
                getProductDTO(2, "Green wood Chair 2"),
                getProductDTO(3, "Green wood Chair 3"),
                getProductDTO(4, "Green wood Chair 4")));

        mockMvc.perform(post("/userFavorites/" + email + "/" + productDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(TestDataUtils.readFileAsString("./src/test/resources/testdata/controller/addProductToUserFavorites.json")));

        verify(userFavoritesService).addProductToUserFavorites(email, productDTO.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "  ", "asdkasdjlasjkdalskdjasldakjdalkdjaslk", "123^^@test.com"})
    @NullSource
    void testAddProductToUserFavorites_invalidEmail(String email) throws Exception {

        ProductDTO productDTO = getProductDTO(1, "Green wood Chair 1");

        when(userFavoritesService.addProductToUserFavorites(email, productDTO.getName())).thenReturn(Arrays.asList(
                getProductDTO(1, "Green wood Chair 1"),
                getProductDTO(2, "Green wood Chair 2"),
                getProductDTO(3, "Green wood Chair 3"),
                getProductDTO(4, "Green wood Chair 4")));

        mockMvc.perform(post("/userFavorites/" + email + "/" + productDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.is("addProductToUserFavorites.email: Invalid email!")));

        verifyNoInteractions(userFavoritesService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "asdkasdjlasjkdalskdjasldakjdalkdjaslk"})
    void testAddProductToUserFavorites_invalidProductName(String productName) throws Exception {

        String email = "test@email.com";

        when(userFavoritesService.addProductToUserFavorites(email, productName)).thenReturn(Arrays.asList(
                getProductDTO(1, "Green wood Chair 1"),
                getProductDTO(2, "Green wood Chair 2"),
                getProductDTO(3, "Green wood Chair 3"),
                getProductDTO(4, "Green wood Chair 4")));

        mockMvc.perform(post("/userFavorites/" + email + "/" + productName)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.containsString("Product Name size has to be between 2 and 30 characters!")));

        verifyNoInteractions(userFavoritesService);
    }

    @Test
    void testAddProductsToUserFavorites() throws Exception {

        String email = "test@email.com";
        List<String> products = Arrays.asList("Green wood Chair 1", "Green wood Chair 2", "Green wood Chair 3");

        when(userFavoritesService.addProductsToUserFavorites(email, products)).thenReturn(Arrays.asList(
                getProductDTO(1, "Green wood Chair 1"),
                getProductDTO(2, "Green wood Chair 2"),
                getProductDTO(3, "Green wood Chair 3")));

        mockMvc.perform(put("/userFavorites/" + email)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(products)))
                .andExpect(status().isOk())
                .andExpect(content().json(Objects.requireNonNull(TestDataUtils.readFileAsString("./src/test/resources/testdata/controller/addProductsToUserFavorites.json"))));

        verify(userFavoritesService).addProductsToUserFavorites(email, products);
    }

    private ProductDTO getProductDTO(long id, String productName) {
        ProductDTO productDTO = new ProductDTO();

        productDTO.setId(id)
                .setName(productName)
                .setCategory("Chair")
                .setDescription("Best green wood Chair")
                .setPhotoLink("www.asqweqwdasdad.com")
                .setPrice(10000)
                .setStatus(ProductStatus.ACTIVE)
                .setStock(12);

        return productDTO;
    }
}
