package smrl.mr.crawljax;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

//import org.apache.sshd.client.SshClient;
//import org.apache.sshd.client.channel.ClientChannel;
//import org.apache.sshd.client.channel.ClientChannelEvent;
//import org.apache.sshd.client.future.ConnectFuture;
//import org.apache.sshd.client.session.ClientSession;
//import org.apache.sshd.common.channel.Channel;
//
//import net.i2p.crypto.eddsa.EdDSAKey;

public class VMWrapper {

	private String vMname;
	private String admin;
	private String IP;

	/**
	 * Create a wrapper for a VM (it can work also with a real host).
	 * 
	 * 
	 * 
	 * ASSUMPTIONS:
	 * 	- the admin user has been configured for SHH connection withou a passwd (i.e., you copied public key into .ssh/authorized_keys)
	 *  - admin user can execute sudo without PWD, i.e., have the following in "visudo" %sudo   ALL=(ALL:ALL) NOPASSWD: ALL
	 * 
	 * vMname is not ealy needed
	 */
	public VMWrapper(String vMname, String IP, String admin) {
		this.vMname = vMname;
		this.IP = IP;
		this.admin = admin;
	}

	public void setNewDate(long millisDate) {
		//Assumption: the VM has ben configured to have a date ndependent from the host
		//VBoxManage setextradata "UbuntuJenkins 2.121.1" "VBoxInternal/Devices/VMMDev/0/Config/GetHostTimeDisabled" 1
		
		//sudo systemctl stop vboxadd-service
		
		//We set in seconds
		long future = millisDate/1000;
		
		
		///1000+1000;
		try {
			int exitCode = executeBashCommandOnVM(admin, IP, 22, 10000, 
					"sudo date -s '@"+future+"'\n exit\n"
//				"exit\n"
					);
			if ( exitCode != 0 ) {
				throw new RuntimeException("Not executed: setNewDate");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	public boolean copyFromVM( String orig, String dest) {
		try {
			//System.out.println("!!! ADMIN "+admin);
			return scpFromVM(admin, 
					  IP, orig, dest, 10000 ) == 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static int scpFromVM(String username, 
			  String host, String orig, String dest, long defaultTimeoutSeconds ) throws IOException {
		
		StringBuffer outputBuffer = new StringBuffer();
		StringBuffer errorBuffer = new StringBuffer();
		List<String> exec = new ArrayList<>();
		exec.add("scp");
		exec.add(username+"@"+host+":"+orig);
		exec.add(dest);
		
		int exitCode = ProcessRunner.run( exec, "", outputBuffer, errorBuffer, (int) defaultTimeoutSeconds );
		
		System.out.println(outputBuffer);
		System.out.println(errorBuffer);
		System.out.println("Exit code: "+exitCode);
		
		return exitCode;
	}
	
	public static int executeBashCommandOnVM(String username, 
			  String host, int port, long defaultTimeoutSeconds, String command) throws IOException {
		
		StringBuffer outputBuffer = new StringBuffer();
		StringBuffer errorBuffer = new StringBuffer();
		List<String> exec = new ArrayList<>();
		exec.add("ssh");
		exec.add(username+"@"+host);
		exec.add("-tt");
		exec.add("-p");
		exec.add(""+port);
		int exitCode = ProcessRunner.run( exec, command, outputBuffer, errorBuffer, (int) defaultTimeoutSeconds );
		
		return exitCode;
	}
//	public static void executeCommandOnVM(String username, String password, 
//			  String host, int port, long defaultTimeoutSeconds, String command) throws IOException {
//			    
//	        
//		
//			    SshClient client = SshClient.setUpDefaultClient();
//			    client.start();
//			    try {
//			    ConnectFuture conn = client.connect(username, host, port);
//			    
//			    	//.verify(defaultTimeoutSeconds, TimeUnit.SECONDS).getSession()) {
//			    	conn.await();
//			    	ClientSession session = conn.getSession();
//			        session.addPasswordIdentity(password);
//			        session.auth().isSuccess();
//			        //.verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
//			        
//			        
//			        		
////			        		ByteArrayOutputStream responseStream = new ByteArrayOutputStream(); 
////			        		
////			        		
////			        		
////			          ClientChannel channel = session.createChannel(ClientChannel.CHANNEL_SHELL)) {
////			        	
////			        	InputStream targetStream = new ByteArrayInputStream(command.getBytes());
////			        	
////			        	channel.setIn(targetStream);
////			            channel.setOut(responseStream);
////			            channel.setErr(responseStream);
////			            try {
//////			                channel.open().await(defaultTimeoutSeconds*1000);
////			                
//////			                channel.setIn(new NoCloseInputStream(System.in));
//////			                channel.setOut(new NoCloseOutputStream(System.out));
////			                channel.setErr(new NoCloseOutputStream(System.err));
////			                channel.open();
//////			                channel.waitFor(ClientChannel.CLOSED, 0);
////			                
////			                System.out.println("HERE ");
////			                System.out.println("HERE " + channel.isClosed());
////			                
////			                channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 
////			                TimeUnit.SECONDS.toMillis(defaultTimeoutSeconds));
////			                
////			                String responseString = new String(responseStream.toByteArray());
////			                System.out.println("\n\n\n"+responseString);
//			        		
//			                ClientChannel channel = session.createChannel(ClientChannel.CHANNEL_SHELL);
//			                try {
//			                ByteArrayOutputStream sent = new ByteArrayOutputStream();
//			                
//			                
//			               
//			                StringBuilder sb = new StringBuilder();
//			                sb.append("ls");
//			                sb.append("\n");
//			                sb.append("exit\n".getBytes());
//			                
//			                ByteArrayInputStream bi = new ByteArrayInputStream(sb.toString().getBytes());			                
//			                channel.setIn(bi);
//
//			                ByteArrayOutputStream out = new ByteArrayOutputStream();
//			                ByteArrayOutputStream err = new ByteArrayOutputStream();
//			                channel.setOut(out);
//			                channel.setErr(err);
//			                channel.open();
//
//
//
//
//			                channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 
//					                TimeUnit.SECONDS.toMillis(defaultTimeoutSeconds));
//			        		
//			            } finally {
//			                channel.close(false);
//			            }
//			        
//			    } finally {
//			        client.stop();
//			    }
//			}
	
//	public static void main(String s[]) throws IOException {
//		Class<EdDSAKey> a = net.i2p.crypto.eddsa.EdDSAKey.class;
//		long val = System.currentTimeMillis()/1000 + 1000;
//		
//		///1000+1000;
//		executeBashCommandOnVM("jenkinsuser", "192.168.56.102", 22, 10000, 
//				"sudo date -s '@"+val+"'\n exit\n"
////				"exit\n"
//				);
//		
//		
//	 
//	}
}
