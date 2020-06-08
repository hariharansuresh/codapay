package file.ops.codapay.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import file.ops.codapay.model.FileTypes;
import file.ops.codapay.model.RecordDataType;

/**
 * 
 * @author hariharansuresh
 *
 */
@Service
public class RecordServiceImpl implements RecordService {

	@Autowired
	private RecordFileGenerator recordFileGenerator;

	private static final Logger logger = LoggerFactory.getLogger(RecordServiceImpl.class);

	/**
	 * Called by consumer of ActiveMQ
	 */
	@Override
	public void createRecord(String header, String data, String outputLoc) {

		String[] headers = null;
		String[] datum = null;
		boolean hasEmail = false;
		// String email = null; // TODO

		if (!StringUtils.isEmpty(header) && !StringUtils.isEmpty(data) && !StringUtils.isEmpty(outputLoc)) {

			headers = header.split(",");
			datum = data.split(",");

			if (headers.length == datum.length) {

				int counter = 0;
				Map<String, String> dataMap = null;
				List<Map<String, String>> dataList = new LinkedList<>();

				for (String headerDtl : headers) {

					dataMap = new LinkedHashMap<>();
					String dataType = deriveDataType(datum[counter]);

					switch (RecordDataType.getType(dataType)) {
					case STRING: {
						dataMap.put("dataType", RecordDataType.STRING.getValue());
						dataMap.put(headerDtl.trim(), datum[counter].trim());
						dataList.add(dataMap);
						break;
					}
					case NUMBER: {
						dataMap.put("dataType", RecordDataType.NUMBER.getValue());
						dataMap.put(headerDtl.trim(), datum[counter].trim());
						dataList.add(dataMap);
						break;
					}
					case PHONE: {
						String maskedPhone = maskedPhone(datum[counter]);
						if (!StringUtils.isEmpty(maskedPhone)) {
							dataMap.put("dataType", RecordDataType.PHONE.getValue());
							dataMap.put(headerDtl.trim(), "***-***-".concat(maskedPhone));
							dataList.add(dataMap);
						} else {
							logger.error(MessageFormat.format("DataType : Phone , {0}, last 4 digits not derived  {1}",
									datum[counter].trim(), maskedPhone));
						}
						break;
					}
					case EMAIL: {
						hasEmail = true;
						// email = datum[counter].trim();
						String updatedEmail = maskedEmail(datum[counter]);
						if (!StringUtils.isEmpty(updatedEmail)) {
							dataMap.put("dataType", RecordDataType.EMAIL.getValue());
							dataMap.put(headerDtl.trim(), updatedEmail);
							dataList.add(dataMap);
						} else {
							logger.error(MessageFormat.format("DataType : Email , {0}, cant derive email properly ",
									datum[counter].trim()));
						}
						break;
					}
					case NONE: {
						dataMap.put("dataType", RecordDataType.NONE.getValue());
						dataMap.put(headerDtl.trim(), datum[counter].trim());
						dataList.add(dataMap);
						break;
					}
					default: {
						break;
					}
					}
					counter++;
				}

				if (!dataList.isEmpty()) {
					UUID uuid = UUID.randomUUID();
					generateRecord(FileTypes.JSON.toString(), dataList, outputLoc, uuid.toString());
					generateRecord(FileTypes.XML.toString(), dataList, outputLoc, uuid.toString());
					if (hasEmail) {
						// TODO
						// register the email with UUID
					}
				}

			} else {
				logger.error("CONTENT LENGTH MISMATCH");
				logger.error(
						MessageFormat.format("header length : {0} - data length: {1} ", headers.length, datum.length));
			}
		} else {
			logger.error("CONTENT EMPTY");
			logger.error(MessageFormat.format("header : {0} - data : {1} - Output Loc : {2}", header, data, outputLoc));
		}
	}

	/**
	 * Called by the create Record for generating files
	 */
	@Override
	public void generateRecord(String fileType, List<Map<String, String>> content, String outputLoc, String fileName) {
		try {
			if (FileTypes.JSON.toString().equals(fileType)) {
				recordFileGenerator.generateJson(content, outputLoc, fileName);
			} else if (FileTypes.XML.toString().equals(fileType)) {
				recordFileGenerator.generateXML(content, outputLoc, fileName);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			logger.error(MessageFormat.format("Cant generate {0} file for UUID: {1}", fileType, fileName));
		}
	}

	@Override
	public String deriveDataType(String text) {

		if (text.matches("[a-zA-Z]+")) {
			return RecordDataType.STRING.toString();
		} else if (text.matches("\\d+")) {
			return RecordDataType.NUMBER.toString();
		} else if (Pattern.matches("[_a-zA-Z1-9]+(\\.[A-Za-z0-9]*)*@[A-Za-z0-9]+\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]*)*",
				text)) {
			return RecordDataType.EMAIL.toString();
		} else if (Pattern.matches("^\\d{3}-\\d{3}-\\d{4}$", text)) {
			return RecordDataType.PHONE.toString();
		}
		return RecordDataType.NONE.toString();
	}

	/**
	 * Helper for masking email.
	 * 
	 * @param data
	 * @return
	 */
	private String maskedEmail(String data) {
		if (!StringUtils.isEmpty(data)) {
			String email = data.trim().length() > 3 ? "***" + data.trim().substring(3)
					: data.trim().replaceAll("", "*");
			logger.info(email);
			return email;
		} else
			return null;
	}

	/**
	 * Helper for deriving phone number
	 * 
	 * @param data
	 * @return
	 */
	private String maskedPhone(String data) {
		String result = null;
		if (data.length() > 4) {
			result = data.substring(data.length() - 4);
		} else {
			result = data;
		}
		logger.info(result);
		return result;
	}
}
