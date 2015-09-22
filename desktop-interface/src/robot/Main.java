package robot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Main  {
    public static void main(String[] args) {
		/*try {
			int sentence;
			String modifiedSentence;
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			Socket clientSocket = new Socket("pi.akoo.nl", 1212);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			while(true) {
				String input = inFromUser.readLine();
				if(input != "q") {
					for(int i =0; i <= 254; i++) {
						outToServer.writeByte(i);
						Thread.sleep(500);
						System.out.println(i);
					}
					outToServer.writeByte(0);
					clientSocket.close();

					//sentence = Integer.parseInt(input);
					//outToServer.writeByte(sentence);
				} else {
					clientSocket.close();
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
		}*/


        SocketClient sc = new SocketClient();
        sc.setUp();

        try {
			boolean running = true;
			while(running) {
				BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
				String input = inFromUser.readLine();
				if(input != "q") {
//					int inputParsed = Integer.parseInt(input);
					sc.write(input);
				} else {
					running = false;
				}
			}

            sc.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
