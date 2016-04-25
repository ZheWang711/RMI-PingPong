
/**
 * Created by zhewang711 on 4/24/16.
 */
package rmi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import static java.lang.Thread.sleep;


public class MyHandler implements InvocationHandler{

    private InetSocketAddress address;

    public MyHandler(InetSocketAddress address){
        this.address = address;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Socket socket = new Socket(this.address.getHostName(), this.address.getPort());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        oos.writeObject(method.getName());
        oos.writeObject(method.getParameterTypes());
        oos.writeObject(args);

        while (socket.getInputStream().available() <= 0)
            sleep(1);

        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Object res = ois.readObject();
        oos.close();
        ois.close();
        socket.close();

        if (res instanceof Exception ){
            throw (Exception)res;
        }
        else {
            return res;
        }

    }

}

