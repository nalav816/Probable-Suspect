package gameEvents.dialogue;

import java.util.HashMap;
import gameGUI.DialogueBook.Page;

public class PageHandler {
    private static HashMap<Page, PageListener> listeners = new HashMap<Page, PageListener>();

    public static void firePageTyped(Page src){
        listeners.get(src).pageTyped();
    }
    
    public static void firePageOpened(Page src){
        listeners.get(src).pageOpened();
    }

    public static void firePageClosed(Page src){
        listeners.get(src).pageClosed();
    }
    
    public static void addListener(Page p, PageListener l){
        listeners.put(p, l);
    }

    public static void removeListener(Page p){
        listeners.remove(p);
    }

}
