package com.boot.user.tests.controller;

import com.boot.user.dto.ProductDTO;
import com.boot.user.enums.ProductStatus;
import com.boot.user.service.UserFavoritesService;
import com.boot.user.tests.testDataUtils.TestDataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class UserFavoritesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserFavoritesService userFavoritesService;

    private ObjectWriter objectWriter;

    @Before
    public void init() {
        this.objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    public void addProductToUserFavorites() throws Exception {

        String email = "test@email.com";
        ProductDTO productDTO = getProductDTO(1,"Green wood Chair 1");

        when(userFavoritesService.addProductToUserFavorites(email, productDTO.getName())).thenReturn(Arrays.asList(
                getProductDTO(1,"Green wood Chair 1"),
                getProductDTO(2,"Green wood Chair 2"),
                getProductDTO(3,"Green wood Chair 3"),
                getProductDTO(4,"Green wood Chair 4")));

        mockMvc.perform(post("/userFavorites/"+ email +"/"+ productDTO.getName())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(TestDataUtils.readFileAsString("./src/test/resources/testdata/controller/addProductToUserFavorites.json")));

        verify(userFavoritesService).addProductToUserFavorites(email, productDTO.getName());
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
