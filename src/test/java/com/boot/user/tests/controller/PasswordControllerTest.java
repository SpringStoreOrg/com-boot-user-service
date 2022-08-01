package com.boot.user.tests.controller;

import com.boot.user.service.UserService;
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

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class PasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectWriter objectWriter;

    @Before
    public void init() {
        this.objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }


    @Test
    public void changeUserPassword() throws Exception {

        String token = "qweqw-e1231-qwew-4324";
        String newPassword = "newPassword";
        String confirmedNewPassword = "confirmedNewPassword";


        mockMvc.perform(put("/password/change/" + token + "/"+ newPassword+ "/"+confirmedNewPassword)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("Password successfully changed!"));;

        verify(userService).changeUserPassword(token, newPassword, confirmedNewPassword);
    }

    @Test
    public void requestResetPassword() throws Exception {

        String email = "test@email.com";

        mockMvc.perform(put("/password/reset/" + email)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("Request to reset Password successfully send!"));;

        verify(userService).requestResetPassword(email);
    }
}
