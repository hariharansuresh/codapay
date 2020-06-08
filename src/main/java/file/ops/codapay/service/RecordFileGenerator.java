package file.ops.codapay.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author hariharansuresh
 *
 */
@Service
public class RecordFileGenerator {

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Generate JSON file.
	 * 
	 * @param content
	 * @param location
	 * @param fileName
	 * @throws IOException
	 */
	public void generateJson(List<Map<String, String>> content, String location, String fileName) throws IOException {
		try (final ByteArrayOutputStream outStream = new ByteArrayOutputStream();) {
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(outStream, content);
			String fileContent = new String(outStream.toByteArray(), StandardCharsets.UTF_8);
			Files.write(Paths.get(location + File.separator + fileName + ".json"), fileContent.getBytes());
		}
	}

	/**
	 * Generate XML
	 * 
	 * @param content
	 * @param location
	 * @param fileName
	 * @return
	 */
	public String generateXML(List<Map<String, String>> content, String location, String fileName) {

		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element root = document.createElement("root");
			document.appendChild(root);

			for (Map<String, String> data : content) {
				Element detail = document.createElement("detail");
				root.appendChild(detail);
				for (Map.Entry<String, String> values : data.entrySet()) {
					Element entry = document.createElement("entry");
					entry.setAttribute(values.getKey(), values.getValue());
					detail.appendChild(entry);
				}
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(location + File.separator + fileName + ".xml"));
			transformer.transform(domSource, streamResult);

		} catch (ParserConfigurationException | TransformerException pce) {
			pce.printStackTrace();
		}

		return null;
	}
}
