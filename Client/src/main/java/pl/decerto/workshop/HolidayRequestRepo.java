package pl.decerto.workshop;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRequestRepo extends JpaRepository<HolidayRequest, Long> {
}
