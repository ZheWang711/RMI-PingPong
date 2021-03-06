package rmi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;

import static java.lang.Thread.sleep;

/**
 * Created by zhewang711 on 4/24/16.
 */

public class MyHandler implements InvocationHandler, Serializable{
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
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        // test if the equals/hash/tostr methods are overloaded
        boolean overload_equal = true;
        boolean overload_hash = true;
        boolean overload_tostr = true;

        try{
            this.intfc.getMethod("equals", method.getParameterTypes());
        }
        catch (Exception e){
            overload_equal = false;
        }

        try{
            this.intfc.getMethod("hashCode", method.getParameterTypes());
        }
        catch (Exception e){
            overload_hash = false;
        }

        try{
            this.intfc.getMethod("hashCode", method.getParameterTypes());
        }
        catch (Exception e){
            overload_tostr = false;
        }



        if (method.getName() == "equals" && !overload_equal) { // equals method
            if (args[0] == null) {
                return false;
            }
            return (31 * this.address.hashCode() + 17 * this.intfc.hashCode()) == args[0].hashCode();
        }

        else if (method.getName() == "hashCode" && !overload_hash) { // hashcode method
            return 31 * this.address.hashCode() + 17 * this.intfc.hashCode();
        }

        else if (method.getName() == "toString" && !overload_tostr){
            return "interface name: " + this.intfc.getName() + ", remote address:" + this.address.toString();
        }

        else {
            try {
                socket = new Socket(this.address.getHostName(), this.address.getPort());
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.flush();
                oos.writeObject(method.getName());
                oos.writeObject(method.getParameterTypes());
                oos.writeObject(args);
                //while (socket.getInputStream().available() <= 0)
                sleep(10);   // available is too slow

                ois = new ObjectInputStream(socket.getInputStream());
                res = ois.readObject();
                //oos.close();
                //ois.close();
                //socket.close();
            } catch (Exception e) {
                throw new RMIException("communication fault");
            } finally {
                try {
                    oos.close();
                    ois.close();
                    socket.close();
                } catch (Exception e) {}
            }

            if (res instanceof InvocationTargetException) {
                throw ((InvocationTargetException) res).getTargetException();
            } else if (res instanceof RMIException){
                throw (RMIException) res;
            }
            else{
                return res;
            }

        }
    }

}