package socs.network.node;

import socs.network.node.request.handler.HelloRequest;
import socs.network.node.request.handler.LSAUpdateRequest;
import socs.network.node.request.handler.Request;

import java.io.*;
import java.util.Observable;
import java.util.Observer;
import java.net.Socket;

public class ClientSocketThread implements Runnable, Observer {
    private Link link;
    // Obtain the input stream and the output stream for the socket

    private Socket socket;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    private boolean isActiveSocket = false;

    public ClientSocketThread(Socket clientSocket) {
        socket = clientSocket;
        // create new Link and add to the <ports> list
        link = new Link(new RouterDescription(), new RouterDescription(), Integer.MAX_VALUE, clientSocket);
        // create input and output streams
        try {
            out = new ObjectOutputStream(link.getSocket().getOutputStream());
            in = new ObjectInputStream(link.getSocket().getInputStream());
        } catch (Exception e) {
            // Clean up
            try {
                link.getSocket().close();
//                Router.getInstance().deleteLink(clientSocket);
                System.out.println("...Stopped");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            e.printStackTrace();
        }
        isActiveSocket = true;
    }

    public void run() {
        while (isActiveSocket) {
            handleRequest();
        }
    }

    public void update(Observable obj, Object arg) {
        // send request
        Request request = (Request) arg;
        request.send(this); //send request
    }

    public void connect() {
        HelloRequest request = new HelloRequest();
        request.send(this); //send request
    }

    public void lsaUpdate() {
        LSAUpdateRequest lsaUpdateRequest = new LSAUpdateRequest();
        lsaUpdateRequest.send(this); //send LSAupdate request
    }

    public void disconnect() {
        isActiveSocket = false;

        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLink(Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return out;
    }

    private void handleRequest() {
        // read incoming stream
        try {
            Request request = (Request) in.readObject();
            request.process(request, this);
        } catch (IOException ioe) {
            disconnect();
        } catch (Exception e) {
            disconnect();
            e.printStackTrace();
        }
    }
}
