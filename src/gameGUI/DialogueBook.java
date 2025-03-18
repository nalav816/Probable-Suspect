package gameGUI;

import java.util.LinkedHashMap;
import gameObjects.Character;
import gameEvents.dialogue.PageHandler;
import gameEvents.dialogue.PageListener;

public class DialogueBook {
    private Page first;
    private final static String DEFAULT_INPUT_KEY = "...";

    public DialogueBook(String firstElement){
        this.first = new Page(firstElement);
    }

    public Page add(Page parent, String newString){
        return add(parent, newString, DEFAULT_INPUT_KEY, null);
    }

    public Page add(Page parent, String newString, String inputKey){
        return add(parent, newString, inputKey, null);
    }

    public Page add(Page parent, String newString, String inputKey, PageListener listener){
        Page newPage = new Page(newString, listener);
        parent.addChild(inputKey, newPage);
        return newPage;
    }
    
    public Page getFirst(){
        return first;
    }

    public void initializeClueDialogues(Character speaker){
        updateClueDialogues(first, speaker);
    }

    private void updateClueDialogues(Page curr, Character speaker){
        if(curr.elem.contains("INSERTCLUEHERE")){
            curr.elem = curr.elem.replace("INSERTCLUEHERE", speaker.getClue().getVariation().getName());
            curr.setListener(new PageListener(){
                public void pageOpened(){ speaker.giveClue(); }
            });
        }

        for(Page n : curr.children.values()){
            updateClueDialogues(n, speaker);
        }
    }

    public class Page {
        private LinkedHashMap<String, Page> children;
        private String elem;
        //Represents any functions that may run when this Page is initially acessed by the dialogue object
        //Open events run when the page represented by this Page is opened
        //And close events run when it is closed
        private PageListener listener;

        public Page(){
            this(null, null);
        }

        public Page(String elem){
            this(elem, null);
        }

        public Page(String elem, PageListener listener){
            this.children = new LinkedHashMap<String, Page>();
            this.elem = elem;
            setListener(listener);
        }

        public String getElement(){
            return elem;
        }

        public Page getChild(String key){
            return children.get(key);
        }

        public String[] getInputPrompts(){
            return children.keySet().toArray(new String[1]);
        }

        public boolean branches(){
            return children.size() > 1;
        }

        public boolean hasNext(){
            return children.size() > 0;
        }

        public boolean hasEvent(){
            return listener != null;
        }

        public void addChild(String key, Page child){
            if(children.size() == 2){
                System.out.println("Dialogue prompt may only contain two responses");
                return;
            }
            children.put(key, child);
        }

        public void setListener(PageListener li){
            if(listener != null){
                PageHandler.removeListener(this);
            }

            listener = li;
            PageHandler.addListener(this, listener);
        }
    }
}
