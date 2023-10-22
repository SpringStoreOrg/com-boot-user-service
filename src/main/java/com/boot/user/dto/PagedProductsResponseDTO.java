package com.boot.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PagedProductsResponseDTO {
    private List<ProductDTO> products;
    private int totalItems;
    private int totalPages;
    private int currentPage;
}
