package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@ToString
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    public User(Long userId) {
        this.id = userId;
    }
}
