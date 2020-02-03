package socs.network.node;

import java.net.Socket;

public class Link {

    RouterDescription router1;
    RouterDescription router2;
    double weight;
    Socket socket;

    public Link(RouterDescription r1, RouterDescription r2, double weight) {
        router1 = r1;
        router2 = r2;
        this.weight = weight;
    }

    public Link(RouterDescription r1, RouterDescription r2, double weight, Socket socket) {
        router1 = r1;
        router2 = r2;
        this.weight = weight;
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public RouterDescription getRouter1() {
        return router1;
    }

    public RouterDescription getRouter2() {
        return router2;
    }

    public void setRouter1(RouterDescription description) {
        router1 = description;
    }

    public void setRouter2(RouterDescription description) {
        router2 = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return (link.router1.equals(router1) && link.router2.equals(router2)) ||
                (link.router1.equals(router2) && link.router2.equals(router1));
    }
}
