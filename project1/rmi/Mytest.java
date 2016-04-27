package rmi;

import java.net.InetSocketAddress;

/**
 * Created by zhewang711 on 4/27/16.
 */
public class Mytest {

    public static void main(String [] args){
        startServer();

        testNull();
        testVoid();
    }

    private static void startServer(){
        TestInterfaceImpl si = new TestInterfaceImpl();
        Skeleton<TestInterface> skeleton = new Skeleton(TestInterface.class, si);
        try {
            skeleton.start();
        }
        catch (RMIException e){
            e.printStackTrace();
        }
    }

    private static TestInterface startStub(){
        InetSocketAddress address = new InetSocketAddress("localhost", 7000);
        TestInterface s = Stub.create(TestInterface.class, address);
        return s;
    }

    private static void testToString(){
        InetSocketAddress address = new InetSocketAddress("localhost", 7000);
        TestInterface s = Stub.create(TestInterface.class, address);
        System.out.println(s);
    }

    private static void testNull(){

        TestInterface s = startStub();

        try{
            System.out.println(s.captl( null, null));
        }
        catch (RMIException e){
            e.printStackTrace();
        }

    }

    private static void testVoid(){
        TestInterface s = startStub();
        try{
            s.echo();
        }
        catch (RMIException e){
            e.printStackTrace();
        }
    }

}
