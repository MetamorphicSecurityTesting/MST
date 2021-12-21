package smrl.mr.automotive;

import java.util.ArrayList;
import java.util.List;

import smrl.mr.language.MRData;


public class CANMessageSequence extends MRData implements Cloneable {
	private ArrayList messages = new ArrayList<CANMessage>();
	
	public CANMessageSequence(List<CANMessage> messages) {
		messages.addAll( messages );
	}

	
	public List<CANMessage> messages() {
		return messages;
	}


}
