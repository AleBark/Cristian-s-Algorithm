package ab.prog.christian;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 *
 * @author alebark
 */
public class Server {

    public static void main(String[] args) throws IOException, InterruptedException {

        ServerSocket skt = new ServerSocket(8080);

        Socket client = skt.accept();
        DataOutputStream serverOut = new DataOutputStream(client.getOutputStream());
        DataInputStream serverIn = new DataInputStream(client.getInputStream());

        long serverTime = System.currentTimeMillis();
        serverIn.readLong();
        
        Random r = new Random();
        //Setting the gap time in millis
        int gap = r.nextInt(30);
        // x 1000 to converto into secods
        Thread.sleep(gap * 1000);
        
        serverOut.writeLong(serverTime);
        serverOut.writeInt(gap);

    }

}
