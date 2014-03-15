package KAT;

import java.io.IOException;

public class KATClient extends Client
{
    public KATClient( String host, int port ){
        super(host, port);
        registerHandler("PLAYERJOIN", new JoinGameEventHandler());
        registerHandler("LOGINSUCCESS", new LoginSuccessEventHandler());
    }
    
    public void sendLogin( String username ){
        Message m = new Message("LOGIN", "CLIENT");
        m.getBody().put("username", username);

        try {
            this.oos.writeObject(m);
        } catch( IOException e ){
            e.printStackTrace();
        } catch( NullPointerException e ){
            e.printStackTrace();
        }
    }
}
