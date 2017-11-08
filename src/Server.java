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
            // Establish the listen socket.
            ServerSocket server = new ServerSocket(PORT);

            while (true) {
                // Listen for a TCP connection request.
                if (server.isClosed()) break;

                Socket connection = server.accept();

                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                // Get the request line of the HTTP request message.
                String requestLine = br.readLine();

                String[] request = requestLine.split(",");

                if (request[0].startsWith("Print")) {
                    for (int i = 1; i <= 10; i++) {
                        System.out.println(request[1] + "," + i);
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
