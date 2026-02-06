package com.sup.event_management.repository;

import com.sup.event_management.entity.CheckIn;
import com.sup.event_management.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    Optional<CheckIn> findByTicket(Ticket ticket);
}
