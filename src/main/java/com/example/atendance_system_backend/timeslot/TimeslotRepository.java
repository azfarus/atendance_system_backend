package com.example.atendance_system_backend.timeslot;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimeslotRepository extends JpaRepository<Timeslot , Long> {
    Optional<Timeslot> findTimeslotByDayAndSliceAndRoomNumber(String day , Long slice , Long roomNumber);
}
