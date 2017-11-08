import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final int PORT = 1024;

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(PORT);

            while (true) {
                if (server.isClosed()) break;

                Socket connection = server.accept();
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String requestLine = br.readLine();
                String[] request = requestLine.split(",");

                if (request[0].trim().equals("print")) {
                    String ip = request[1].trim();

                    for (int i = 0; i < 10; i++) {
                        System.out.println("Host " + ip + " printing " + i);
                        TimeUnit.MILLISECONDS.sleep(500);
                    }
                }

                br.close();
                connection.close();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
