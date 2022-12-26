package com.boot.user.controller;

import com.boot.user.dto.ChangeUserPasswordDTO;
import com.boot.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class PasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testChangeUserPassword() throws Exception {

        ChangeUserPasswordDTO changeUserPasswordDTO = new ChangeUserPasswordDTO();
        changeUserPasswordDTO.setToken("qweqw-e1231-qwew-4324");
        changeUserPasswordDTO.setNewPassword("newPassword");
        changeUserPasswordDTO.setConfirmedNewPassword("newPassword");


        mockMvc.perform(put("/password/change/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(changeUserPasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password successfully changed!"));

        verify(userService).changeUserPassword(changeUserPasswordDTO);
    }

    @Test
    void testRequestResetPassword() throws Exception {

        String email = "test@email.com";

        mockMvc.perform(put("/password/reset/" + email)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("Request to reset Password successfully send!"));

        verify(userService).requestResetPassword(email);
    }
}
