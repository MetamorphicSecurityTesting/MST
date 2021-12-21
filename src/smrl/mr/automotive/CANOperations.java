package smrl.mr.automotive;

import java.util.List;

import smrl.mr.language.MR;
import smrl.mr.language.MRDataProvider;

public class CANOperations {


	public static List<CANMessageSequence> CANMessageSequences(){
		return MR.CURRENT.getMRData("CANMessageSequence");
	}
	
	@MRDataProvider
	public static CANMessageSequence CANMessageSequence(){ 
		return (smrl.mr.automotive.CANMessageSequence) MR.CURRENT.getMRData("CANMessageSequence",0);
	}
	
	@MRDataProvider
	public static CANMessageSequence CANMessageSequence(int x){ 
		return (smrl.mr.automotive.CANMessageSequence) MR.CURRENT.getMRData("CANMessageSequence",x);
	}
	
	public static boolean isSecurityAccessRequest( CANMessage m ) {
		return m.getMessage().startsWith("27");
	}
	
	public static List<CANMessageSequence> CANOutput( CANMessageSequence s ) {
		return null;
	}
}
