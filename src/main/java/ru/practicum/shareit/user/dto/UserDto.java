package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.validation.Create;

@Data
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(groups = Create.class)
    private String name;

    @NotBlank(groups = Create.class)
    @Email(groups = Create.class, message = "Invalid email format")
    private String email;
}
