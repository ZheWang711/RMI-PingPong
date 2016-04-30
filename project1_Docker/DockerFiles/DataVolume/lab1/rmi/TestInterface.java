package rmi;

/**
 * Created by zhewang711 on 4/27/16.
 */

public interface TestInterface {
    public int bar(int a, int b) throws RMIException;
    public String captl(String s1, String s2) throws RMIException;
    public void echo () throws RMIException;
}

