package tp;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Router{
	
	//router config
	String name;
	int sendPort;
	int receivePort;
	ArrayList<Link> links;
	ArrayList<Link> neighbor;
	
	//LoggerInfo
	
	public DatagramPacket PacketToSend;
	public String lastReceive;
	
	public Boolean LSPSent = false;
	public Boolean sync = false;
	public Boolean syncronizing = false;
	public String state = "";
	
	int num;
	boolean valueSet = false;
	
	public Router(String name, int receivePort, int sendPort, ArrayList<Link> links) {
		this.name = name;
		this.receivePort = receivePort;
		this.sendPort = sendPort;
		this.links = links;
		neighbor = links;
	}
	
	public synchronized void put(int num) {
		
		while(this.valueSet) {
			try {
				wait();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("ROUTER_" + this.name + " Receiving: " + num);
		this.num = num;
		
		this.valueSet = true;
		notify(); //Notify Sender that data is ready for it to send
	}
	//Send will have to get the data it has to sends
	public synchronized void get() {
		while(!valueSet) {
			try {
				System.out.println("send wait");
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("ROUTER_" + this.name + " is Sending: " + this.lastReceive);
		
		this.valueSet = false;

		notify(); //Notify Receiver that we got the data
		
	}
	
	public synchronized ArrayList<Link> getLinkList(){
		return this.links;
	}
	


	public synchronized  ArrayList<DatagramPacket> createLinksPacketList(int to_port) throws UnknownHostException {
		
		ArrayList<DatagramPacket> packetList = new ArrayList<DatagramPacket>();
		
		for(Link link : this.neighbor) {
			packetList.add(this.createLinksPacket(to_port));
		}
		
		return packetList;
		
	}
	
	
	public synchronized DatagramPacket createLinksPacket(int receiverPort) throws UnknownHostException {
		

			InetAddress ip = InetAddress.getLocalHost();

			//CREATE THE HEADER
			String header = "LSP" + "&";
			
			for(int i = 0; i < this.links.size(); i++) {
				header += this.links.get(i).from_port + "," + this.links.get(i).to_port + "," + this.links.get(i).cost + ";";
				
			}
			
			header += ";;";
			
			byte buf[] = header.getBytes();
			
			return new DatagramPacket(buf, buf.length, ip, receiverPort);

	}
	
	public synchronized DatagramPacket createAckPacket(DatagramPacket packetToACK) throws UnknownHostException {
		String ACKstring = "ACK";

		
		byte buf[] = ACKstring.getBytes();
		InetAddress ip = InetAddress.getLocalHost(); 
		
		return  new DatagramPacket(buf, buf.length, ip, packetToACK.getPort());
		
	}
	
}
