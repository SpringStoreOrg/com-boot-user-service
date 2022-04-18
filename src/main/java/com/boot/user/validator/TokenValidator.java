package com.boot.user.validator;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class TokenValidator {

    public boolean checkTokenAvailability(Date date) {

        return LocalDateTime.now().isBefore(Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().plusHours(1));
    }
}
