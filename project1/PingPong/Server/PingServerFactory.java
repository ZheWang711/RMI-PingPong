package PingPong.Server;

import rmi.RMIException;

/**
 * Created by zhewang711 on 4/27/16.
 */
public interface PingServerFactory {
    public PingServer makePingServer() throws RMIException;
}
