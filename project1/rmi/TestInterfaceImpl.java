package rmi;

/**
 * Created by zhewang711 on 4/27/16.
 */
public class TestInterfaceImpl implements TestInterface {
    private int x;
    public int bar(int a, int b) throws RMIException{
        throw new RMIException("hahaha");
    }
    public String captl(String s1, String s2) throws RMIException{
        if (s1 == null || s2 == null ){
            return null;
        }
        return s1.toUpperCase() + s2.toUpperCase();
    }

    public void echo () throws RMIException{
        System.out.println("echo");
    }
}
