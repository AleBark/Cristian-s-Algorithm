package ab.prog.berkeley;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import org.joda.time.DateTime;

/**
 *
 * @author alebark
 */
public class Server {

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {

        //Port, and the gap from client - server time
        HashMap<Integer, Integer> clients = new HashMap<>();

        InetAddress group = InetAddress.getByName("226.8.3.5");
        MulticastSocket ms = new MulticastSocket();
        DatagramSocket ds = new DatagramSocket();
        byte[] bt = new byte[1024];

        long serverTime = System.currentTimeMillis();

        String infos = InetAddress.getLocalHost().getHostAddress() + ";" + ds.getLocalPort() + ";" + String.valueOf(serverTime);

        DatagramPacket pct = new DatagramPacket(infos.getBytes(), infos.length(), group, 8080);
        ms.send(pct);

        //Timeout to stop the server from listen to new clients
        ds.setSoTimeout(5000);

        while (true) {
            pct.setData(bt);
            try {

                ds.receive(pct);
                clients.put(pct.getPort(), new Integer(new String(pct.getData(), pct.getOffset(), pct.getLength())));

            } catch (SocketTimeoutException e) {
                break;
            }
        }

        //Getting all the time gaps, and returning its average to a new variable
        Set<Integer> keys = clients.keySet();
        int average = 0;
        for (Integer key : keys) {
            int val = clients.get(key);
            average += val;
        }
        average = average / (clients.size() + 1);

        DateTime serverDateTime = new DateTime(serverTime);
        serverDateTime = serverDateTime.plusMinutes(average);
        Date serverAdjustedTime = new Date(serverDateTime.getMillis());

        System.out.println("Old server time: " + new Date(serverTime));
        System.out.println("New server time: " + serverAdjustedTime);

//      clientNewTime = serverDateTime - serverAdjustedTime = serverAdjustedTime
        for (Integer key : keys) {
            int port = key;
            DatagramPacket pct2 = new DatagramPacket(String.valueOf(serverAdjustedTime).getBytes(), String.valueOf(serverAdjustedTime).length(), InetAddress.getByName("127.0.0.1"), port);
            ds.send(pct2);

        }

    }
}
