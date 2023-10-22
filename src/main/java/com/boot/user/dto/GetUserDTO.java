package com.boot.user.dto;

import com.boot.user.model.UserFavorite;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class GetUserDTO extends CreateUserDTO{
    private AddressDTO address;
    @Schema(description = "User role",
            example = "USER")
    private List<String> roles;

    private List<ProductDTO> userFavorites;

    private boolean verified;
}
