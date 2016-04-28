package rmi;

/**
 * Created by zhewang711 on 4/27/16.
 */
public class PingPongTest {

    Skeleton<PingServerFactory> factorySkeleton = null;

    public static void main(String [] args) {
        Skeleton<PingServer> serverSkeleton = startServerSkeleton();
        Skeleton<PingServerFactory> factorySkeleton = startFact();

        while (true){
        }
    }

    private static Skeleton<PingServer> startServerSkeleton(){
        PingServerImpl server = new PingServerImpl();
        Skeleton<PingServer> serverSkeleton = new Skeleton(PingServer.class, server);
        try{
            serverSkeleton.start();
        }
        catch (RMIException e){
            e.printStackTrace();
        }
        return serverSkeleton;
    }

    private static Skeleton<PingServerFactory> startFact(){
        PingServerFactoryImpl factory = new PingServerFactoryImpl(startServerSkeleton());
        Skeleton<PingServerFactory> factorySkeleton = new Skeleton(PingServerFactory.class, factory);
        try{
            factorySkeleton.start();
        }
        catch (RMIException e){
            e.printStackTrace();
        }
        return factorySkeleton;
    }

}
