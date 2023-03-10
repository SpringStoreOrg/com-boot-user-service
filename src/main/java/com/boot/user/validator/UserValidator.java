
package com.boot.user.validator;

import com.boot.user.repository.UserRepository;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@NoArgsConstructor
public class UserValidator {

    @Autowired
    private UserRepository userRepository;

    public boolean isEmailPresent(String email) {
        return userRepository.getUserByEmail(email) != null;
    }

}
