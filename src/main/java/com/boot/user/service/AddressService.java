package com.boot.user.service;

import com.boot.user.dto.AddressDTO;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.model.Address;
import com.boot.user.model.User;
import com.boot.user.repository.AddressRepository;
import com.boot.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class AddressService {
    private AddressRepository addressRepository;
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    public AddressDTO save(long userId, AddressDTO dto) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("Invalid User-Id passed!");
        }

        Address savedModel = null;
        if (user.getAddress() != null) {
            Address model = modelMapper.map(dto, Address.class);
            modelMapper.map(model, user.getAddress());
            savedModel = addressRepository.save(user.getAddress());
        } else {
            Address model = modelMapper.map(dto, Address.class);
            model.setUser(user);
            savedModel = addressRepository.save(model);
        }

        return modelMapper.map(savedModel, AddressDTO.class);
    }
}
