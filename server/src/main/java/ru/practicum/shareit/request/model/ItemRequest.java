package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.responce.model.ItemResponse;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)  // Загрузка по требованию
    @JoinColumn(name = "user_id", nullable = false)  // Столбец в БД
    @ToString.Exclude
    private User requester;

    @Column(name = "created", nullable = false)
    private LocalDateTime createdAt;

    // Добавляем связь с responses
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemResponse> responses = new ArrayList<>();
}
