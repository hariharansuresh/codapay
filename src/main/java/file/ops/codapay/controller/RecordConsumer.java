package file.ops.codapay.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import file.ops.codapay.model.Record;
import file.ops.codapay.service.RecordService;

/**
 * 
 * @author hariharansuresh
 *
 */
@Component
public class RecordConsumer {

	@Autowired
	private RecordService recordService;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${output.loc}")
	private String outputLocation;

	private static final Logger logger = LoggerFactory.getLogger(RecordConsumer.class);

	/**
	 * Consume the CSV records in reactive way.
	 * 
	 * @param text
	 */
	@JmsListener(destination = "${file.queue.name}")
	public void processRecord(String text) {
		logger.info("Message Received: " + text);
		Record record = null;
		try {
			record = objectMapper.readValue(text, Record.class);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		}
		recordService.createRecord(record.getHeader(), record.getContent(), outputLocation);
	}
}
