package com.boot.user.controller;

import com.boot.user.dto.UserDTO;
import com.boot.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
 class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void testAddUser() throws Exception {

        UserDTO userDTO = getUserDTO();

        when(userService.addUser(userDTO)).thenReturn(userDTO);

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

     verify(userService).addUser(userDTO);
    }

    @Test
    void testAddUserWith_invalidFirstNameMinCharacters() throws Exception {

        UserDTO userDTO = getUserDTO();
        userDTO.setFirstName("aa");

        when(userService.addUser(userDTO)).thenReturn(userDTO);
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.containsString("Min First name size is 3 characters!")));

        verifyNoInteractions(userService);
    }

    @Test
    void testUpdateUserByEmail() throws Exception {

        UserDTO userDTO = getUserDTO();
        userDTO.setFirstName("newTestName");

        when(userService.updateUserByEmail(userDTO.getEmail(), userDTO)).thenReturn(userDTO);

        mockMvc.perform(put("/" + userDTO.getEmail())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"id\":0,\"firstName\":\"newTestName\",\"lastName\":\"testLastName\"," +
                                "\"password\":\"testPassword\",\"phoneNumber\":\"0742000000\",\"email\":\"jon278@gaailer.site\"," +
                                "\"deliveryAddress\":\"stret, no. 1\",\"roles\":[\"ACCESS\", \"CREATE_ORDER\"],\"userFavorites\":null,\"activated\":false}"));

        verify(userService).updateUserByEmail(userDTO.getEmail(), userDTO);
    }

    @Test
    void testUpdateUserByEmailInvalidEmail() throws Exception {

        UserDTO userDTO = getUserDTO();
        userDTO.setFirstName("newTestName");

        mockMvc.perform(put("/" + "a")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.is("updateUserByEmail.email: Invalid email!")));

        verifyNoInteractions(userService);
    }

    @Test
    void testConfirmUserAccount() throws Exception {

        String token = "testToken";

        mockMvc.perform(get("/confirm/" + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("User activated Successfully!"));

        verify(userService).confirmUserAccount(token);
    }

    @Test
    void testGetAllUsers() throws Exception {

        when(userService.getAllUsers()).thenReturn(Arrays.asList(
                getUserDTO(),
                getUserDTO()
        ));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":0,\"firstName\":\"testName\",\"lastName\":\"testLastName\",\"password\":\"testPassword\",\"phoneNumber\":\"0742000000\",\"email\":\"jon278@gaailer.site\",\"deliveryAddress\":\"stret, no. 1\",\"roles\":[\"ACCESS\",\"CREATE_ORDER\"],\"userFavorites\":null,\"activated\":false}," +
                        "{\"id\":0,\"firstName\":\"testName\",\"lastName\":\"testLastName\",\"password\":\"testPassword\",\"phoneNumber\":\"0742000000\",\"email\":\"jon278@gaailer.site\",\"deliveryAddress\":\"stret, no. 1\",\"roles\":[\"ACCESS\",\"CREATE_ORDER\"],\"userFavorites\":null,\"activated\":false}]"));

        verify(userService).getAllUsers();
    }

    @Test
    void testGetUserByEmail() throws Exception {

        UserDTO userDTO = getUserDTO();

        when(userService.getUserByEmail(userDTO.getEmail())).thenReturn(userDTO);

        mockMvc.perform(get("/").param("email", userDTO.getEmail())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"id\":0,\"firstName\":\"testName\",\"lastName\":\"testLastName\"," +
                                "\"password\":\"testPassword\",\"phoneNumber\":\"0742000000\",\"email\":\"jon278@gaailer.site\"," +
                                "\"deliveryAddress\":\"stret, no. 1\",\"roles\":[\"ACCESS\", \"CREATE_ORDER\"],\"userFavorites\":null,\"activated\":false}"));

        verify(userService).getUserByEmail(userDTO.getEmail());
    }

    @Test
    void testDeleteUserByByEmail() throws Exception {

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
                .setDeliveryAddress("stret, no. 1")
                .setRoles(List.of("ACCESS", "CREATE_ORDER"));

        return userDTO;
    }


}
