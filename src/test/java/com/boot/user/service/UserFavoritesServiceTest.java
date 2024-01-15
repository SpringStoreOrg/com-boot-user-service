package com.boot.user.service;


import com.boot.user.client.ProductServiceClient;
import com.boot.user.dto.*;
import com.boot.user.exception.DuplicateEntryException;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.model.Role;
import com.boot.user.model.User;
import com.boot.user.model.UserFavorite;
import com.boot.user.repository.UserFavoriteRepository;
import com.boot.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserFavoritesServiceTest {

    @InjectMocks
    UserFavoritesService userFavoritesService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRetrieverService productRetrieverService;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private UserFavoriteRepository userFavoriteRepository;

    @Captor
    private ArgumentCaptor<UserFavorite> userFavoriteArgumentCaptor;

    private static final  String INVALID_EMAIL_ERROR = "Invalid Email address!";

    @Test
    void testAddProductToUserFavorites() throws DuplicateEntryException, EntityNotFoundException {
        User user = getUser();

        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setUser(user);
        userFavorite.setProductName("testProductName3");

        when(userRepository.getUserById(5)).thenReturn(user);

        when(userFavoriteRepository.findAllByUser(user)).thenReturn(user.getUserFavorites());

        when(userFavoriteRepository.save(any())).thenReturn(userFavorite);

        when(productServiceClient.callGetAllProductsFromUserFavorites("testProductName3", true)).thenReturn(getPagedProducts(Arrays.asList(getProductDTO("testProductName3"))));

        userFavoritesService.addProductToUserFavorites(5, "testProductName3");

        verify(userFavoriteRepository).save(userFavoriteArgumentCaptor.capture());

        UserFavorite userFavoriteArgumentCaptorValue = userFavoriteArgumentCaptor.getValue();

        verify(userFavoriteRepository).save(userFavoriteArgumentCaptorValue);

        verify(productServiceClient).callGetAllProductsFromUserFavorites("testProductName3", true);

        List<UserFavorite> userFavorites = user.getUserFavorites();
        userFavorites.add(createUserFavorite(user, "testProductName3"));

        assertNotNull(userFavoriteArgumentCaptorValue);
        assertEquals(user.setUserFavorites(userFavorites), userFavoriteArgumentCaptorValue.getUser());
        assertNotNull(userFavoriteArgumentCaptorValue.getProductName());
    }

    @Test
    void testAddProductToUserFavorites_duplicateProductName() {
        User user = getUser();

        when(userRepository.getUserById(5)).thenReturn(user);

        when(userFavoriteRepository.findAllByUser(user)).thenReturn(user.getUserFavorites());

        DuplicateEntryException exception = assertThrows(DuplicateEntryException.class, () ->
                userFavoritesService.addProductToUserFavorites(5, "testProductName2"));

        assertEquals("Product: testProductName2 was already added to favorites!", exception.getMessage());

        verify(userRepository).getUserById(5);
        verify(userFavoriteRepository).findAllByUser(user);
        verifyNoMoreInteractions(userFavoriteRepository);
    }

    @Test
    void testAddProductToUserFavorites_user_null() {
        User user = getUser();

        when(userRepository.getUserById(7)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userFavoritesService.addProductToUserFavorites(7, "testProductName2"));

        assertEquals(INVALID_EMAIL_ERROR, exception.getMessage());

        verifyNoInteractions(userFavoriteRepository);
    }

    @Test
    void testAddProductsToUserFavorites() throws EntityNotFoundException {
        User user = getUser();

        List<String> productNames = new ArrayList<>();
        productNames.add("testProductName3");
        productNames.add("testProductName4");

        when(userRepository.getUserById(3)).thenReturn(user);

        when(userFavoriteRepository.findAllByUser(user)).thenReturn(user.getUserFavorites());
        when(productRetrieverService.getProductDTOS(any())).thenReturn(Arrays.asList(
                getProductDTO("testProductName1"),
                getProductDTO("testProductName2"),
                getProductDTO("testProductName3"),
                getProductDTO("testProductName4")
        ));

        userFavoritesService.addProductsToUserFavorites(3, productNames);

        verify(userFavoriteRepository, times(2)).save(userFavoriteArgumentCaptor.capture());

        UserFavorite userFavoriteArgumentCaptorValue = userFavoriteArgumentCaptor.getValue();

        verify(userFavoriteRepository).save(userFavoriteArgumentCaptorValue);

        verify(userFavoriteRepository).save(createUserFavorite(user, "testProductName3"));
        verify(userFavoriteRepository).save(createUserFavorite(user, "testProductName4"));

        verify(productRetrieverService).getProductDTOS(any());
    }

    @Test
    void testAddProductsToUserFavorites_user_null() {
        User user = getUser();

        List<String> productNames = new ArrayList<>();
        productNames.add("testProductName3");
        productNames.add("testProductName4");

        when(userRepository.getUserById(2)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userFavoritesService.addProductsToUserFavorites(2, productNames));

        assertEquals(INVALID_EMAIL_ERROR, exception.getMessage());

        verifyNoInteractions(userFavoriteRepository);
    }

    @Test
    void testRemoveProductFromUserFavorites() throws EntityNotFoundException {
        User user = getUser();
        String productName = "testProductName2";
        UserFavorite userFavorite = createUserFavorite(user, productName);

        when(userRepository.getUserById(3)).thenReturn(user);
        when(userFavoriteRepository.findByUserAndProductName(user, productName)).thenReturn(userFavorite);

        userFavoritesService.removeProductFromUserFavorites(3, productName);

        List<UserFavorite> userFavorites = user.getUserFavorites();
        userFavorites.add(createUserFavorite(user, productName));

        verify(userRepository).getUserById(3);
        verify(userFavoriteRepository).findByUserAndProductName(user.setUserFavorites(userFavorites), productName);
        verify(userFavoriteRepository).delete(userFavorite);
    }

    @Test
    void testRemoveProductFromUserFavorites_user_null() {
        User user = getUser();
        String productName = "testProductName2";

        when(userRepository.getUserById(4)).thenReturn(user);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userFavoritesService.removeProductFromUserFavorites(4, productName));

        assertEquals("User Favorite could not be found!", exception.getMessage());
    }

    @Test
    void testRemoveProductFromUserFavorites_userFavorite_null() {
        String productName = "testProductName2";

        when(userRepository.getUserById(7)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userFavoritesService.removeProductFromUserFavorites(7, productName));

        assertEquals(INVALID_EMAIL_ERROR, exception.getMessage());

        verifyNoInteractions(userFavoriteRepository);
    }

    @Test
    void testGetAllProductsFromUserFavorites() throws EntityNotFoundException {
        User user = getUser();

        List<ProductDTO> productDTOList = new ArrayList<>();
        productDTOList.add(getProductDTO("Yellow Chair"));
        productDTOList.add(getProductDTO("Orange Chair"));

        when(userRepository.getUserById(6)).thenReturn(user);
        when(userFavoriteRepository.findAllByUser(user)).thenReturn(user.getUserFavorites());
        when(productRetrieverService.getProductDTOS(any())).thenReturn(productDTOList);

        List<ProductDTO> newProductDTOList = userFavoritesService.getAllProductsFromUserFavorites(6);

        verify(userRepository).getUserById(6);
        verify(productRetrieverService).getProductDTOS(any());
        assertEquals(2, newProductDTOList.size());
        assertEquals(productDTOList, newProductDTOList);
    }

    @Test
    void testGetAllProductsFromUserFavorites_userFavoritesEmpty() throws EntityNotFoundException {
        User user = getUser();
        List<UserFavorite> userFavorites = new ArrayList<>();
        user.setUserFavorites(userFavorites);

        when(userRepository.getUserById(8)).thenReturn(user);
        when(userFavoriteRepository.findAllByUser(user)).thenReturn(user.getUserFavorites());

        List<ProductDTO> newProductDTOList = userFavoritesService.getAllProductsFromUserFavorites(8);

        verify(userRepository).getUserById(8);
        assertTrue(newProductDTOList.isEmpty());
        verifyNoInteractions(productServiceClient);
    }

    @Test
    void testGetAllProductsFromUserFavorites_user_null() {

        when(userRepository.getUserById(4)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userFavoritesService.getAllProductsFromUserFavorites(4));

        assertEquals(INVALID_EMAIL_ERROR, exception.getMessage());
        verifyNoInteractions(productServiceClient);
    }

    private UserFavorite createUserFavorite(User user, String productName) {

        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setUser(user);
        userFavorite.setProductName(productName);

        return userFavorite;
    }

    private User getUser() {
        User user = new User();
        List<UserFavorite> userFavorites = new ArrayList<>();
        List<String> productNamesList = new ArrayList<>(Arrays.asList("testProductName1,testProductName2".split(",")));

        productNamesList.forEach(product -> userFavorites.add(createUserFavorite(user, product)));

        user.setFirstName("testName")
                .setLastName("testLastName")
                .setPhoneNumber("0742000000")
                .setPassword("testPassword")
                .setEmail("jon278@gaailer.site")
                .setRoleList(Arrays.asList(new Role()))
                .setVerified(true)
                .setCreatedOn(LocalDateTime.now())
                .setUserFavorites(userFavorites);

        return user;
    }

    private ProductDTO getProductDTO(String name) {
        ProductDTO productDTO = new ProductDTO();

        productDTO.setThumbnail("test.trewredqw.com")
                .setPrice(10000)
                .setName(name)
                .setSlug(name);

        return productDTO;
    }

    private PagedProductsResponseDTO getPagedProducts(List<ProductDTO> products){
        PagedProductsResponseDTO response = new PagedProductsResponseDTO();
        response.setProducts(products);

        return response;
    }
}
