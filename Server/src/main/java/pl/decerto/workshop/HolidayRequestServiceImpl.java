package pl.decerto.workshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Component;


@Component
public class HolidayRequestServiceImpl implements HolidayRequestService {

	private final CounterService counterService;
	private final HolidayRequestRepo holidayRequestRepo;

	@Autowired
	public HolidayRequestServiceImpl(CounterService counterService, HolidayRequestRepo holidayRequestRepo) {
		this.counterService = counterService;
		this.holidayRequestRepo = holidayRequestRepo;
	}

	@Override
	public Long getRequestCount() {
		counterService.increment("services.system.holidayService.invoked");
		return holidayRequestRepo.count();
	}

}
