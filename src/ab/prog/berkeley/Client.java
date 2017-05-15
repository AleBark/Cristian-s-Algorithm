package ab.prog.berkeley;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

/**
 *
 * @author alebark
 */
public class Client {

    public static void main(String[] args) throws UnknownHostException, IOException, ParseException {

        InetAddress group = InetAddress.getByName("226.8.3.5");
        MulticastSocket ms = new MulticastSocket(8080);
        Random random = new Random();
        byte[] bt = new byte[1024];

        DatagramPacket serverInfo = new DatagramPacket(bt, bt.length);
        ms.joinGroup(group);

        //New random client time base on milisecs on a day
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        Random r = new Random();
        String hr = Integer.toString(r.nextInt(24)) + ":" + Integer.toString(r.nextInt(60)) + ":" + Integer.toString(r.nextInt(60));
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, sdf.parse(hr).getHours());
        c.set(Calendar.MINUTE, sdf.parse(hr).getMinutes());
        c.set(Calendar.SECOND, sdf.parse(hr).getSeconds());

        long clientTime = c.getTimeInMillis();

        ms.receive(serverInfo);

        String serverResponse = new String(serverInfo.getData(), serverInfo.getOffset(), serverInfo.getLength());

        /*
        The array 'info server' contains:
            1 - The server address -> [0], 
            2 - The port to establish an individual connection -> [1] 
            3 - The server time [2]
         */
        String[] infoServer = serverResponse.split(";");

        serverInfo.setAddress(InetAddress.getByName(infoServer[0]));
        serverInfo.setPort(Integer.parseInt(infoServer[1]));
        long serverTime = Long.valueOf(infoServer[2]);
        long difference = Minutes.minutesBetween(new DateTime(serverTime), new DateTime(clientTime)).getMinutes();

        
        Date dateClient = new Date(clientTime);
        Date dateServer = new Date(serverTime);
        
        System.out.println("Client: " + dateClient);
        System.out.println("Server: " + dateServer);
        System.out.println("Diff: " + difference + " min(s)");
        
        
        serverInfo.setData(String.valueOf(difference).getBytes());
        DatagramSocket ds = new DatagramSocket();

        ds.send(serverInfo);
        serverInfo.setData(bt);
        ds.receive(serverInfo);

        String adjusedTime = new String(serverInfo.getData(), serverInfo.getOffset(), serverInfo.getLength());

        System.out.println("Client adjusted time: " + adjusedTime);
    }

}
