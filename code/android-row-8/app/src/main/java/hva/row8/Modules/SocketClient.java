package hva.row8.Modules;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hva.row8.Interfaces.DataReceiveListener;

/**
 * Created by Mats-Mac on 06-10-15.
 */
public class SocketClient {
	String url;
	int port;
	DataOutputStream outputStream;
	Socket socket;
	private List<DataReceiveListener> listeners = new ArrayList<DataReceiveListener>();

	public SocketClient(String url, int port) {
		this.url = url;
		this.port = port;
	}
	public SocketClient(String url) {
		this.url = url;
		this.port = 1212;
	}
	public SocketClient() {
		this.url = "pi.akoo.nl";
		this.port = 1212;
	}
	public void setUp() {
		try
		{
			this.socket = new Socket(this.url, this.port);
			final InputStream is = this.socket.getInputStream();
			this.outputStream = new DataOutputStream(this.socket.getOutputStream());

			Thread readThread = new Thread(){
				public void run(){
					byte[] buffer = new byte[1025];
					int length = 0;
					try {
						while((length = is.read(buffer)) > 0) {

							for(DataReceiveListener dataReceiveListener : listeners)
								dataReceiveListener.onDataReceive(buffer[0], Arrays.copyOfRange(buffer, 1, length));

							buffer = new byte[1025];
						}
						if(length == -1) {
							for(DataReceiveListener dataReceiveListener : listeners)
								dataReceiveListener.onConnectionDrop();
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			};

			readThread.start();
		}catch(IOException e) {
			for(DataReceiveListener dataReceiveListener : listeners)
				dataReceiveListener.onConnectionDrop();

			e.printStackTrace();
		}
	}
	public void stop() throws IOException {
		this.socket.close();
	}

	public void write(int module, byte[] data) throws Exception {
		byte[] dataToWrite = new byte[data.length + 1];

		System.arraycopy(new byte[]{ (byte)module }, 0, dataToWrite, 0, 1); // concat data
		System.arraycopy(data, 0, dataToWrite, 1, data.length); // concat data

		this.outputStream.write(dataToWrite);
		this.outputStream.flush();
	}

	public void reconnect(int amount) {
		if(amount <= 0)
			return;

		try {
			setUp();
		} catch (Exception e) {
			try{
				Thread.sleep(1000);
			} catch (Exception te) {
				System.out.println("Cannot sleep thread.");
			} finally {
				reconnect(amount - 1);
			}
		}
	}

	public void addListener(DataReceiveListener dataReceiveListener) {
		listeners.add(dataReceiveListener);
	}
}
