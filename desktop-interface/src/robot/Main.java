package robot;

import java.io.IOException;

public class Main  {
    public static void main(String[] args) {
        SocketClient sc = new SocketClient();
        sc.setUp();
        try {
            sc.write("left");
            sc.write("stop");
            sc.write("forward");
            sc.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
