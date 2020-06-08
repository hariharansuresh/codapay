package file.ops.codapay.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author hariharansuresh
 *
 */
public class Record implements Serializable {

	private static final long serialVersionUID = 4411023574633161239L;

	public Record() {
	}

	@JsonProperty("header")
	private String header;

	@JsonProperty("content")
	private String content;

	public Record(String header, String content) {
		this.header = header;
		this.content = content;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
