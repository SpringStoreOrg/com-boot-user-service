
package com.boot.user.validator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boot.user.repository.UserRepository;


@Service
public class UserValidator {

    @Autowired
    private UserRepository userRepository;

    public boolean isEmailPresent(String email) {
        if (userRepository.getUserByEmail(email) == null) return true;
        return false;
    }

    public boolean isIdPresent(long id) {
        if (userRepository.getUserById(id) == null) return false;
        return true;
    }
}
