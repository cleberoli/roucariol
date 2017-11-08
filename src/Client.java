import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Client {

    private static final int TIMEOUT = 1000;
    private static String ip = "";
    private static Node node = null;

    public static void main(String[] args) throws IOException {
        ip = args[0];

        try {
            node = new Node(ip);
            checkHosts(subnet(ip));
            node.printAllHosts();
        } catch (ServerNotActiveException | RemoteException e) {
            e.printStackTrace();
        }

        while (true) {
            Random random = new Random();
            double number = random.nextDouble();

            if (number > 0.5 && node != null) {
                System.out.println("Host " + ip + " with number  " + String.format("%.5f", number) + " wants to access the printer");
                if (node.request()) node.print();
            }
            else
                System.out.println("Host " + ip + " with number  " + String.format("%.5f", number) + " doesn't want to access the printer");

            try {
                TimeUnit.SECONDS.sleep(5);
                node.printAllHosts();
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static void checkHosts(String subnet) {
        for (int i = 1; i <= 20; i++) {
            String host = subnet + "." + i;

            if (!ip.equals(host)) {

                System.out.println("Host " + ip + " is trying to reach host " + host);

                try {
                    if (InetAddress.getByName(host).isReachable(TIMEOUT)) {
                        node.join(host);
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static String subnet(String ip) {
        return ip.substring(0, ip.lastIndexOf("."));
    }
}
