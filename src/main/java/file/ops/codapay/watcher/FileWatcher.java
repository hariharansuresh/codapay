package file.ops.codapay.watcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import file.ops.codapay.controller.RecordFeeder;

/**
 * 
 * @author hariharansuresh
 *
 */
@Component
public class FileWatcher implements CommandLineRunner {

	@Autowired
	private RecordFeeder recordFeeder;

	private static final Logger logger = LoggerFactory.getLogger(FileWatcher.class);

	public void watchFolder(String filePath) throws IOException, InterruptedException, URISyntaxException {

		WatchService watchService = FileSystems.getDefault().newWatchService();
		Path path = Paths.get(filePath);

		path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

		WatchKey key;
		while ((key = watchService.take()) != null) {
			for (WatchEvent<?> event : key.pollEvents()) {

				Path dir = (Path) key.watchable();
				Path file = dir.resolve(event.context().toString());

				try (BufferedReader reader = new BufferedReader(
						new FileReader(new File(file.toAbsolutePath().toString())));) {
					String header = reader.readLine();
					Files.lines(file).skip(1).forEach(o -> {
						try {
							recordFeeder.createMessage(header, o);
						} catch (MessagingException | JsonProcessingException e) {
							logger.error(e.getMessage(), e);
						}
					});
				}

				logger.info("Event:" + event.kind() + ". File: " + event.context());
			}
			key.reset();
		}
	}

	/**
	 * Command line runner for registering the folder to locate the CSV inputs.
	 */
	public void run(String... args) throws Exception {

		if (args.length == 1)
			watchFolder(args[0]);
		else {
			logger.error("Provide the CSV Parent File Path to observe.");
		}
	}
}
