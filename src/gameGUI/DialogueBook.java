package gameGUI;

import java.util.LinkedHashMap;
import java.util.function.Consumer;
import java.lang.Runnable;

public class DialogueBook {
    private final float MAX_PAGE_CHOICES = 2;
    private Page root;

    public DialogueBook(String firstPage) {
        this.root = new Page(firstPage);
    }

    public Page getFirst() {
        return root;
    }

    public DialogueBook setBranch(String choiceText, Consumer<Page> pageConsumer){
        root.setBranch(choiceText, pageConsumer);
        return this;
    }

    public class Page {
        private String text;
        private LinkedHashMap<String, Page> choices;
        private Runnable pageOpened, pageTyped, pageClosed;

        public Page() {
            this("");
        }

        public Page(String text) {
            this.text = text;
            this.choices = new LinkedHashMap<String, Page>();
            this.pageOpened = () -> {};
            this.pageTyped = () -> {};
            this.pageClosed = () -> {};
        }

        public String getText() {
            return text;
        }

        public String[] getChoices() {
            return choices.keySet().toArray(new String[1]);
        }

        public boolean hasChoices() {
            return choices.size() > 0;
        }

        public Page getPageFromChoice(String choice) {
            return choices.get(choice);
        }

        public Page setBranch(String choiceText, Consumer<Page> nextPageConsumer){
            if(choices.size() == MAX_PAGE_CHOICES){
                throw new DialogueBookException("Page already has " + MAX_PAGE_CHOICES + " choices.");
            }
            Page nextPage = new Page();
            nextPageConsumer.accept(nextPage);
            choices.put(choiceText, nextPage);
            return this;
        }

        public Page setOnPageOpen(Runnable e){
            pageOpened = e;
            return this;
        }

        public Page setOnPageTyped(Runnable e){
            pageTyped = e;
            return this;
        }

        public Page setOnPageClosed(Runnable e){
            pageClosed = e;
            return this;
        }

        public Page setText(String text){
            this.text = text;
            return this;
        }

        public void firePageOpen(){
            pageOpened.run();
        }

        public void firePageTyped(){
            pageTyped.run();
        }

        public void firePageClosed(){
            pageClosed.run();
        }
    }

    public class DialogueBookException extends RuntimeException {
        public DialogueBookException(String message){
            super(message);
        }
    }
}
