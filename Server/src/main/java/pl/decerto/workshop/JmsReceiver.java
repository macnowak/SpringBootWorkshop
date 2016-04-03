package pl.decerto.workshop;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class JmsReceiver {

	private ObjectMapper objectMapper;
	private HolidayRequestRepo holidayRequestRepo;


	@Autowired
	public JmsReceiver(ObjectMapper objectMapper, HolidayRequestRepo holidayRequestRepo) {
		this.objectMapper = objectMapper;
		this.holidayRequestRepo = holidayRequestRepo;
	}


	@JmsListener(destination = "mailbox-destination")
	public void receiveMessage(String message) throws IOException {
		HolidayRequest request = objectMapper.readValue(message, HolidayRequest.class);
		System.out.println("Received <" + request + ">");
		holidayRequestRepo.save(request);
	}
}


