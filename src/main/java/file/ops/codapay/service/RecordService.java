package file.ops.codapay.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author hariharansuresh
 *
 */
public interface RecordService {

	public String deriveDataType(String data);

	public void createRecord(String header, String data, String outputLoc);

	public void generateRecord(String fileType, List<Map<String, String>> content, String outputLoc, String fileName);

}
