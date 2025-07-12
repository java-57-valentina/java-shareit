package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE " +
            "b.item.ownerId = :ownerId AND (" +
            "(:state = 'ALL') OR " +
            "(:state = 'WAITING' AND b.status = 'WAITING') OR " +
            "(:state = 'REJECTED' AND b.status = 'REJECTED') OR " +
            "(:state = 'CURRENT' AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP) OR " +
            "(:state = 'PAST' AND b.end < CURRENT_TIMESTAMP) OR " +
            "(:state = 'FUTURE' AND b.start > CURRENT_TIMESTAMP)" +
            ") ORDER BY b.start DESC")
    Collection<Booking> findByStateAndOwner(
            @Param("state") String state,
            @Param("ownerId") Long ownerId
    );

    @Query("SELECT b FROM Booking b WHERE " +
            "b.booker.id = :bookerId AND (" +
            "(:state = 'ALL') OR " +
            "(:state = 'WAITING' AND b.status = 'WAITING') OR " +
            "(:state = 'REJECTED' AND b.status = 'REJECTED') OR " +
            "(:state = 'CURRENT' AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP) OR " +
            "(:state = 'PAST' AND b.end < CURRENT_TIMESTAMP) OR " +
            "(:state = 'FUTURE' AND b.start > CURRENT_TIMESTAMP)" +
            ") ORDER BY b.start DESC")
    Collection<Booking> findByStateAndBooker(
            @Param("state") String state,
            @Param("bookerId") Long bookerId
    );

    @Query("SELECT b FROM Booking b WHERE " +
            "b.booker.id = :bookerId " +
            "AND b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end <= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<Booking> findApprovedPastByBookerIdAndItemId(
            @Param("bookerId") Long bookerId,
            @Param("itemId") Long itemId
    );

    Optional<Booking> findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(
            Long itemId,
            LocalDateTime dateTime,
            Collection<Status> statuses);

    Optional<Booking> findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(
            Long itemId,
            LocalDateTime dateTime,
            Collection<Status> statuses);

}
