package com.boot.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class PhotoDTO {

    String image;
}
