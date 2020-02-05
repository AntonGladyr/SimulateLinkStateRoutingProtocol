package socs.network.node;

public class RouterDescription {
    //used to socket communication
    String processIPAddress;
    int processPortNumber;
    //used to identify the router in the simulated network space
    String simulatedIPAddress;
    //status of the router
    RouterStatus status;

    public String getProcessIPAddress() {
        return processIPAddress;
    }

    public void setProcessIPAddress(String processIPAddress) {
        this.processIPAddress = processIPAddress;
    }

    public int getProcessPortNumber() {
        return processPortNumber;
    }

    public void setProcessPortNumber(int processPortNumber) {
        this.processPortNumber = processPortNumber;
    }

    public String getSimulatedIPAddress() {
        return simulatedIPAddress;
    }

    public void setSimulatedIPAddress(String simulatedIPAddress) {
        this.simulatedIPAddress = simulatedIPAddress;
    }

    public RouterStatus getStatus() {
        return status;
    }

    public void setStatus(RouterStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouterDescription rd = (RouterDescription) o;
        if (rd.processIPAddress == null || rd.processPortNumber == 0 || rd.simulatedIPAddress == null)
            return false;
        return rd.processIPAddress.equals(processIPAddress) &&
                rd.processPortNumber == processPortNumber &&
                rd.simulatedIPAddress.equals(simulatedIPAddress);
    }
}