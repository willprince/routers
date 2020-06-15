package tp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class Receiver implements Runnable{
	
	Router router;
	byte[] receive = new byte[65535];
	DatagramPacket receivedPacket = new DatagramPacket(receive, receive.length);
	
	public Receiver(Router router) 
	{
		this.router = router;
		Thread thread = new Thread(this,"Receiver");
		thread.start();
	}

	@Override
	public synchronized void run() 
	{
		DatagramSocket Socket;
		try {
			Socket = new DatagramSocket(this.router.receivePort);
			packetReader pReader;
			int count = 0;
			int duplicate = 0;
			Socket.setSoTimeout(999);
			ArrayList<Link> toAdd;
			while(true) 	
			{


				this.router.state = "Receiving";
				//Start handshaking
				try {
					receivedPacket = new DatagramPacket(receive, receive.length);
					Socket.receive(receivedPacket);
					//Send ack to send
					
					Socket.send(this.router.createAckPacket(receivedPacket));	
					
				} catch (IOException e) {
					//System.out.println(this.router.name +  " receiving timeout at port: " + this.router.receivePort);
					continue;
				}
				
				this.router.PacketToSend = receivedPacket;
				this.router.lastReceive = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
				
				

				if(!this.router.syncronizing && this.router.lastReceive.substring(0,3).equals("LSP") ) {
					this.router.syncronizing = true;
				}
				
				if(this.router.lastReceive.length() > 3) {
					pReader = new packetReader(receivedPacket);
					
					//Check if links are equals
					count = 0;
					duplicate = 0;
					toAdd = new ArrayList<Link>();
					for(Link l : pReader.links) {
						duplicate = 0;
						for(Link lRouter : this.router.links) {

							if(l.equals(lRouter)) {
								count ++;
								duplicate ++;
							}
						}
						if(duplicate == 0) {
							toAdd.add(l);
						}
					}
					//System.out.println(this.router.name + "count: " + count);
					//System.out.println(this.router.name + "pReader: " + pReader.links.size());
					
					if(count >= pReader.links.size()) {
						this.router.sync = true;
						this.router.syncronizing = false;

					}else {
						for(Link l : toAdd) {
							this.router.links.add(l);

						}
					}
					
					
				}
				Thread.sleep(999);
				
				//this.router.put(1);
			} 
		} catch (SocketException e) {
			e.printStackTrace();
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		}
		
	}


