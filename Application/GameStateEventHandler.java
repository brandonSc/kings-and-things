package KAT;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameStateEventHandler implements EventHandler
{
	public boolean handleEvent( Event event ){
        //System.out.println(event.getMap());
        return true;
    }
}
