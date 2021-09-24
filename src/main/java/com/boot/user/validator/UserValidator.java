
package com.boot.user.validator;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boot.user.repository.UserRepository;


@Service
public class UserValidator {

	@Autowired
	private UserRepository userRepository;

	public boolean isEmailValid(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" +
			"(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";

		Pattern pat = Pattern.compile(emailRegex);
		if (email == null) return false;
		return pat.matcher(email).matches();
	}
	
	public boolean isEmailPresent(String email) {
		if (userRepository.getUserByEmail(email) == null) return true;
		return false;
	}
	
	public boolean isIdPresent(long id) {
		if (userRepository.getUserById(id) == null) return false;
		return true;
	}

	public boolean isUserDataSizeCorrect(String userData, int min, int max) {
	if(userData == null||userData.length()< min ||userData.length()> max)	
		return false;
	return true;
	}
	
	public boolean isPhoneNumberValid(String phoneNumber) {
		String phoneNumberRegex = "^(?=(?:[07]){2})(?=[0-9]{10}).*";

		Pattern pat = Pattern.compile(phoneNumberRegex);
		if (phoneNumber == null) return false;
		return pat.matcher(phoneNumber).matches();
	}
}
