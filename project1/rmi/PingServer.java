package rmi;

/**
 * Created by zhewang711 on 4/27/16.
 */
public interface PingServer {
    public String ping(int idNumber) throws RMIException;
}
