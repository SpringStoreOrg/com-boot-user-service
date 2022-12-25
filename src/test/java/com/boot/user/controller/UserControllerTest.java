package com.boot.user.controller;

import com.boot.user.dto.UserDTO;
import com.boot.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private static ObjectWriter objectWriter;

    @BeforeAll
    public static void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void addUser() throws Exception {

        UserDTO userDTO = getUserDTO();
        String requestJson = objectWriter.writeValueAsString(userDTO);

        when(userService.addUser(userDTO)).thenReturn(userDTO);

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson))
                .andExpect(status().isCreated());

     verify(userService).addUser(userDTO);
    }

    @Test
    void addUserWith_invalidFirstNameMinCharacters() throws Exception {

        UserDTO userDTO = getUserDTO();
        userDTO.setFirstName("aa");
        String requestJson = objectWriter.writeValueAsString(userDTO);

        when(userService.addUser(userDTO)).thenReturn(userDTO);
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.containsString("Min First name size is 3 characters!")));

        verifyNoInteractions(userService);
    }

    @Test
    void updateUserByEmail() throws Exception {

        UserDTO userDTO = getUserDTO();
        userDTO.setFirstName("newTestName");

        String requestJson = objectWriter.writeValueAsString(userDTO);

        when(userService.updateUserByEmail(userDTO.getEmail(), userDTO)).thenReturn(userDTO);

        mockMvc.perform(put("/" + userDTO.getEmail())
                        .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"id\":0,\"firstName\":\"newTestName\",\"lastName\":\"testLastName\"," +
                                "\"password\":\"testPassword\",\"phoneNumber\":\"0742000000\",\"email\":\"jon278@gaailer.site\"," +
                                "\"deliveryAddress\":\"stret, no. 1\",\"role\":null,\"userFavorites\":null,\"activated\":false}"));

        verify(userService).updateUserByEmail(userDTO.getEmail(), userDTO);
    }

    @Test
    void updateUserByEmailInvalidEmail() throws Exception {

        UserDTO userDTO = getUserDTO();
        userDTO.setFirstName("newTestName");

        String requestJson = objectWriter.writeValueAsString(userDTO);


        mockMvc.perform(put("/" + "a")
                        .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.is("updateUserByEmail.email: Invalid email!")));

        verifyNoInteractions(userService);
    }

    @Test
    void confirmUserAccount() throws Exception {

        String token = "testToken";

        mockMvc.perform(put("/confirm/" + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("User activated Successfully!"));

        verify(userService).confirmUserAccount(token);
    }

    @Test
    void getAllUsers() throws Exception {

        when(userService.getAllUsers()).thenReturn(Arrays.asList(
                getUserDTO(),
                getUserDTO()
        ));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":0,\"firstName\":\"testName\",\"lastName\":\"testLastName\",\"password\":\"testPassword\",\"phoneNumber\":\"0742000000\",\"email\":\"jon278@gaailer.site\",\"deliveryAddress\":\"stret, no. 1\",\"role\":null,\"userFavorites\":null,\"activated\":false}," +
                        "{\"id\":0,\"firstName\":\"testName\",\"lastName\":\"testLastName\",\"password\":\"testPassword\",\"phoneNumber\":\"0742000000\",\"email\":\"jon278@gaailer.site\",\"deliveryAddress\":\"stret, no. 1\",\"role\":null,\"userFavorites\":null,\"activated\":false}]"));

        verify(userService).getAllUsers();
    }

    @Test
    void getUserByEmail() throws Exception {

        UserDTO userDTO = getUserDTO();

        String requestJson = objectWriter.writeValueAsString(userDTO);

        when(userService.getUserByEmail(userDTO.getEmail())).thenReturn(userDTO);

        mockMvc.perform(get("/").param("email", userDTO.getEmail())
                        .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"id\":0,\"firstName\":\"testName\",\"lastName\":\"testLastName\"," +
                                "\"password\":\"testPassword\",\"phoneNumber\":\"0742000000\",\"email\":\"jon278@gaailer.site\"," +
                                "\"deliveryAddress\":\"stret, no. 1\",\"role\":null,\"userFavorites\":null,\"activated\":false}"));

        verify(userService).getUserByEmail(userDTO.getEmail());
    }

    @Test
    void deleteUserByByEmail() throws Exception {

        UserDTO userDTO = getUserDTO();

        mockMvc.perform(delete("/" + userDTO.getEmail())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        verify(userService).deleteUserByEmail(userDTO.getEmail());
    }

    private UserDTO getUserDTO() {
        UserDTO userDTO = new UserDTO();

        userDTO.setFirstName("testName")
                .setLastName("testLastName")
                .setPhoneNumber("0742000000")
                .setPassword("testPassword")
                .setEmail("jon278@gaailer.site")
                .setDeliveryAddress("stret, no. 1");

        return userDTO;
    }


}
