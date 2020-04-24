package socs.network.message;

import java.io.Serializable;

public class LinkDescription implements Serializable {
    public String linkID;
    public int portNum;
    public double tosMetrics;

    public String toString() {
        return linkID + "," + portNum + "," + tosMetrics;
    }
}
