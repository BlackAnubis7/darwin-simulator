import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Asset {
    public static final Color CPLAINS = new Color(168, 209, 92);
    public static final Color CPLANT = new Color(76, 198, 7);
    public static final Color CJUNGLE = new Color(0, 92, 8);
    public static final Color CTREE = new Color(4, 47, 0);

    public static final Color CTOGGLEALL = new Color(173, 0, 0);
    public static final Color CTOGGLEFONT = new Color(255, 255, 255);

    public static final Color CSELECT = new Color(255, 42, 42);
    public static final Color CBOTH = new Color(149, 0, 255);
    public static final Color CHIGHLIGHT = new Color(17, 113, 210);
    public static final int SELECTWIDTH = 3;
    public static final Border SELECTBORDER = BorderFactory.createLineBorder(CSELECT, SELECTWIDTH);
    public static final Border HIGHLIGHTBORDER = BorderFactory.createLineBorder(CHIGHLIGHT, SELECTWIDTH);
    public static final Border BOTHBORDER = BorderFactory.createLineBorder(CBOTH, SELECTWIDTH);

    public static final Color CDEFAULT = new Color(115, 115, 115);
    public static final int DEFAULTWIDTH = 1;
    public static final Border DEFAULTBORDER = BorderFactory.createLineBorder(CDEFAULT, DEFAULTWIDTH);

    public static final Border INFOPANELBORDER = BorderFactory.createLineBorder(Color.BLACK, 2);
    public static final Border SKIPPANELBORDER = BorderFactory.createLineBorder(new Color(71, 71, 71), 1);
    public static final Color SKIPPANELBACK = new Color(184, 184, 184);

    public static final int borderWidth = 2;

    public static final double relativeAnimalSize = 0.7;

    public static final int[] XMOVEMENT = {0, 1, 1,  1,  0, -1, -1, -1};
    public static final int[] YMOVEMENT = {1, 1, 0, -1, -1, -1,  0,  1};

    public static Color energyColor(double energy) {
        if(energy <= 0) return new Color(64, 0, 0);
        else if(energy > 1) energy = 1;

        float fullHue = 0.3f;  // 0.3
        float emptyHue = 0f;  // 0.0
        float hue = emptyHue + (float) energy * (fullHue - emptyHue);

        return Color.getHSBColor(hue,1f,0.9f);
    }
}
