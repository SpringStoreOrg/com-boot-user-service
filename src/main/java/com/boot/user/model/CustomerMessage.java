package com.boot.user.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "customerMessage")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
public class CustomerMessage {

    /**
     *
     */
    private static final long serialVersionUID = -2904101345253876784L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String name;

    @Size(min = 3, max = 50)
    @Column
    private String email;

    @Column
    private String phoneNumber;

    @Column
    private String comment;

    @Column
    private LocalDateTime createdOn;
}
