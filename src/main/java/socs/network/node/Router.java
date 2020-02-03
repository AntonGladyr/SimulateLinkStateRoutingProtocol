package socs.network.node;

import socs.network.node.request.handler.DisconnectRequest;
import socs.network.node.request.handler.HelloRequest;
import socs.network.util.Configuration;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;


public class Router extends Observable {
    public final int NUMBER_OF_PORTS = 4; // the number of physical ports in the router

    //Singleton
    private static Router instance;

    //private constructor to avoid client applications to use constructor
    private Router() {
    }

    //double checked locking principle
    public static Router getInstance() {
        if (instance == null) {
            synchronized (Router.class) {
                if (instance == null) {
                    instance = new Router();
                }
            }
        }
        return instance;
    }

    private boolean isInitialized = false;

    private LinkStateDatabase lsd;

    private RouterDescription rd = new RouterDescription();

    //assuming that all routers are with 4 ports
    private ArrayList<Link> ports = new ArrayList<Link>();

    private ArrayList<ClientSocketThread> clientThreads = new ArrayList<ClientSocketThread>();

    public RouterDescription getRouterDescription() {
        return rd;
    }

    public void setRouterDescription(RouterDescription rd) {
        this.rd = rd;
    }

    public ArrayList<ClientSocketThread> getClientThreads() {
        return clientThreads;
    }

    /**
     * output the shortest path to the given destination ip
     * <p/>
     * format: source ip address  -> ip address -> ... -> destination ip
     *
     * @param destinationIP the ip adderss of the destination simulated router
     */
    private void processDetect(String destinationIP) {

    }

    /**
     * disconnect with the router identified by the given destination ip address
     * Notice: this command should trigger the synchronization of database
     *
     * @param portNumber the port number which the link attaches at
     */
    private void processDisconnect(short portNumber) {

    }

    /**
     * attach the link to the remote router, which is identified by the given simulated ip;
     * to establish the connection via socket, you need to indentify the process IP and process Port;
     * additionally, weight is the cost to transmitting data through the link
     * <p/>
     * NOTE: this command should not trigger link database synchronization
     */
    private void processAttach(String processIP, int processPort,
                               String simulatedIP, double weight) {
        //check if there is an empty port
        if (ports.size() == NUMBER_OF_PORTS) {
            System.out.print("Sorry, there is no empty port. Try later... \n");
            return;
        }

        RouterDescription destinationRouter = new RouterDescription();
        destinationRouter.processIPAddress = processIP;
        destinationRouter.processPortNumber = processPort;
        destinationRouter.simulatedIPAddress = simulatedIP;

        Link link = new Link(this.rd, destinationRouter, weight);

        //check if a new link already exists in the ports ArrayList
        if (ports.contains(link)) {
            System.out.print("Such link already exists. Try another destination router...\n");
            return;
        }

        // create socket for new link
        // create new socket thread
        try {
            Socket socket = new Socket(processIP, processPort);
            link.setSocket(socket);
            ports.add(link);
            SocketServerThread.getInstance().startNewClientThread(socket);
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    /**
     * broadcast Hello to neighbors
     */
    private void processStart() {
        setChanged();
        notifyObservers(new HelloRequest());
    }

    /**
     * attach the link to the remote router, which is identified by the given simulated ip;
     * to establish the connection via socket, you need to indentify the process IP and process Port;
     * additionally, weight is the cost to transmitting data through the link
     * <p/>
     * This command does trigger the link database synchronization
     */
    private void processConnect(String processIP, short processPort,
                                String simulatedIP, short weight) {

    }

    /**
     * output the neighbors of the routers
     */
    private void processNeighbors() {

    }

    /**
     * disconnect with all neighbors and quit the program
     */
    private void processQuit() {
        setChanged();
        notifyObservers(new DisconnectRequest());
        SocketServerThread.getInstance().stopSocketServer();
    }

    /**
     * add new port to the ArrayList<Link> ports
     */

    public void addLink(Link link) {
        ports.add(link);
    }

    public void updateLink(Link link) {
        int linkIndex = ports.indexOf(link);
        Link testLink = ports.get(linkIndex);
        System.out.println(testLink.getRouter1().status);
        ports.set(linkIndex, link);
        System.out.println(testLink.getRouter1().status);
    }

    public void deleteLink(Link link) {
        ports.remove(link);
    }

    /**
     * check if router has an empty port to connect new node
     */
    public boolean hasEmptyPort() {
        return ports.size() < NUMBER_OF_PORTS;
    }

    public void initializeRouter(Configuration config) {
        if (!isInitialized) {
            rd.simulatedIPAddress = config.getString("socs.network.router.ip");
            rd.processIPAddress = config.getString("socs.network.router.processIPAddress");
            rd.processPortNumber = config.getInt("socs.network.router.portNumber");
            lsd = new LinkStateDatabase(rd);
            SocketServerThread.getInstance().startSocketServer(rd.processIPAddress, rd.processPortNumber);
            Thread serverThread = new Thread(SocketServerThread.getInstance());
            serverThread.start();
            isInitialized = true;
        } else {
            System.out.print("\nThis router is already initialized.\n");
            System.exit(1);
        }
    }


    public void terminal() {
        try {
            InputStreamReader isReader = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isReader);
            System.out.print(">> ");
            String command = br.readLine();
            while (true) {
                if (command.startsWith("detect ")) {
                    String[] cmdLine = command.split(" ");
                    processDetect(cmdLine[1]);
                } else if (command.startsWith("disconnect ")) {
                    String[] cmdLine = command.split(" ");
                    processDisconnect(Short.parseShort(cmdLine[1]));
                } else if (command.startsWith("quit")) {
                    processQuit();
                    break;
                } else if (command.startsWith("attach ")) {
                    String[] cmdLine = command.split(" ");
                    processAttach(cmdLine[1], Integer.parseInt(cmdLine[2]),
                            cmdLine[3], Double.parseDouble(cmdLine[4]));
                } else if (command.equals("start")) {
                    processStart();
                } else if (command.equals("connect ")) {
                    String[] cmdLine = command.split(" ");
                    processConnect(cmdLine[1], Short.parseShort(cmdLine[2]),
                            cmdLine[3], Short.parseShort(cmdLine[4]));
                } else if (command.equals("neighbors")) {
                    //output neighbors
                    processNeighbors();
                } else {
                    //invalid command
                    break;
                }
                System.out.print(">> ");
                command = br.readLine();
            }
            isReader.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
