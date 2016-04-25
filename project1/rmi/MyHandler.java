package rmi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;

import static java.lang.Thread.sleep;

/**
 * Created by zhewang711 on 4/24/16.
 */
public class MyHandler implements InvocationHandler{

    private Class<?> intfc;
    private InetSocketAddress address;
    private Socket socket;

    public MyHandler(InetSocketAddress address, Class<?> intfc) {
        this.address = address;
        this.intfc = intfc;

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object res;

        if (method.getName() == "equals") {
            if (args[0] == null) {
                return false;
            }
            return (31 * this.address.hashCode() + 17 * this.intfc.hashCode()) == args[0].hashCode();
        }

        else if (method.getName() == "hashCode") {
            return 31 * this.address.hashCode() + 17 * this.intfc.hashCode();
        }

        else {
            try {
                Socket socket = new Socket(this.address.getHostName(), this.address.getPort());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.flush();
                oos.writeObject(method.getName());
                oos.writeObject(method.getParameterTypes());
                oos.writeObject(args);
                while (socket.getInputStream().available() <= 0)
                    sleep(1);

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                res = ois.readObject();
                oos.close();
                ois.close();
                socket.close();
            } catch (Exception e) {
                throw new RMIException("communication fault");
            }

            if (res instanceof Exception) {
                throw (Exception) res;
            } else {
                return res;
            }

        }
    }

}