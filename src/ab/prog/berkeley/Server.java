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
import org.joda.time.Minutes;

/**
 *
 * @author alebark
 */
public class Server {

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {

        //Port, and the gap from client - server time
        HashMap<Integer, Integer> clientes = new HashMap<>();

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
                clientes.put(pct.getPort(), new Integer(new String(pct.getData(), pct.getOffset(), pct.getLength())));

            } catch (SocketTimeoutException e) {
                break;
            }
        }

        //Getting all the time gaps, and returning its average to a new variable called diff
        Set<Integer> chaves = clientes.keySet();
        int diff = 0;
        for (Integer chave : chaves) {
            int val = clientes.get(chave);
            System.out.println("chave" + chave);
            System.out.println("valor" + val);
            diff += val;
        }
        diff = diff / (clientes.size() + 1);

        System.out.println("media= " + diff);

        //SETA HORA SERVIDOR
        DateTime altHr = new DateTime(serverTime);
        altHr = altHr.plusMinutes(diff);
        serverTime = altHr.getMillis();

        System.out.println("Horario do servidor reajustado" + " para: " + new Date(serverTime));

        int dif;
        for (Integer chave : chaves) {
            dif = clientes.get(chave);

            DateTime finalHour = new DateTime(serverTime);
            DateTime initialHour = new DateTime(serverTime);
            DateTime clientTime = initialHour.plus(Minutes.minutes(dif));

            int finalTime = Minutes.minutesBetween(finalHour, clientTime).getMinutes();

            System.out.println("diferença horario final enviada: " + -finalTime);
            DatagramPacket pct2 = new DatagramPacket(String.valueOf(-finalTime).getBytes(), String.valueOf(-finalTime).length(), InetAddress.getByName("127.0.0.1"), chave);
            ds.send(pct2);

        }
    }

}
