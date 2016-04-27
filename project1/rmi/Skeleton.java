package rmi;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/** RMI skeleton

 <p>
 A skeleton encapsulates a multithreaded TCP server. The server's clients are
 intended to be RMI stubs created using the <code>Stub</code> class.

 <p>
 The skeleton class is parametrized by a type variable. This type variable
 should be instantiated with an interface. The skeleton will accept from the
 stub requests for calls to the methods of this interface. It will then
 forward those requests to an object. The object is specified when the
 skeleton is constructed, and must implement the remote interface. Each
 method in the interface should be marked as throwing
 <code>RMIException</code>, in addition to any other exceptions that the user
 desires.

 <p>
 Exceptions may occur at the top level in the listening and service threads.
 The skeleton's response to these exceptions can be customized by deriving
 a class from <code>Skeleton</code> and overriding <code>listen_error</code>
 or <code>service_error</code>.
 */
public class Skeleton<T>
{
    private ServerSocket socket = null;
    private InetSocketAddress address = null;

    // a main thread "accept" incoming connection, and spawn subthreads to call method in para@ server
    private TCPListen tlisten = null;

    // object implementing the common interface
    private T server = null;

    private boolean started = false;

    /** Creates a <code>Skeleton</code> with no initial server address. The
     address will be determined by the system when <code>start</code> is
     called. Equivalent to using <code>Skeleton(null)</code>.

     <p>
     This constructor is for skeletons that will not be used for
     bootstrapping RMI - those that therefore do not require a well-known
     port.

     @param c An object representing the class of the interface for which the
     skeleton server is to handle method call requests.
     @param server An object implementing said interface. Requests for method
     calls are forwarded by the skeleton to this object.
     @throws Error If <code>c</code> does not represent a remote interface -
     an interface whose methods are all marked as throwing
     <code>RMIException</code>.
     @throws NullPointerException If either of <code>c</code> or
     <code>server</code> is <code>null</code>.
     */
    public Skeleton(Class<T> c, T server) throws NullPointerException, Error
    {
        // Just invoke the general constructor with a designated port
        this(c, server, null);
    }

    /** Creates a <code>Skeleton</code> with the given initial server address.

     <p>
     This constructor should be used when the port number is significant.

     @param c An object representing the class of the interface for which the
     skeleton server is to handle method call requests.
     @param server An object implementing said interface. Requests for method
     calls are forwarded by the skeleton to this object.
     @param address The address at which the skeleton is to run. If
     <code>null</code>, the address will be chosen by the
     system when <code>start</code> is called.
     @throws Error If <code>c</code> does not represent a remote interface -
     an interface whose methods are all marked as throwing
     <code>RMIException</code>.
     @throws NullPointerException If either of <code>c</code> or
     <code>server</code> is <code>null</code>.
     */
    public Skeleton(Class<T> c, T server, InetSocketAddress address)
            throws NullPointerException, Error
    {
        this.server = server;
        this.address = address;          // record the server address

        // ---- checking ---

        // if either of the parameters is null
        if(c == null || server == null) {
            throw new NullPointerException("The template class or instance" +
                    "for creating the Skeleton is NULL.");
        }

        // checking if param@c is a remote interface
        try
        {
            boolean rmiexp;
            if(!c.isInterface())   // if c is not an interface
                throw new Error("The template is not an interface.");

            //iterate over all methods in c to check for RMIException
            for(Method s : c.getMethods())
            {
                rmiexp = false;

                // check if there is an RMIException in this method
                for(Class<?> exp : s.getExceptionTypes())
                    if(exp.getCanonicalName().equals("rmi.RMIException"))
                    {
                        rmiexp = true;
                        break;
                    }
                if(!rmiexp)  //if no RMIException is found
                    throw new Error("The Methods in template class don't throw RMIException.");
            }
        }
        catch(SecurityException e)
        {
            System.out.println("Get Methods is blocked by security reasons.");
            e.printStackTrace();
        }


        // checking whether the object passed in actually implemented the interface.
        {
            boolean intf = false;
            // iterate over all interfaces of 'server'
            for(Class<?> cl : server.getClass().getInterfaces())
                if(cl == c) {
                    intf = true;
                    break;
                }
            if(!intf)
                throw new Error("The interface c cannot match with server.");
        }

    }

    /** Get the socket address from the Skeleton.
     */
    public InetSocketAddress getAddress()
    {
        return address;
    }

    /** Called when the listening thread exits.

     <p>
     The listening thread may exit due to a top-level exception, or due to a
     call to <code>stop</code>.

     <p>
     When this method is called, the calling thread owns the lock on the
     <code>Skeleton</code> object. Care must be taken to avoid deadlocks when
     calling <code>start</code> or <code>stop</code> from different threads
     during this call.

     <p>
     The default implementation does nothing.

     @param cause The exception that stopped the skeleton, or
     <code>null</code> if the skeleton stopped normally.
     */
    protected void stopped(Throwable cause)
    {
    }

    /** Called when an exception occurs at the top level in the listening
     thread.

     <p>
     The intent of this method is to allow the user to report exceptions in
     the listening thread to another thread, by a mechanism of the user's
     choosing. The user may also ignore the exceptions. The default
     implementation simply stops the server. The user should not use this
     method to stop the skeleton. The exception will again be provided as the
     argument to <code>stopped</code>, which will be called later.

     @param exception The exception that occurred.
     @return <code>true</code> if the server is to resume accepting
     connections, <code>false</code> if the server is to shut down.
     */
    protected boolean listen_error(Exception exception)
    {
        return false;
    }

    /** Called when an exception occurs at the top level in a service thread.

     <p>
     The default implementation does nothing.

     @param exception The exception that occurred.
     */
    protected void service_error(RMIException exception)
    {
    }

    /** Starts the skeleton server.

     <p>
     A thread is created to listen for connection requests, and the method
     returns immediately. Additional threads are created when connections are
     accepted. The network address used for the server is determined by which
     constructor was used to create the <code>Skeleton</code> object.

     @throws RMIException When the listening socket cannot be created or
     bound, when the listening thread cannot be created,
     or when the server has already been started and has
     not since stopped.
     */
    public synchronized void start() throws RMIException
    {
        if(started) return;
        else started = true;

        if(address == null)
            address = new InetSocketAddress(7000);
        try {
            socket = new ServerSocket();
        }
        catch(Exception e) {
            System.out.println("Skeleton TCP socket open error.");
            e.printStackTrace();
            throw new RMIException("Skeleton TCP socket open error.");
        }
        try {
            socket.bind(address);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new RMIException("Skeleton TCP socket bind error.");
        }
        tlisten = new TCPListen(socket, this);
        tlisten.start();

    }

    /** Stops the skeleton server, if it is already running.

     <p>
     The listening thread terminates. Threads created to service connections
     may continue running until their invocations of the <code>service</code>
     method return. The server stops at some later time; the method
     <code>stopped</code> is called at that point. The server may then be
     restarted.
     */
    public synchronized void stop()
    {
        if(!started) return;
        else started = false;

        if(tlisten == null) return;  // able to stop multiple times

        tlisten.terminate();         // let thread know it should stop

        try
        {
            tlisten.join();          // wait for the thread to exit
        }
        catch(Exception e) {}

        this.stopped(null);          // perform required post actions


        tlisten = null;  // release the thread resource immediately
    }


    /** The top level TCP server thread for
     *  listening the port and run the sub-threads for
     *  each connection
    */
    private class TCPListen extends Thread
    {
        private volatile ServerSocket serversocket;     // TCP server socket
        private volatile boolean stop = false; // stop request
        private Skeleton<T> father;            // the Skeleton
        private List<SocketConn> tsockets = new ArrayList<SocketConn>();
        // all TCP connection threads
        private Object mutex = new Object();

        public TCPListen(ServerSocket socket, Skeleton<T> father)
        {
            this.serversocket = socket;
            this.father = father;
        }

        public void terminate()
        {
            stop = true;  // make the stop request
            try
            {
                serversocket.close(); // close socket, interrupt the accept
            }
            catch(Exception e) {}

        }

        public synchronized void run()
        {
            Socket conn = null;

            while(!stop)
            {
                try
                {
                    conn = serversocket.accept();  //wait for connection
                }
                catch(SocketException e)
                {
                    if(stop) break;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    father.listen_error(e);  //deal with listen error
                }

                if(conn != null)  // prevent the stop situation caused socket.close
                {
                    SocketConn tmp = new SocketConn(conn, this.father); //dispatch
                    tsockets.add(tmp);
                    tmp.start();
                }

            }

            for(SocketConn s : tsockets) // request and wait for all connections finish
            {
                try
                {
                    s.terminate();
                    s.join();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

        }
    }

    /** The thread class for handling each Stub connection
     */
    private class SocketConn extends Thread
    {
        private volatile Socket socket;
        private volatile boolean stop = false;
        private Skeleton<T> gfather = null;

        public SocketConn(Socket socket, Skeleton<T> gfather)
        {
            this.socket=socket;
            this.gfather=gfather;
        }

        public void terminate()
        {
            stop = true;
            try
            {
                socket.close();
            }
            catch(Exception e) {}

        }

        public void run()
        {
            ObjectInputStream ois = null;
            ObjectOutputStream oos = null;
            String mname = null;
            Object[] plist = null;
            Class<?>[] ptype = null;

            try
            {
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.flush();

                while (socket.getInputStream().available() <= 0)
                    sleep(1);

                ois = new ObjectInputStream(socket.getInputStream());

                mname = (String) ois.readObject();
                ptype = (Class <?>[]) ois.readObject();
                plist = (Object[])ois.readObject();

            }
            catch (Exception e) // the exception from stop request
            {
                if(stop) return;
            }

            try
            {
                Method m;
                Object retv = null;  // return value

                try
                {
                    m = server.getClass().getMethod(mname, ptype);
                    retv = m.invoke(server, plist);  // actual invocation
                }
                catch(InvocationTargetException e)
                {
                    retv = e.getTargetException(); // get the actual exception
                }

                oos.writeObject(retv);

            }
            catch(Exception e) // if the invocation throws exception
            {
                try
                {
                    oos.writeObject(e); // send back the exception
                }
                catch(Exception ee) {}
            }

            try
            {
                ois.close();
                oos.close();
                socket.close();
            }
            catch(Exception e) {}
        }
    }

}
