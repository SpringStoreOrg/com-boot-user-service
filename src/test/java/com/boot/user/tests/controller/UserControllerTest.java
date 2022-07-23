package com.boot.user.tests.controller;

import com.boot.user.controller.UserController;
import com.boot.user.dto.UserDTO;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {UserService.class, UserController.class})
@ActiveProfiles("test")
public class UserControllerTest {

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
    public void addUser() throws Exception {

        UserDTO userDTO = getUserDTO();
        String requestJson = objectWriter.writeValueAsString(userDTO);

        mockMvc.perform(post("http://localhost:8080/")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .string("{\"firstName\":\"testName\",\"lastName\":\"testLastName\",\"password\":\"testPassword\"," +
                                "\"phoneNumber\":\"0742000000\",\"email\":\"jon278@gaailer.site\",\"deliveryAddress\":\"stret, no. 1\"}"));

        verify(userService).addUser(userDTO);
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
