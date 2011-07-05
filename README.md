[Salvero](https://github.com/zcox/salvero) implementation of Ostrich remote hook based on Push & Pull socket.

# Server 
A central server collects timing/metric data. Starts ostrich http admin service on 9990 by default.

``` scala
import org.ostrich.salvero.core.OstrichSalveroServer
OstrichSalveroServer("tcp://127.0.0.1:5555") start()

```

# Client 
Sends timing/metric each new timing/metric to the remote server as it's collected. Starts ostrich http admin service on 9991 by default.

``` scala
import org.ostrich.salvero.core.OstrichSalveroServer
OstrichSalveroClient("tcp://127.0.0.1:5555") connect()

```
