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

public class Client
{
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean running;
    private String host;
    private int port;

    public Client( String host, int port ){
        this.host = host;
        this.port = port;
        this.running = false;
    }

    public void connect(){
        try { 
            final Socket s = new Socket(host, port);
            
            new Thread(new Runnable(){
                public void run(){
                    try {
                        running = true;
                        service(s);
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
            }).start(); // execute in a background thread
        } catch( Exception e ){
            e.printStackTrace();
        }
    }
    
    public void disconnect(){
        running = false;
    }

    public void service( final Socket s )
        throws IOException, ClassNotFoundException, EOFException {
        oos = new ObjectOutputStream(s.getOutputStream());
        ois = new ObjectInputStream(s.getInputStream());

        Message m = new Message("CONNECT", "CLIENT");
        oos.writeObject(m);
        
        while( running ){
            try {
                m = (Message)ois.readObject();
                System.out.println("Received message: "+m);
            } catch( EOFException e ){
                System.err.println("Error: disconnected from server");
                running = false;
                break;
            }

            String type = m.getHeader().getType();

            switch( type ){
                case "LOGINSUCCESS":
                    // notify view-controller
                    break;
                default:
                    System.err.println("Error: unrecognized message type: "+type);
                    break;
            }
        }

        try {
            s.close();
        } catch( IOException e ){
            e.printStackTrace();
        }
    }

    public void sendLogin( String username ){
        Message m = new Message("LOGIN", "CLIENT");
        m.getBody().put("username", username);

        try {
            oos.writeObject(m);
        } catch( IOException e ){
            e.printStackTrace();
        }
    }

    public static void main( String args[] ){
        new Client("localhost", 8888).connect();
    }
}
