package ab.prog.christian;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author alebark
 */
public class Client {

    public static void main(String[] args) throws IOException, ParseException {

        //New socket on local server
        Socket skt = new Socket("127.0.0.1", 8080);

        DataInputStream clientIn = new DataInputStream(skt.getInputStream());
        DataOutputStream clientOut = new DataOutputStream(skt.getOutputStream());
        Random random = new Random();

        //New random cliente time
        int millisInDay = 24 * 60 * 60 * 1000;
        Time clientFullTime = new Time((long) random.nextInt(millisInDay));
        String clientHr = (clientFullTime.getHours() < 9) ? '0' + Integer.toString(clientFullTime.getHours()) : Integer.toString(clientFullTime.getHours());
        String clientMin = (clientFullTime.getMinutes() < 9) ? '0' + Integer.toString(clientFullTime.getMinutes()) : Integer.toString(clientFullTime.getMinutes());

        String clientTime = clientHr + ':' + clientMin;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, sdf.parse(clientTime).getHours());
        long clientTimeLong = c.getTimeInMillis();

        clientOut.writeLong(clientTimeLong);

        System.out.println("Esperando resposta do servidor!");
        
        //Reading the time from server, and the gap
        long serverTime = clientIn.readLong();
        int gapServer = clientIn.readInt();

        long adjustedTime = adjustTime(serverTime, gapServer);

        System.out.println("Horário Cliente: " + clientFullTime);
        System.out.println("Horário Server: " + new Date(serverTime));
        System.out.println("Horário Ajustado: " + new Date(adjustedTime));
    }

    private static long adjustTime(long sTime, int gap) {

        Random r = new Random();
        int t0 = r.nextInt(2000);
        int t1 = r.nextInt(5500);
        int d = (t1 - t0 - (gap * 1000)) / 2;
        return (sTime + d);
    }
}
