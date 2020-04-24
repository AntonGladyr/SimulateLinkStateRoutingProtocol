1. Create routers:
    - java -jar target/router.jar conf/router1.conf
    - java -jar target/router.jar conf/router2.conf
    - java -jar target/router.jar conf/router3.conf
    - java -jar target/router.jar conf/router4.conf
    - java -jar target/router.jar conf/router5.conf

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

4. Find shortest path from the router1 to the router4:
    - detect 192.168.3.1