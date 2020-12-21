import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Map {
    private final int width, height;
    private final Field[] fields;  // size = width * height

    public Map(int w, int h, Vector2d j1, Vector2d j2) {
        Random generator = new Random();  /////////////////////// TEST PHASE ///////////////////////
        width = w;
        height = h;
        fields = new Field[w * h];
        for(int i = 0; i < width * height; i++) {
            fields[i] = new Field(i);
        }
        for(int x = j1.x; x <= j2.x; x++) {
            for(int y = j1.y; y <= j2.y; y++) {
                getField(x, y).junglify();
            }
        }
    }

    public Field getField(int n) { return this.fields[n]; }
    public Field getField(int x, int y) {
        x = (x + width) % this.width;
        y = (y + height) % this.height;
        return this.fields[y * this.width + x];
    }
    public Field getField(Vector2d v) {
        return getField(v.x % this.width, v.y % this.height);
    }

    public void display(int id) {
        Map m = this;
        JFrame f = new JFrame("Darwin simulator - Map #" + id);
        f.setLayout(new GridLayout(m.height, m.width));
        for(int y=height-1; y>=0; y--) {
            for(int x=0; x<width; x++) {
                f.add(m.getField(x, y).but);
            }
        }
        f.setSize(Math.min(m.width * 50, MainEngine.maxWindowWidth), Math.min(m.height * 50, MainEngine.maxWindowHeight));
        f.setLocation(50 * id, 50 * id);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
