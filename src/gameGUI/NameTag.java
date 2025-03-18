package gameGUI;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import game.Game;
import gameMath.Vector;
import gameObjects.Character;

public class NameTag extends Tag {
    private JTextField textComponent;

    public NameTag(Character tagged) {
        super(tagged, "assets/art/ui/nameTag.png", new Vector(-4, tagged.getHeight() + 1));
        JTextField text = new JTextField();
       
            SwingUtilities.invokeLater(() -> {
                text.setText(tagged.getName());
                text.setEditable(false);
                text.setFont(GUISettings.FONT);
                text.setHorizontalAlignment(SwingConstants.CENTER);
                text.setBackground(new Color(0, 0, 0, 0));
                text.setBorder(BorderFactory.createEmptyBorder());
                text.setForeground(GUISettings.FONT_COLOR);
            });
        
        textComponent = text;
    }

    @Override
    public void render() {
        
            super.render();
            textComponent.setFont(textComponent.getFont().deriveFont((float) (GUISettings.FONT_SIZE * Game.SCALE)));
            textComponent.setSize(displayImage.getWidth(), displayImage.getHeight());
            textComponent.setLocation((int)position.getX() * Game.SCALE,
            (int) (position.getY() * Game.SCALE + displayImage.getHeight() * .23));
       
    }

    @Override
    public JComponent[] getJComponents() {
        return new JComponent[] { textComponent, component };
    }

}
