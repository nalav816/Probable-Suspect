package gameGUI;

import java.awt.Insets;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;
import javax.imageio.ImageIO;

import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JComponent;
import javax.swing.BorderFactory;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import game.*;
import gameMath.*;
import gameObjects.Character;
import gameObjects.Suspect;
import gameGUI.DialogueBook.Page;

public class Dialogue extends GameImage {
    private final static String CLOSING_INPUT_PROMPT = "...";
    private final static int TYPE_SPEED = 50; 
    private final static double VERTICAL_TEXT_MARGINS = .12;
    private final static double HORIZONTAL_TEXT_MARGINS = .085;
    
    private static Dialogue activeDialogue = null;

    private Character speaker;
    private JTextPane textBox;
    private JTextField nameLabel;
    private ResponseButton[] responseButtons;
    private Page currentPage;
    private double progress;

    public Dialogue(Character speaker, DialogueBook dialogueBook) {
        super("assets/art/ui/dialogue.png", new Vector(26, 70));
        activeDialogue = this;
        this.progress = 0;
        this.speaker = speaker;
        this.currentPage = dialogueBook.getFirst();
        this.textBox = new JTextPane();
        this.nameLabel = new JTextField();
        this.responseButtons = new ResponseButton[2];

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> setComponentProperties());
        } else {
            setComponentProperties();
        }
    }

    @Override
    public Integer getLayer() {
        return JLayeredPane.POPUP_LAYER;
    }

    @Override
    public void tick() {
        super.tick();
        if (progress == 0) currentPage.firePageOpen();

        if (!finished()) {
            progress += getTextTypeDelta();
            SwingUtilities.invokeLater(() -> setVisibleText());
        } else {
            if (responseButtons[0] == null) {
                String[] responsePrompts;
                if (!currentPage.hasChoices()) {
                    responsePrompts = new String[] { CLOSING_INPUT_PROMPT };
                } else {
                    responsePrompts = currentPage.getChoices();
                }

                // Positions and spawns responseButtons
                for (int i = 1; i <= responsePrompts.length; i++) {
                    // The point where the dialogue buttons are positioned around
                    int dialogueXPivot = (int)position.getX() + 53;
                    int buttonYPos = (int)position.getY() + 13;
                    int buttonXPosOffset = 0;
                    if (responsePrompts.length % 2 == 0) {
                        buttonXPosOffset = (ResponseButton.length() - 1) * (i - responsePrompts.length / 2 - 1);
                    } else {
                        buttonXPosOffset = ((ResponseButton.length() - 1) * (i - responsePrompts.length / 2 - 1))
                                - ResponseButton.length() / 2;
                    }
                    int buttonXPos = dialogueXPivot + buttonXPosOffset;
                    responseButtons[i - 1] = new ResponseButton(responsePrompts[i - 1],
                            new Vector(buttonXPos, buttonYPos));
                    Game.getActiveGame().add(responseButtons[i - 1]);
                }

                currentPage.firePageTyped();
            }
        }
    }

    @Override
    public void render() {
        super.render();
        textBox.setBounds((int)position.getX() * Game.SCALE, (int)position.getY() * Game.SCALE,
                displayImage.getWidth(), displayImage.getHeight());
        textBox.setMargin(new Insets((int) (displayImage.getHeight() * VERTICAL_TEXT_MARGINS),
                (int) (displayImage.getWidth() * HORIZONTAL_TEXT_MARGINS),
                (int) (displayImage.getHeight() * VERTICAL_TEXT_MARGINS),
                (int) (displayImage.getWidth() * HORIZONTAL_TEXT_MARGINS)));
        nameLabel.setFont(GUISettings.FONT.deriveFont((float) GUISettings.FONT_SIZE * Game.SCALE));
        nameLabel.setSize(displayImage.getWidth(), (int) (displayImage.getHeight() * .25));
        nameLabel.setLocation((int) (position.getX() * Game.SCALE + displayImage.getWidth() * .05),
                (int) (position.getY() * Game.SCALE - displayImage.getHeight() * .25));

    }

    @Override
    public JComponent[] getJComponents() {
        return new JComponent[] { nameLabel, textBox, component };
    }

    public boolean finished() {
        return progress > 1 + getTextTypeDelta();
    }

    public void destroy() {
        progress = 0;
        activeDialogue = null;
        removeResponseButtons();
        Game.getActiveGame().remove(Dialogue.this);
        speaker.deselect();
    }

    private void setVisibleText() {
        // Typewrite Effect
        // Returns a percentage of the completed page based on the "progress" variable
        String substringToRender = getTextToRender();
        String completedParagraph = currentPage.getText();
        StyledDocument doc = textBox.getStyledDocument();
        SimpleAttributeSet regText = new SimpleAttributeSet();
        SimpleAttributeSet transparentText = new SimpleAttributeSet();
        StyleConstants.setForeground(regText, GUISettings.FONT_COLOR);
        StyleConstants.setFontSize(regText, GUISettings.FONT_SIZE * Game.SCALE);
        StyleConstants.setFontFamily(regText, GUISettings.FONT.getFamily());
        StyleConstants.setForeground(transparentText, new Color(0, 0, 0, 0));
        StyleConstants.setFontSize(transparentText, GUISettings.FONT_SIZE * Game.SCALE);
        StyleConstants.setFontFamily(transparentText, GUISettings.FONT.getFamily());

        try {
            doc.remove(0, doc.getLength());
            doc.insertString(0, substringToRender, regText);
            if (completedParagraph.length() != substringToRender.length()) {
                doc.insertString(substringToRender.length(),
                        currentPage.getText().substring(substringToRender.length()), transparentText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Line Spacing
        SimpleAttributeSet paragraph = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(paragraph, GUISettings.LINE_SPACING);
        doc.setParagraphAttributes(0, doc.getLength(), paragraph, true);

        component.repaint();
    }

    private String getTextToRender() {
        String textToRender = currentPage.getText();
        textToRender = textToRender.substring(0,
                (int) (Math.min(progress, 1) * textToRender.length()));

        return textToRender;
    }

    private double getTextTypeDelta() {
        String textToRender = currentPage.getText();
        return (1 / (double) Game.FRAME_RATE) / ((double) textToRender.length() / (TYPE_SPEED * 10));
    }

    private void flipPage(String input) {
        currentPage = currentPage.getPageFromChoice(input);
        progress = 0;
    }

    private void removeResponseButtons() {
        for (int i = 0; i < responseButtons.length; i++) {
            if (responseButtons[i] != null) {
                Game.getActiveGame().remove(responseButtons[i]);
                responseButtons[i] = null;
            }
        }
    }

    private void setComponentProperties() {
        component.setDoubleBuffered(true);
        textBox.setEditable(false);
        textBox.setFocusable(false);
        textBox.setOpaque(false);
        nameLabel.setEditable(false);
        nameLabel.setOpaque(false);
        if (speaker instanceof Suspect) {
            nameLabel.setText("System");
        } else {
            nameLabel.setText(speaker.getName());
        }
        nameLabel.setBackground(new Color(0, 0, 0, 0));
        nameLabel.setBorder(BorderFactory.createEmptyBorder());
        nameLabel.setForeground(new Color(204, 166, 129));
    }

    private class ResponseButton extends GameImage {
        private JTextField textComponent;

        public ResponseButton(String responsePrompt, Vector position) {
            super("assets/art/ui/responseButton.png", position);
            this.textComponent = new JTextField(responsePrompt);

            if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(() -> setComponentProperties(responsePrompt));
            } else {
                setComponentProperties(responsePrompt);
            }
        }

        @Override
        public void render() {
            super.render();
            textComponent.setFont(textComponent.getFont().deriveFont((float) (GUISettings.FONT_SIZE * Game.SCALE)));
            textComponent.setSize(displayImage.getWidth(), displayImage.getHeight());
            textComponent.setLocation((int) (position.getX() * Game.SCALE),
                    (int) (position.getY() * Game.SCALE));
        }

        @Override
        public JComponent[] getJComponents() {
            return new JComponent[] { textComponent, component };
        }

        @Override
        public Integer getLayer() {
            return JLayeredPane.DRAG_LAYER;
        }

        public static int length() {
            try {
                return ImageIO.read(new File("assets/art/ui/responseButton.png")).getWidth();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        private void setComponentProperties(String responsePrompt) {
            textComponent.setDoubleBuffered(true);
            textComponent.setText(responsePrompt);
            textComponent.setFocusable(false);
            textComponent.setEditable(false);
            textComponent.setOpaque(false);
            textComponent.setFont(GUISettings.FONT);
            textComponent.setHorizontalAlignment(SwingConstants.CENTER);
            textComponent.setBorder(BorderFactory.createEmptyBorder());
            textComponent.setForeground(GUISettings.FONT_COLOR);

            // The text component lies on top of the game image so
            // the click logic is handled within it instead of the game image class
            textComponent.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Page previousPage = currentPage;
                    if (currentPage.hasChoices()) {
                        flipPage(responsePrompt);
                        removeResponseButtons();
                        previousPage.firePageClosed();
                    } else {
                        previousPage.firePageClosed();
                        destroy();
                        
                    }
                }
            });
        }
    }

    public static Dialogue getActiveDialogue() {
        return activeDialogue;
    }

}
