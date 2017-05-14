package ab.prog.berkeley;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.sql.Time;
import java.util.Date;
import java.util.Random;
import org.joda.time.DateTime;
import org.joda.time.Minutes;


/**
 *
 * @author alebark
 */
public class Client {

    public static void main(String[] args) throws UnknownHostException, IOException {

        InetAddress group = InetAddress.getByName("226.8.3.5");
        MulticastSocket ms = new MulticastSocket(8080);
         Random random = new Random();
        byte[] bt = new byte[1024];

        DatagramPacket serverInfo = new DatagramPacket(bt, bt.length);

        ms.joinGroup(group);

        //New random cliente time
        int millisInDay = 24 * 60 * 60 * 1000;
        Time clientFullTime = new Time((long) random.nextInt(millisInDay));
        String clientHr = (clientFullTime.getHours() < 9) ? '0' + Integer.toString(clientFullTime.getHours()) : Integer.toString(clientFullTime.getHours());
        String clientMin = (clientFullTime.getMinutes() < 9) ? '0' + Integer.toString(clientFullTime.getMinutes()) : Integer.toString(clientFullTime.getMinutes());
        String clientTime = clientHr + ':' + clientMin;

        ms.receive(serverInfo);

        String res = new String(serverInfo.getData(), serverInfo.getOffset(), serverInfo.getLength());

        /*
        The array 'info server' contains:
            1 - The server address -> [0], 
            2 - The port to establish an individual connection -> [1] 
            3 - The server time [2]
         */
        String[] infoServer = res.split(";");

        serverInfo.setAddress(InetAddress.getByName(infoServer[0]));
        serverInfo.setPort(Integer.parseInt(infoServer[1]));
        long serverTime = Long.valueOf(infoServer[2]);
        
        DateTime dtServerTime = new DateTime(serverTime);
        DateTime dtClientTime = new DateTime((long) random.nextInt(millisInDay));
        
        long diff = Minutes.minutesBetween(dtServerTime, dtClientTime).getMinutes();

        System.out.println("Deferença do horario do serv: " + diff );

        serverInfo.setData(String.valueOf(diff).getBytes());
        DatagramSocket ds = new DatagramSocket();

        ds.send(serverInfo);
        serverInfo.setData(bt);
        ds.receive(serverInfo);

        Integer ajustar = new Integer(new String(serverInfo.getData(), serverInfo.getOffset(), serverInfo.getLength()));
       
        //ajustar horario cliente
        DateTime altHr = new DateTime(serverTime);
        altHr = altHr.plusMinutes(ajustar);
        serverTime = altHr.getMillis();

        System.out.println("Horario do cliente reajustado"+ " para: " + new Date(serverTime));
    }

}
