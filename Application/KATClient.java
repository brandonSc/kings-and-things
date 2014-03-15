package KAT;

import java.io.IOException;

public class KATClient extends Client
{
    public KATClient( String host, int port ){
        super(host, port);
        registerHandler("PLAYERJOIN", new JoinGameEventHandler());
        registerHandler("LOGINSUCCESS", new LoginSuccessEventHandler());
    }
    
    public void sendLogin( String username, String color, int gameSize ){
        Message m = new Message("LOGIN", "CLIENT");
        m.getBody().put("username", username);
        m.getBody().put("playerColor", color);
        m.getBody().put("gameSize", gameSize);

        try {
            this.oos.writeObject(m);
            this.oos.flush();
        } catch( IOException e ){
            e.printStackTrace();
        } catch( NullPointerException e ){
            e.printStackTrace();
        }
    }
}
