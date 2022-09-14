package com.boot.user.service;


import com.boot.user.client.ProductServiceClient;
import com.boot.user.dto.ProductDTO;
import com.boot.user.dto.UserDTO;
import com.boot.user.enums.ProductStatus;
import com.boot.user.exception.DuplicateEntryException;
import com.boot.user.model.User;
import com.boot.user.model.UserFavorite;
import com.boot.user.repository.UserFavoriteRepository;
import com.boot.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class UserFavoritesServiceTest {

    @InjectMocks
    UserFavoritesService userFavoritesService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private UserFavoriteRepository userFavoriteRepository;

    @Captor
    private ArgumentCaptor<UserFavorite> userFavoriteArgumentCaptor;

    @Test
    public void addProductToUserFavorites() throws DuplicateEntryException {
        User user =  getUser();

        List<UserFavorite> userFavorites = new ArrayList<>();
        UserFavorite userFavorite1 = new UserFavorite();

        userFavorite1.setUser(user);
        userFavorite1.setProductName("testProductName1");
        userFavorites.add(userFavorite1);

        UserFavorite userFavorite2 = new UserFavorite();
        userFavorite2.setUser(user);
        userFavorite2.setProductName("testProductName2");
        userFavorites.add(userFavorite2);

        user.setUserFavorites(userFavorites);

        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);

        userFavoritesService.addProductToUserFavorites(user.getEmail(), "testProductName3");

        verify(userFavoriteRepository).save(userFavoriteArgumentCaptor.capture());

        UserFavorite userFavoriteArgumentCaptorValue = userFavoriteArgumentCaptor.getValue();

        verify(userFavoriteRepository).save(userFavoriteArgumentCaptorValue);

        verify(productServiceClient).callGetAllProductsFromUserFavorites("testProductName1,testProductName2,testProductName3", true);
    }

    @Test
    public void addProductToUserFavorites_duplicateProductName() {
        User user =  getUser();

        List<UserFavorite> userFavorites = new ArrayList<>();
        UserFavorite userFavorite1 = new UserFavorite();

        userFavorite1.setUser(user);
        userFavorite1.setProductName("testProductName1");
        userFavorites.add(userFavorite1);

        UserFavorite userFavorite2 = new UserFavorite();
        userFavorite2.setUser(user);
        userFavorite2.setProductName("testProductName2");
        userFavorites.add(userFavorite2);

        user.setUserFavorites(userFavorites);

        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);

        DuplicateEntryException exception = assertThrows(DuplicateEntryException.class, () ->
                userFavoritesService.addProductToUserFavorites(user.getEmail(), "testProductName2"));

        assertEquals("Product: testProductName2 was already added to favorites!", exception.getMessage());

        verifyNoInteractions(userFavoriteRepository);
    }


    private ProductDTO getProductDTO(){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Black core-wood chair")
                .setStock(3)
                .setStatus(ProductStatus.ACTIVE)
                .setPrice(10000)
                .setPhotoLink("www.testPhoto.com")
                .setDescription("Best core-wood black chair")
                .setCategory("Chair");

        return productDTO;
    }

    private UserDTO getUserDTO() {
        UserDTO userDTO = new UserDTO();

        userDTO.setFirstName("testName")
                .setLastName("testLastName")
                .setPhoneNumber("0742000000")
                .setPassword("testPassword")
                .setEmail("jon278@gaailer.site")
                .setRole("USER")
                .setDeliveryAddress("street, no. 1");

        return userDTO;
    }

    private User getUser() {
        User user = new User();

        user.setFirstName("testName")
                .setLastName("testLastName")
                .setPhoneNumber("0742000000")
                .setPassword("testPassword")
                .setEmail("jon278@gaailer.site")
                .setRole("USER")
                .setActivated(true)
                .setCreatedOn(LocalDate.now())
                .setDeliveryAddress("street, no. 1");

        return user;
    }

}
