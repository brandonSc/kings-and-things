package KAT;

import java.io.IOException;

public class KATClient extends Client
{
    public KATClient( String host, int port ){
        super(host, port);
        registerHandler("PLAYERJOIN", new JoinGameEventHandler());
        registerHandler("LOGINSUCCESS", new LoginSuccessEventHandler());
        registerHandler("GAMESTATE", new GameStateEventHandler());
    }
    
    public void sendLogin( String username, int gameSize ){
        Message m = new Message("LOGIN", username);
        m.getBody().put("username", username);
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

    public void getGameState( String username ){
        Message m = new Message("GAMESTATE", username);
        m.getBody().put("username", username);

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
