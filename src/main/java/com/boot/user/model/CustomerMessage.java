package com.boot.user.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
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

    @Size(min = 3, message = "Min Last name size is 3 characters!")
    @Size(max = 50, message = "Max Last name size is 50 characters!")
    @Column
    private String name;

    @Email(message = "Invalid Email!")
    @Size(min = 3, message = "Min email size is 3 characters!")
    @Size(max = 100, message = "Max email size is 100 characters!")
    @Column
    private String email;

    @Pattern(regexp="^(?=[07]{2})(?=\\d{10}).*", message = "Invalid Phone Number!")
    @Column
    private String phoneNumber;

    @Size(min = 2, message = "Min comment size is 2 characters!")
    @Size(max = 550, message = "Max comment size is 600 characters!")
    @Column
    private String comment;

    @Column
    private LocalDateTime createdOn;

    @PrePersist
    public void create(){
        this.createdOn = LocalDateTime.now();
    }
}
