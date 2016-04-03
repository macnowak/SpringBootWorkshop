package pl.decerto.workshop;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Maciek on 03.04.2016.
 */
public interface HolidayRequestRepo extends MongoRepository<HolidayRequest, String> {
}
