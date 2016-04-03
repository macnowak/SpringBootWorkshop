package pl.decerto.workshop;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("request")
public class HolidayRequestEndpoint {

	private HolidayRequestRepo holidayRequestRepo;
	private JmsClient jmsClient;

	@Autowired
	public HolidayRequestEndpoint(HolidayRequestRepo holidayRequestRepo, JmsClient jmsClient) {
		this.holidayRequestRepo = holidayRequestRepo;
		this.jmsClient = jmsClient;
	}

	@RequestMapping(method = RequestMethod.POST)
	public void add(@RequestBody HolidayRequest holidayRequest) throws JsonProcessingException {
		holidayRequestRepo.save(holidayRequest);
		jmsClient.send(holidayRequest);
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<HolidayRequest> get() {
		return holidayRequestRepo.findAll();
	}

}
