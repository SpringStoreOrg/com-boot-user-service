package com.boot.user.validator;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class TokenValidator {

	public boolean checkTokenAvailability(Date date) throws ParseException {
		//You could implement this logic using:
		//LocalDateTime.now().isBefore(Instant.ofEpochMilli(date.getTime())
                //.atZone(ZoneId.systemDefault())
                //.toLocalDateTime().plusHours(1))
		Date now = Calendar.getInstance().getTime(); // Get time now
		long differenceInMillis = now.getTime() - date.getTime();
		long differenceInHours = (differenceInMillis) / 1000L / 60L / 60L; // Divide by millis/sec, secs/min, mins/hr
		return (int) differenceInHours > 1 ? true : false;
	}
}
