package com.boot.user.validator;

import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class TokenValidator {

    public boolean checkTokenAvailability(@NotNull Date date) {

        return !LocalDateTime.now().isBefore(Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().plusHours(1));
    }
}
