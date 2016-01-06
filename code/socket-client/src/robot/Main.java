package robot;

import robot.listeners.DataReceiveListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main  {
    public static void main(String[] args) {
        SocketClient sc = new SocketClient("pi6.akoo.nl", 1212);
        sc.setUp();

		sc.addListener(new DataReceiveListener() {
			@Override
			public void onDataReceive(int module, byte[] data) {
				try {
					System.out.println("Module: " + module);
					System.out.println("Data: " + new String(data, "UTF-8"));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});

        try {
			boolean running = true;
			while(running) {
				BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
				String input = inFromUser.readLine();
				if(input != "q") {
					//int inputParsed = Integer.parseInt(input);
					sc.write(10, input.getBytes());
					//sc.write(input);
				} else {
					running = false;
				}
			}

            sc.stop();
        } catch (Exception e) {
            e.printStackTrace();
			sc.reconnect(5);
        }


    }
}
