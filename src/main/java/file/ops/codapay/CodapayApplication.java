package file.ops.codapay;

import javax.jms.Queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author hariharansuresh
 *
 */
@SpringBootApplication
@EnableJms
public class CodapayApplication {
	public static void main(String[] args) {
		SpringApplication.run(CodapayApplication.class, args);
	}

	@Value("${file.queue.name}")
	private String queueName;

	@Bean("queue")
	public Queue registerQueue() {
		return new ActiveMQQueue(queueName);
	}

	@Bean("objectMapper")
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
