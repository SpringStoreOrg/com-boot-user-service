
package com.boot.user.validator;


import com.boot.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserValidator {

    private UserRepository userRepository;

    public boolean isEmailPresent(String email) {
        return userRepository.getUserByEmail(email) == null;
    }

    public boolean isIdPresent(long id) {
        return userRepository.getUserById(id) == null;
    }
}
