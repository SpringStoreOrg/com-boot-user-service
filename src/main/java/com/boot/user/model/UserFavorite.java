package com.boot.user.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@ToString
@Table(name = "user_favorite")
public class UserFavorite {

    @Schema(description = "Unique identifier of the user favorite product.",
            example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;


    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude private User user;

    @Schema(description = "Name of the product.",
            example = "Black core wood chair")
    @Column(name = "product_name")
    private String productName;


}