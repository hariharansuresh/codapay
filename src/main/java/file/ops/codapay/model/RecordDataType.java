package file.ops.codapay.model;

/**
 * 
 * @author hariharansuresh
 *
 */
public enum RecordDataType {
	STRING("String"), NUMBER("Number"), PHONE("Phone number"), EMAIL("Email address"), NONE("None");

	private String value;

	private RecordDataType(String value) {
		this.value = value;
	}

	public static boolean contains(String inputType) {
		for (RecordDataType type : RecordDataType.values()) {
			if (type.name().equalsIgnoreCase(inputType)) {
				return true;
			}
		}
		return false;
	}

	public static RecordDataType getType(String value) {
		return RecordDataType.valueOf(value.toUpperCase());
	}

	public String getValue() {
		return this.value;
	}
}