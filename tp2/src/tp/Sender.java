package tp;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Sender implements Runnable{
	
	Router router;
	byte[] receive = new byte[65535];
	DatagramPacket receivedPacket = new DatagramPacket(receive, receive.length);
	
	public Sender(Router router) 
	{
		this.router = router;
		Thread thread = new Thread(this,"Sender");
		thread.start();
	}

	@Override
	public synchronized void  run() 
	{
		
		byte[] send = new byte[65535];
		DatagramPacket packetToSend;
		DatagramPacket LSPdataPacket;
		DatagramSocket Socket;
		Boolean sent = false;
		try {
			//this.router.get();  //wait
			Socket = new DatagramSocket(this.router.sendPort);
			
			
			while(true) {
				
				this.router.state = "Sending";
				
				if(!this.router.sync) {
					//SEND PACKET TO NEIGHBOR
					//FIRST WE CREATE A PACKET CONTAINING OUR NEIGHBOR INFO 
					
					ArrayList<Link>  neighbor = this.router.neighbor;
					
					
					//SEND MY INFORMATION TO EACH NEIGHBOR
//					for(int i = 0; i < neighbor.size(); i++) {
//						this.router.createLinksPacket(neighbor.get(i).to_port);
//						Socket.setSoTimeout(999);
//						sent = false;
//					}
					

					for(int i = 0; i < neighbor.size(); i++) {
						packetToSend = this.router.createLinksPacket(neighbor.get(i).to_port);
						Socket.setSoTimeout(999);
						sent = false;
						while(!sent) {
							try {
								receivedPacket = new DatagramPacket(receive, receive.length);
								//SEND ACK
								
								//System.out.println(this.router.state + " " +this.router.name + neighbor.get(i).to_port);
								
								if(this.router.sync) {
									break;
								}
								Socket.send(packetToSend);
								//WAIT FOR ACK
								Socket.receive(receivedPacket);
								sent = true;
							} catch (IOException e) {
								//ON TIMEOUT RESEND
								continue;
							}
						}
						
					}
					
				}
				
			}
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}

}
