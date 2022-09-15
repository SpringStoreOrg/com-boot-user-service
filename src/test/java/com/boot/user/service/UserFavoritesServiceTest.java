package com.boot.user.service;


import com.boot.user.client.ProductServiceClient;
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

import static org.junit.jupiter.api.Assertions.*;
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

        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);

        userFavoritesService.addProductToUserFavorites(user.getEmail(), "testProductName3");

        verify(userFavoriteRepository).save(userFavoriteArgumentCaptor.capture());

        UserFavorite userFavoriteArgumentCaptorValue = userFavoriteArgumentCaptor.getValue();

        verify(userFavoriteRepository).save(userFavoriteArgumentCaptorValue);

        verify(productServiceClient).callGetAllProductsFromUserFavorites("testProductName1,testProductName2,testProductName3", true);

        List<UserFavorite> userFavorites = user.getUserFavorites();
        userFavorites.add(createUserFavorite(user,  "testProductName3"));

        assertNotNull(userFavoriteArgumentCaptorValue);
        assertEquals(getUser().setUserFavorites(userFavorites), userFavoriteArgumentCaptorValue.getUser());
        assertNotNull(userFavoriteArgumentCaptorValue.getProductName());
    }

    @Test
    public void addProductToUserFavorites_duplicateProductName() {
        User user =  getUser();

        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);

        DuplicateEntryException exception = assertThrows(DuplicateEntryException.class, () ->
                userFavoritesService.addProductToUserFavorites(user.getEmail(), "testProductName2"));

        assertEquals("Product: testProductName2 was already added to favorites!", exception.getMessage());

        verifyNoInteractions(userFavoriteRepository);
    }

    @Test
    public void addProductsToUserFavorites()  {
        User user =  getUser();

        List<String> productNames = new ArrayList<>();
        productNames.add("testProductName3");
        productNames.add("testProductName4");

        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);

        userFavoritesService.addProductsToUserFavorites(user.getEmail(), productNames);

        verify(userFavoriteRepository, times(2)).save(userFavoriteArgumentCaptor.capture());

        UserFavorite userFavoriteArgumentCaptorValue = userFavoriteArgumentCaptor.getValue();

        verify(userFavoriteRepository).save(userFavoriteArgumentCaptorValue);

        verify(productServiceClient).callGetAllProductsFromUserFavorites("testProductName1,testProductName2,testProductName3,testProductName4", true);
    }

    @Test
    public void removeProductFromUserFavorites()  {
        User user =  getUser();
        String productName = "testProductName2";
        UserFavorite userFavorite = createUserFavorite(user, productName);

        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);
        when(userFavoriteRepository.findByUserAndProductName(user, productName)).thenReturn(userFavorite);

        userFavoritesService.removeProductFromUserFavorites(user.getEmail(), productName);

        List<UserFavorite> userFavorites = user.getUserFavorites();
        userFavorites.add(createUserFavorite(user,  productName));

        verify(userRepository).getUserByEmail(getUser().getEmail());
        verify(userFavoriteRepository).findByUserAndProductName(getUser().setUserFavorites(userFavorites), productName);
        verify(userFavoriteRepository).delete(userFavorite);
    }


    private UserFavorite createUserFavorite(User user, String productName){

        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setUser(user);
        userFavorite.setProductName(productName);

        return userFavorite;
    }

    private User getUser() {
        User user = new User();
        List<UserFavorite> userFavorites = new ArrayList<>();

        userFavorites.add(createUserFavorite(user,"testProductName1"));
        userFavorites.add(createUserFavorite(user,"testProductName2"));

        user.setFirstName("testName")
                .setLastName("testLastName")
                .setPhoneNumber("0742000000")
                .setPassword("testPassword")
                .setEmail("jon278@gaailer.site")
                .setRole("USER")
                .setActivated(true)
                .setCreatedOn(LocalDate.now())
                .setUserFavorites(userFavorites)
                .setDeliveryAddress("street, no. 1");

        return user;
    }

}
