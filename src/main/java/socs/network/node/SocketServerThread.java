package socs.network.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServerThread implements Runnable {
    //Singleton
    private static SocketServerThread instance;

    //private constructor to avoid client applications to use constructor
    private SocketServerThread() {
    }

    //double checked locking principle
    public static SocketServerThread getInstance() {
        if (instance == null) {
            synchronized (SocketServerThread.class) {
                if (instance == null) {
                    instance = new SocketServerThread();
                }
            }
        }
        return instance;
    }

    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean isServerOn = false;

    public void run() {
        while (isServerOn) {
            try {
                // Accept incoming connections.
                // accept() will block until a client connects to the server.
                Socket clientSocket = serverSocket.accept();

                //check if there is an empty port to connect new node
                if (!Router.getInstance().hasEmptyPort()) {
                    //send message to the client and close connection
                    continue;
                }

                // Create new thread and start it
                ClientSocketThread clientSocketThread = new ClientSocketThread(clientSocket);
                startNewClientThread(clientSocketThread);
            } catch (SocketException e) {
                threadPool.shutdown();
            }
            catch (IOException ioe) {
                System.out.println("Exception encountered on accept. Ignoring. Stack Trace :");
                threadPool.shutdown();
                ioe.printStackTrace();
            }
        }
    }

    public void startSocketServer(String ipAddress, int portNumber) {
        try {
            serverSocket = new ServerSocket(portNumber, 0, InetAddress.getByName(ipAddress));
        } catch (IOException ioe) {
            System.out.printf("Could not create server socket on port %d. Quitting.\n", portNumber);
            System.exit(-1);
        }
        threadPool = Executors.newFixedThreadPool(Router.getInstance().NUMBER_OF_PORTS);
        isServerOn = true;
    }

    public void stopSocketServer() {
        try {
            isServerOn = false;
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startNewClientThread(ClientSocketThread clientSocketThread) {
        Router.getInstance().addObserver(clientSocketThread);
        threadPool.execute(clientSocketThread);
    }
}
