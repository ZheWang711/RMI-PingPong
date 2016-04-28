package rmi;

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
