package com.boot.user.controller;

import com.boot.user.dto.AddressDTO;
import com.boot.user.dto.CreateUserDTO;
import com.boot.user.service.AddressService;
import com.boot.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AddressController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AddressControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AddressService addressService;

    @Test
    void testAddAddressSuccess() throws Exception {

        AddressDTO address = getValidAddress();

        when(addressService.save(1, address)).thenReturn(address);

        mockMvc.perform(post("/address")
                .header("User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isOk());

        verify(addressService).save(1, address);
    }

    @Test
    void testAddAddressMissinguserIdHeader() throws Exception {

        AddressDTO address = getValidAddress();

        mockMvc.perform(post("/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(addressService);
    }

    @Test
    void testAddAddressMissingValues() throws Exception {

        AddressDTO address = new AddressDTO();

        mockMvc.perform(post("/address")
                .header("User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                        "   \"messages\":[\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"street\",\n" +
                        "         \"message\":\"must not be null\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"city\",\n" +
                        "         \"message\":\"must not be null\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"lastName\",\n" +
                        "         \"message\":\"must not be null\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"county\",\n" +
                        "         \"message\":\"must not be null\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"postalCode\",\n" +
                        "         \"message\":\"must not be null\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"email\",\n" +
                        "         \"message\":\"must not be null\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"firstName\",\n" +
                        "         \"message\":\"must not be null\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"country\",\n" +
                        "         \"message\":\"must not be null\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"phoneNumber\",\n" +
                        "         \"message\":\"must not be null\"\n" +
                        "      }\n" +
                        "   ]\n" +
                        "}"));

        verifyNoInteractions(addressService);
    }

    @Test
    void testAddAddressInvalidValues() throws Exception {

        AddressDTO address = new AddressDTO();
        address.setFirstName("Fi");
        address.setLastName("La");
        address.setEmail("abc");
        address.setPhoneNumber("072012345");
        address.setCountry("Ro");
        address.setCounty("Cl");
        address.setCity("Cl");
        address.setPostalCode("40000");
        address.setStreet("St");

        mockMvc.perform(post("/address")
                .header("User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                        "   \"messages\":[\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"phoneNumber\",\n" +
                        "         \"message\":\"Invalid Phone Number!\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"county\",\n" +
                        "         \"message\":\"Min County size is 3 characters!\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"postalCode\",\n" +
                        "         \"message\":\"Invalid Postal code\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"street\",\n" +
                        "         \"message\":\"Min Street size is 3 characters!\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"firstName\",\n" +
                        "         \"message\":\"Min First name size is 3 characters!\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"email\",\n" +
                        "         \"message\":\"Invalid Email!\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"country\",\n" +
                        "         \"message\":\"Min Country size is 4 characters!\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"fieldKey\":\"lastName\",\n" +
                        "         \"message\":\"Min Last name size is 3 characters!\"\n" +
                        "      }\n" +
                        "   ]\n" +
                        "}"));

        verifyNoInteractions(addressService);
    }

    private AddressDTO getValidAddress(){
        AddressDTO address = new AddressDTO();
        address.setFirstName("FirstName");
        address.setLastName("Lastname");
        address.setEmail("abc@yahoo.com");
        address.setPhoneNumber("0720123456");
        address.setCountry("Romania");
        address.setCounty("Cluj");
        address.setCity("Cluj-Napoca");
        address.setPostalCode("400000");
        address.setStreet("Str Unirii");
        return address;
    }
}
