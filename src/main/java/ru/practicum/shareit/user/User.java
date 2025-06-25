package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

    private Long id;

    @NotBlank
    private String name;

    @Email(message = "Неверный формат email")
    @NotBlank
    private String email;
}
