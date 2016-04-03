package pl.decerto.workshop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class JmsClient {

	private JmsTemplate jmsTemplate;
	private ObjectMapper objectMapper;


	@Autowired
	public JmsClient(JmsTemplate jmsTemplate, ObjectMapper objectMapper) {
		this.jmsTemplate = jmsTemplate;
		this.objectMapper = objectMapper;
	}

	public void send(HolidayRequest request) throws JsonProcessingException {

		String message = objectMapper.writeValueAsString(request);

		MessageCreator messageCreator = new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message);
			}
		};
		jmsTemplate.send("mailbox-destination", messageCreator);
	}


}
