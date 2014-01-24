package KAT;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * An asynchronous, multithreaded server using the Reactor pattern
 */
public class ReactorServer implements EventHandler 
{
    HashMap<String,EventHandler> eventHandlers;
    boolean running;
    int port;

    ReactorServer( int port ){
        this.eventHandlers = new HashMap<String,EventHandler>();
        this.port = port;
        this.running = false;

        init();
    }

    public void init(){
        registerHandler("LOGIN", new LoginEventHandler());
    }

    public void stop(){
        running = false;
    }

    public void start() {
        ServerSocket ss = null;
        running = true;

        try {
            ss = new ServerSocket(port);
            System.out.println("\nListening on port: " + port);
            System.out.println();
        } catch (IOException e) {
            stop();
            e.printStackTrace();
        }

        while( running ){
            try {
                final Socket s = ss.accept();

                new Thread(new Runnable(){
                    public void run(){
                        try {
                            service(s);
                        } catch( Exception e ){
                            e.printStackTrace();
                        } finally {
                            try {
                                if( s != null ){
                                    s.close();
                                }
                            } catch( IOException e ){
                                e.printStackTrace();
                            }
                        }
                    }
                }).start(); // execute a new thread for each client
            } catch( Exception e ){
                stop();
                e.printStackTrace();
            }
        }
        try {
            if( ss != null ){
                ss.close();
            }
        } catch( IOException e ){
            e.printStackTrace();
        }
    }

    private void service( final Socket s )throws IOException, ClassNotFoundException {
        boolean connected = true;

        // estabalish IO streams
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

        // run loop: listen for messages from client
        while( running && connected ){
            Message m = (Message)ois.readObject();
            System.out.println("Received Message: " + m);

            // create and dispatch a new event
            String type = m.getHeader().getType();
            Event event = new Event(type, m.getBody().getMap());
            event.put("OUTSTREAM", oos);
            boolean error = false;

            switch( type ){
                case "CONNECT":
                    System.out.println("client connected");
                    break;
                case "LOGIN":
                    // handle login event
                    error = !handleEvent(event);
                    break;
                case "EXIT":
                    // exit the while loop, thus endnig the client's thread
                    connected = false;
                    s.close();
                    break;
                default:
                    error = true;
                    System.err.println("Error: unrecognized message type: " + type);
                    break;
            }

            if( error ){
                System.err.println("Error: handling event: "+event);
            }
        }
    }

    public void registerHandler( String type, EventHandler handler ){
        eventHandlers.put(type, handler);
    }

    public void deregisterHandler( String type ){
        eventHandlers.remove(type);
    }

    /**
     * Dispatches an Event to an EventHandler.
     * @return true if successfully handled, false otherwise
     */
    @Override
    public boolean handleEvent( Event event ){
        EventHandler handler = eventHandlers.get(event.getType());
        if( handler != null ){
            try {
                return handler.handleEvent(event);
            } catch( IOException e ){
                System.err.println("Error: no handler registered for type: "+event.getType());
                return false;
            }
        } else {
            return false;
        }
    }
    
    public static void main( String args[] ){

        int port = (args.length > 0 ) ? Integer.parseInt(args[0]) : 8888;

        System.out.println("\nStarting Server: Kings&Things...");

        new ReactorServer(port).start();
    }
}
