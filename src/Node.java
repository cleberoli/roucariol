import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.*;

public class Node {
    private static final int PORT = 1024;
    private static final int TIMEOUT = 1000;
    private static final String SERVER = "10.2.7.7";

    private String ip;
    private ServerSocket server;
    private Hashtable<String, Neighbor> neighbors;

    private int osn = 0;
    private int hsn = 0;

    private boolean waiting;
    private boolean using;

    public Node(String ip) throws RemoteException, ServerNotActiveException {
        super();
        this.ip = ip;
        neighbors = new Hashtable<>();
        new Thread(this::startClient).start();
    }

    public void join(String ip) {
        sendMessage("join," + this.ip, ip, true);
    }

    public void print() {
        try {
            Socket socket = new Socket(SERVER, PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("print," + Node.this.ip);
            out.close();
            socket.close();
            release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean request() {
        waiting = true;
        osn = hsn + 1;

        neighbors.values().stream().filter(neighbor -> (!neighbor.isAccess())).forEach(neighbor -> sendRequest(osn, neighbor.getIp()));

        waiting = false;
        using = true;

        return true;
    }

    public void printAllHosts() {
        System.out.println("Neighbors: " + neighbors.keySet());
    }

    private void addNeighbor(Neighbor neighbor) throws RemoteException {
        if (!neighbors.contains(neighbor.getIp()))
            neighbors.put(neighbor.getIp(), neighbor);
    }

    private void reply(int seq, Neighbor neighbor) {
        boolean ourPriority;
        hsn = Math.max(hsn, seq);
        ourPriority = (seq > osn) || ((seq == osn));

        if (using || (waiting && ourPriority)) {
            neighbor.setReply(true);
        }

        if (!(using || waiting) || (waiting && (!neighbor.isAccess()) && (!ourPriority))) {
            if (neighbor != null) {
                neighbor.setAccess(false);
                sendReply(neighbor);
            }
        }

        if (waiting && neighbor.isAccess() && (!ourPriority)) {
            neighbor.setAccess(false);
            sendReply(neighbor);
            sendRequest(osn, ip);
        }
    }

    private void response(Neighbor neighbor) {
        neighbor.setAccess(true);
    }

    private void release() {
        using = false;

        neighbors.values().stream().filter(Neighbor::isReply).forEach(this::sendReply);
    }

    private void sendReply(Neighbor neighbor) {
        sendMessage("ok," + this.ip, neighbor.getIp(), false);
    }

    private void sendRequest(int osn, String ip) {
        sendMessage(osn + "," + this.ip, ip, false);
    }

    private void sendMessage(String message, String ip, boolean timeout) {
        try {
            Socket socket;

            if (timeout) {
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip, PORT), TIMEOUT);
            } else {
                socket = new Socket(ip, PORT);
            }

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
            out.close();
            socket.close();
        } catch (SocketTimeoutException ste) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startClient() {
        try {
            server = new ServerSocket(PORT);

            while (true) {
                if (server.isClosed()) break;

                Socket connection = server.accept();
                InputStream inputStream = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                String requestLine = br.readLine();
                String[] request = requestLine.split(",");
                String method = request[0].trim();
                Neighbor neighbor;

                switch (method) {
                    case "ok":
                        response(neighbors.get(request[1]));
                        break;
                    case "join":
                        sendMessage("joined," + this.ip, request[1], false);
                        neighbor = new Neighbor(request[1]);
                        addNeighbor(neighbor);
                        break;
                    case "joined":
                        neighbor = new Neighbor(request[1]);
                        addNeighbor(neighbor);
                        break;
                    case "print":
                        System.out.println(request[1]);
                        break;
                    default:
                        reply(Integer.parseInt(request[0]), neighbors.get(request[1]));
                        break;
                }

                br.close();
                connection.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
