package PingPong.Client;

import PingPong.Server.PingServer;
import rmi.RMIException;

/**
 * Created by zhewang711 on 4/27/16.
 */
public class PingServerImpl implements PingServer {

//    public PingServerImpl(){
//
//    }

    public String ping(int idNumber) throws RMIException{
        return "Pong " + idNumber;
    }
}
