This project is a program which simulates the major functionalities of a routing device running a simplified Link State Routing protocol. Each program instance represents a router or host in the simulated network space; Correspondingly, the links connecting the routers/hosts and the IP addresses identifying the routers/hosts are simulated by the in-memory data structures. For more details, see PA_COMP_535.pdf.

Usage:

1. Create routers (in different terminals):
    - java -jar target/router.jar conf/router1.conf
    - java -jar target/router.jar conf/router2.conf
    - java -jar target/router.jar conf/router3.conf
    - java -jar target/router.jar conf/router4.conf
    - java -jar target/router.jar conf/router5.conf
    - java -jar target/router.jar conf/router6.conf

2. Connect routers:
    2.1 Run on the router1(192.168.1.1) for connecting router1 and router2, router1 and router3:
        - attach 127.0.0.1 50002 192.168.2.1 2
        - attach 127.0.0.1 50001 192.168.1.100 1
        - start
    2.2 Run on the router2(192.168.1.100) for connecting router2 and router4
        - attach 127.0.0.1 50003 192.168.3.1 1
        - start
    2.3 Run on the router2(192.168.2.1) for connecting router3 and router4
        - attach 127.0.0.1 50003 192.168.3.1 1
        - start

3. Show neighbors and LinkStateDatabase of the router4:
    - neighbors

4. Find shortest path from the router4 to the router1:
    - detect 192.168.3.1

=========================================================================================================
=========================================================================================================

#AN EXAMPLE FOR THE DIJKSTRA ALGORITHM
1. Create routers (in different terminals):
    - java -jar target/router.jar conf/router1.conf
    - java -jar target/router.jar conf/router2.conf
    - java -jar target/router.jar conf/router3.conf
    - java -jar target/router.jar conf/router4.conf
    - java -jar target/router.jar conf/router5.conf
    - java -jar target/router.jar conf/router6.conf
    - java -jar target/router.jar conf/router7.conf

2. Connect routers:
    2.1 On the Router 192.168.1.1
            - attach 127.0.0.1 50001 192.168.1.100 25
            - start
    2.2 On the Router 192.168.2.1
            - attach 127.0.0.1 50001 192.168.1.100 10
            - attach 127.0.0.1 50003 192.168.3.1 50
            -start
    2.3 On the Router 192.168.3.1
            - attach 127.0.0.1 50001 192.168.1.100 5
            - attach 127.0.0.1 50004 192.168.4.1 50
            - start
    2.4 On the Router 192.168.5.1
            - attach 127.0.0.1 50002 192.168.2.1 10
            - attach 127.0.0.1 50006 192.168.6.1 30
            - start

     2.5 On the Router 192.168.6.1
            - attach 127.0.0.1 50002 192.168.2.1 10
            - start

3. Check the routing table (in any router)
    - neighbors

4. Find shortest path from 192.168.1.1 to 192.168.3.1:
    4.1 On the Router 192.168.1.1
        - detect 192.168.3.1

