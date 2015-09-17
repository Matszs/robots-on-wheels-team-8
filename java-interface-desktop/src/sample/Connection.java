package sample;

/**
 * Created by Mitchel- on 14-9-2015.
 */

import java.net.*;
import java.io.*;
import java.awt.event.*;

public class Connection
{
    // declare low level and high level objects for input
    private InputStream inStream;
    private DataInputStream inDataStream;

    // declare low level and high level objects for output
    private OutputStream outStream;
    private DataOutputStream outDataStream;

    // declare socket
    private Socket connection;

    // declare attribute to told details of remote machine and port
    private String remoteMachine;
    private int port;

    // constructor
    public Connection(String remoteMachineIn, int portIn){
        remoteMachine = remoteMachineIn;
        port= portIn;

        //start the helper method that starts the client
        startClient();
    }

    private void startClient() {
        // TODO Auto-generated method stub
        try{
            // attempt to create a connection to the server
            connection = new Socket(remoteMachine,port);
            //msg.setText("connection establish");

            // create an input stream from the server
            inStream = connection.getInputStream();
            inDataStream = new DataInputStream(inStream);

            //create output stream to the server
            outStream = connection.getOutputStream();
            outDataStream = new DataOutputStream(outStream);

            //send the host IP to the server
            outDataStream.writeUTF(connection.getLocalAddress().getHostAddress());

        }catch (UnknownHostException e){
            //msg.setText("Unknown host");
        }
        catch (IOException except){
            //msg.setText("Network Exception");
        }
    }

    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        try{
            // send the two integers to the server
            //outDataStream.writeInt(Integer.parseInt();
            //outDataStream.writeInt(Integer.parseInt();

            //read and display the results sent back from the server
            //String results= inDataStream.readUTF();
            int results= inDataStream.readInt();
            //sum.setText(""+results);
        }catch(IOException ie){
            ie.printStackTrace();
        }
    }
}
