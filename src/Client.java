import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.Random;
import java.util.Scanner;

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
            Scanner keyboard = new Scanner(System.in);
            System.out.println("Aperte enter para sortear um número, 'E' para sair");
            node.printAllHosts();
            String text = keyboard.nextLine();

            if (text.startsWith("E") || text.startsWith("e")) {
                assert node != null;
                node.cancel();
                break;
            } else {
                Random random = new Random();
                double number = random.nextDouble();
                System.out.println("Numero sorteado: " + number);

                if (number >= 0.5 && node != null) {
                    if (node.request()) node.print();
                }
            }
        }
    }

    private static void checkHosts(String subnet) {
        for (int i = 1; i <= 20; i++) {
            String host = subnet + "." + i;

            if (!ip.equals(host)) {

                System.out.println("Trying to reach host: " + host);

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
