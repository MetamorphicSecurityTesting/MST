package smrl.mr.automotive;

public class CANMessage implements Cloneable {

	private String message;

	public CANMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
