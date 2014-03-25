//
// Client.java
// kingsandthings/Application/
// @author Brandon Schurman
//
package KAT;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.EOFException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

public class Client implements EventHandler
{
    protected HashMap<String,EventHandler> eventHandlers;
    protected ObjectOutputStream oos;
    protected ObjectInputStream ois;
    private Thread netThread;
    private boolean running;
    private String host;
    private int port;

    public Client( String host, int port ){
        this.eventHandlers = new HashMap<String,EventHandler>();
        this.host = host;
        this.port = port;
        this.running = false;
    }

    public void connect(){
        try { 
            final Socket s = new Socket(host, port);
            this.oos = new ObjectOutputStream(s.getOutputStream());
            this.ois = new ObjectInputStream(s.getInputStream());
            
            this.netThread = new Thread(new Runnable(){
                public void run(){
                    try {
                        running = true;
                        service(s);                        
                    } catch( SocketException e ){
                        running = false;
                    } catch( IOException e ){
                        e.printStackTrace();
                    } catch( ClassNotFoundException e ){
                        e.printStackTrace();
                    } finally {
                        running = false;
                        try {
                            if( s != null ){
                                s.close();
                            }
                        } catch( IOException e ){
                            e.printStackTrace();
                        }
                    }
                }
            }); 
            netThread.start(); // execute in a background thread
        } catch( Exception e ){
            e.printStackTrace();
        }
    }
    
    public void disconnect(){
        running = false;
        try {
            ois.close(); 
        } catch( IOException e ){
            e.printStackTrace();
        }
        netThread.interrupt();
    }

    public void service( final Socket s )
        throws IOException, ClassNotFoundException, EOFException, SocketException {

        Message m = new Message("CONNECT", "CLIENT");
        oos.writeObject(m);
        oos.flush();
        
        while( running ){
            m = (Message)ois.readObject();
            //System.out.println("Received Message: " + m);

            // create and dispatch a new event
            String type = m.getHeader().getType();
            Event event = new Event(type, m.getBody().getMap());
            event.put("OUTSTREAM", oos);
            boolean error = false;

            // dispatch the event to an event handler
            error = !handleEvent(event);

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
                System.err.println("Error: no handler registered for type: "
                        + event.getType());
                return false;
            }
        } else {
            return false;
        }
    }

    public static void main( String args[] ){
        new Client("localhost", 8888).connect();
    }
}
