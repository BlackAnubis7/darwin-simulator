import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

public class Field {

    private final ArrayList<Animal> animals;
    private Animal fittest;
    private double highestEnergy;
    private int numberOfFittest;
    private boolean ifPlant;
    private boolean ifJungle;
    private final int id;
    private boolean selected, highlighted;

    protected JButton but;

    public Field(int id) {
        this.id = id;
        animals = new ArrayList<>();
        fittest = null;
        highestEnergy = 0;
        numberOfFittest = 0;
        ifPlant = false;
        ifJungle = false;
        selected = false;
        highlighted = false;

        but = new JButton();
        but.setBackground(Asset.CPLAINS);
        but.setBorder(Asset.DEFAULTBORDER);

        but.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                  reportSelection();
              }
        });
        but.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) { redrawAnimal(); }
            @Override
            public void componentMoved(ComponentEvent e) { }
            @Override
            public void componentShown(ComponentEvent e) { }
            @Override
            public void componentHidden(ComponentEvent e) { }
        });
    }

    public boolean noPlant() {return !ifPlant;}
    public boolean isJungle() {return ifJungle;}
    public boolean noAnimal() {return animals.isEmpty();}
    public boolean isSelected() {return selected;}

    private void reportSelection() {
        MainEngine.select(this);
    }

    public Animal getFittest() {
        return this.fittest;
    }

    private int minDim() {
        return Math.min(this.but.getWidth(), this.but.getHeight());
    }
    private int minDim(Dimension d) {
        return Math.min(d.width, d.height);
    }

    protected void redrawAnimal() {
        if(animals.size() == 0 || fittest == null) {
            but.setIcon(null);
        }
        else {
            but.setIcon(new AnimalIcon(Asset.energyColor(highestEnergy / (MainEngine.startEnergy*2)), minDim(but.getSize())*Asset.relativeAnimalSize, fittest.getOrient()));
        }
    }

    protected void select() {
        this.selected = true;
        reborder();
    }
    protected void unselect() {
        this.selected = false;
        reborder();
    }
    protected void highlight() {
        this.highlighted = true;
        reborder();
    }
    protected void unhighlight() {
        this.highlighted = false;
        reborder();
    }
    private void reborder() {
        if(this.selected && this.highlighted) this.but.setBorder(Asset.BOTHBORDER);
        else if(this.selected) this.but.setBorder(Asset.SELECTBORDER);
        else if(this.highlighted) this.but.setBorder(Asset.HIGHLIGHTBORDER);
        else this.but.setBorder(Asset.DEFAULTBORDER);
    }

    public int getId() {
        return this.id;
    }

    protected void forceRecount() {
        this.highestEnergy = 0;
        for(Animal animal : animals) {
            if(animal.getEnergy() > this.highestEnergy) {
                this.highestEnergy = animal.getEnergy();
            }
        }
    }

    public void junglify() {
        ifJungle = true;
        but.setBackground(Asset.CJUNGLE);
    }
    public void plant() {
        ifPlant = true;
        if(ifJungle) but.setBackground(Asset.CTREE);
        else but.setBackground(Asset.CPLANT);
    }
    public void eat() {
        if(ifPlant && animals.size() > 0) {
            for(Animal animal : animals) {
                if(animal.getEnergy() == highestEnergy) {
                    animal.alterEnergy((double) MainEngine.plantEnergy / numberOfFittest);
                    if(animal.getEnergy() > highestEnergy) {
                        highestEnergy = animal.getEnergy();
                    }
                }
            }
            ifPlant = false;
            if(ifJungle) but.setBackground(Asset.CJUNGLE);
            else but.setBackground(Asset.CPLAINS);
        }
    }

    public void resetField() {
        animals.clear();
        fittest = null;
        highestEnergy = 0;
        numberOfFittest = 0;
    }
    public void enroll(Animal candidate) {
        if(candidate.getEnergy() > highestEnergy || animals.size() == 0) {
            highestEnergy = candidate.getEnergy();
            fittest = candidate;
            numberOfFittest = 1;
        }
        else if(candidate.getEnergy() == highestEnergy) {
            numberOfFittest += 1;
        }
        this.animals.add(candidate);
    }

    public Animal breed(int cut1, int cut2, int rot, Engine parentEngine) {
        if(animals.size() >= 2) {
            Animal parent1 = animals.get(0);
            Animal parent2 = animals.get(1);
            for(int i=2; i<animals.size(); i++) {
                if(animals.get(i).getEnergy() > parent1.getEnergy() && parent1.getEnergy() <= parent2.getEnergy()) {
                    parent1 = animals.get(i);
                }
                else if(animals.get(i).getEnergy() > parent2.getEnergy() && parent1.getEnergy() >= parent2.getEnergy()) {
                    parent2 = animals.get(i);
                }
            }
            if(parent1.getEnergy() >= MainEngine.startEnergy / 2 && parent2.getEnergy() >= MainEngine.startEnergy / 2) {
                if(cut2 >= cut1) cut2 += 1;
                int[] newGene = new int[32];
                int i=0;
                while(i <= Math.min(cut1, cut2)) {
                    newGene[i] = parent1.ithGene(i);
                    i++;
                }
                while(i <= Math.max(cut1, cut2)) {
                    newGene[i] = parent2.ithGene(i);
                    i++;
                }
                while(i < 32) {
                    newGene[i] = parent1.ithGene(i);
                    i++;
                }
                parentEngine.repairGenome(newGene);
                this.forceRecount();
                Animal child = new Animal(parent1.getPos(), newGene, rot, parent1.getEnergy() / 4 + parent2.getEnergy() / 4, parentEngine.getDay());
                parent1.alterEnergy(-parent1.getEnergy() / 4);
                parent2.alterEnergy(-parent2.getEnergy() / 4);
                MainEngine.infoPanels[MainEngine.identify(this)].reportBreed(parent1, parent2, child);
                parent1.addChild();
                parent2.addChild();
                return child;
            }
        }
        return null;
    }
}

class AnimalIcon implements Icon {
    private final Color color;
    private final double hgt;
    private final int orient;

    AnimalIcon(Color color, double hgt, int orient) {
        this.hgt = hgt;
        this.color = color;
        this.orient = orient;
    }

    public int getIconWidth() {
        return (int) hgt;
    }

    public int getIconHeight() {
        return (int) hgt;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Color.black);
        g.fillOval(x, y, getIconWidth(), getIconHeight());
        g.setColor(color);
        g.fillOval(x + Asset.borderWidth, y + Asset.borderWidth, getIconWidth() - 2 * Asset.borderWidth,
                getIconHeight() - 2 * Asset.borderWidth);
        g.setColor(Color.BLACK);
        int R = getIconWidth() / 2;
        int x1 = x + R;
        int y1 = y + R;
        int x2 = x1, y2 = y1;
        final double sin = 0.707;
        switch (orient) {
            case 0 -> {
                x2 = x1;
                y2 = y1 - R;
            }
            case 1 -> {
                x2 = (int) (x1 + sin * R);
                y2 = (int) (y1 - sin * R);
            }
            case 2 -> {
                x2 = x1 + R;
                y2 = y1;
            }
            case 3 -> {
                x2 = (int) (x1 + sin * R);
                y2 = (int) (y1 + sin * R);
            }
            case 4 -> {
                x2 = x1;
                y2 = y1 + R;
            }
            case 5 -> {
                x2 = (int) (x1 - sin * R);
                y2 = (int) (y1 + sin * R);
            }
            case 6 -> {
                x2 = x1 - R;
                y2 = y1;
            }
            case 7 -> {
                x2 = (int) (x1 - sin * R);
                y2 = (int) (y1 - sin * R);
            }
        }
        g.drawLine(x1, y1, x2, y2);
    }
}
