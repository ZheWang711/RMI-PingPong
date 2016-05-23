package PingPong.Server;

import rmi.*;

import java.net.UnknownHostException;

/**
 * Created by zhewang711 on 4/27/16.
 */
public class PingServerFactoryImpl implements PingServerFactory {

    Skeleton<PingServer> skeleton;

    public PingServerFactoryImpl(Skeleton<PingServer> skeleton){
        this.skeleton = skeleton;
    }


    public PingServer makePingServer() throws RMIException {
        try {
            PingServer serverProx = Stub.create(PingServer.class, skeleton);
            return serverProx;
        }
        catch (UnknownHostException e) {
            throw new RMIException("unknown host from skeleton");
        }

    }
}
