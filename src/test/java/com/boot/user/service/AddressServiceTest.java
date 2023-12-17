package com.boot.user.service;

import com.boot.user.dto.AddressDTO;
import com.boot.user.dto.CreateUserDTO;
import com.boot.user.dto.GetUserDTO;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.model.Address;
import com.boot.user.model.User;
import com.boot.user.repository.AddressRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {
    @InjectMocks
    AddressService addressService;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<Address> addressCaptor;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(addressService, "modelMapper", getModelMapper());
    }

    @Test
    public void testSaveNewAddress(){
        AddressDTO addressDTO = getValidAddress();
        Address address = getModelMapper().map(getValidAddress(), Address.class);
        when(userRepository.getUserById(1)).thenReturn(getUser());
        when(addressRepository.save(any())).thenReturn(address);
        AddressDTO result = addressService.save(1, getValidAddress());
        verify(addressRepository).save(addressCaptor.capture());
        assertEquals(address.getCountry(), addressCaptor.getValue().getCountry());
        assertEquals(address.getCounty(), addressCaptor.getValue().getCounty());
        assertEquals(address.getCity(), addressCaptor.getValue().getCity());
        assertEquals(address.getEmail(), addressCaptor.getValue().getEmail());
        assertEquals(address.getPhoneNumber(), addressCaptor.getValue().getPhoneNumber());
        assertEquals(address.getPostalCode(), addressCaptor.getValue().getPostalCode());
        assertEquals(address.getStreet(), addressCaptor.getValue().getStreet());
        assertEquals(addressDTO, result);
    }

    @Test
    public void testSaveWithUpdateAddress(){
        Address address = getModelMapper().map(getValidAddress(), Address.class);
        User user = getUser();
        user.setAddress(address);
        AddressDTO addressDTO = getValidAddress();
        addressDTO.setCountry("CountryU");
        addressDTO.setCounty("CountyU");
        addressDTO.setCity("CityU");
        addressDTO.setPostalCode("50000");
        addressDTO.setPhoneNumber("0780000000");
        addressDTO.setStreet("StreetU");
        Address updated = getModelMapper().map(addressDTO, Address.class);

        when(userRepository.getUserById(1)).thenReturn(user);
        when(addressRepository.save(any())).thenReturn(address);
        AddressDTO result = addressService.save(1, addressDTO);
        verify(addressRepository).save(addressCaptor.capture());
        assertEquals(updated.getCountry(), addressCaptor.getValue().getCountry());
        assertEquals(updated.getCounty(), addressCaptor.getValue().getCounty());
        assertEquals(updated.getCity(), addressCaptor.getValue().getCity());
        assertEquals(updated.getEmail(), addressCaptor.getValue().getEmail());
        assertEquals(updated.getPhoneNumber(), addressCaptor.getValue().getPhoneNumber());
        assertEquals(updated.getPostalCode(), addressCaptor.getValue().getPostalCode());
        assertEquals(updated.getStreet(), addressCaptor.getValue().getStreet());
        assertEquals(addressDTO, result);
    }

    @Test
    public void testSaveForNotExistingUser(){
        when(userRepository.getUserById(1)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                addressService.save(1, getValidAddress()));

        assertEquals("Invalid User-Id passed!", exception.getMessage());

        verifyNoInteractions(addressRepository);

    }

    private ModelMapper getModelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(User.class, CreateUserDTO.class);
        modelMapper.typeMap(User.class, GetUserDTO.class);
        modelMapper.typeMap(Address.class, AddressDTO.class);
        modelMapper.addMappings(new PropertyMap<Address, Address>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getCreatedOn());
                skip(destination.getLastUpdatedOn());
                skip(destination.getUser());
            }
        });

        return modelMapper;
    }

    private User getUser() {
        User user = new User();

        user.setFirstName("testName")
                .setLastName("testLastName")
                .setPhoneNumber("0742000000")
                .setPassword("testPassword")
                .setEmail("jon278@gaailer.site");

        return user;
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
