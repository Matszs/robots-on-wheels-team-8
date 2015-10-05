package robot;

import robot.listeners.DataReceiveListener;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        this.url = "192.168.1.100";
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

							//for(int i = 0; i < length; i++)
							//	System.out.printf("%s", buffer[i]);

							//System.out.println("TEST " + length);
							//System.out.println(new String(buffer, "UTF-8"));

							for(DataReceiveListener dataReceiveListener : listeners)
								dataReceiveListener.onDataReceive(buffer[0], Arrays.copyOfRange(buffer, 0, length));

							buffer = new byte[1025];
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			};

			readThread.start();
        }catch(IOException e) {
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
