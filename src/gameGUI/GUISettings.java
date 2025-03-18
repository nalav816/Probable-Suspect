package gameGUI;

import java.awt.Font;
import java.awt.Color;
import java.io.File;

public class GUISettings {
    public final static Font FONT = loadFont();
    public final static int FONT_SIZE = 7;
    public final static Color FONT_COLOR = new Color(99, 72, 75);
    public final static float LINE_SPACING = -.4f;

    private static Font loadFont() {
        try {
            File fontFile = new File("assets/fonts/m3x6.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            return font;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
