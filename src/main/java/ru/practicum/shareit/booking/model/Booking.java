package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // Загрузка по требованию
    @JoinColumn(name = "item_id", nullable = false)  // Столбец в БД
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)  // Загрузка по требованию
    @JoinColumn(name = "user_id", nullable = false)  // Столбец в БД
    private User booker;

    @Column(name = "date_start", nullable = false)
    @JsonProperty("start")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @Column(name = "date_end")
    private LocalDateTime end;

    @Enumerated(EnumType.STRING)
    private Status status = Status.WAITING;
}
