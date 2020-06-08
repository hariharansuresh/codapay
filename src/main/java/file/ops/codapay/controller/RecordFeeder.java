package file.ops.codapay.controller;

import javax.jms.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import file.ops.codapay.model.Record;

/**
 * 
 * @author hariharansuresh
 *
 */
@Component
public class RecordFeeder {

	private static final Logger logger = LoggerFactory.getLogger(RecordFeeder.class);

	@Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;

	@Autowired
	private Queue queue;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Producer called by the watch service to enqueue content.
	 * 
	 * @param header
	 * @param content
	 * @throws MessagingException
	 * @throws JsonProcessingException
	 */
	public void createMessage(String header, String content) throws MessagingException, JsonProcessingException {
		Record record = new Record(header, content);
		this.jmsMessagingTemplate.convertAndSend(this.queue, objectMapper.writeValueAsString(record));
		logger.debug(content);
	}
}
