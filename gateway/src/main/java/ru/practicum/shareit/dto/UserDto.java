package ru.practicum.shareit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {
    private Integer id;
    @NotBlank
    private String name;
    @NotNull
    @Email
    private String email;
}