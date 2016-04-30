package PingPong;

import rmi.RMIException;
import rmi.Stub;

import java.net.InetSocketAddress;

/**
 * Created by zhewang711 on 4/27/16.
 */
public class PingClient {

    PingServer server= null;

    public void initialize(){
        InetSocketAddress address = new InetSocketAddress("pingserver", 7001);
        PingServerFactory fact_prox = Stub.create(PingServerFactory.class, address);
        try{
           this.server  = fact_prox.makePingServer();
        }
        catch (RMIException e){
            e.printStackTrace();
        }
    }



    public void pingPongTest(){
        this.initialize();
        System.out.println("initialized succeed hehe");
        System.out.println(server.toString());
        int success = 0;
        int fail = 0;
        for (int i = 0; i < 4 ; ++i){
            try{
                System.out.println(this.server.ping(i));
            }
            catch (RMIException e){
                e.printStackTrace();
            }
        }
    }


}
