import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class InfoPanel {
    private final int id;
    private final JPanel panel;
    private final JLabel daily, entry;
    protected Animal selectedAnimal;
    private final Engine engine;
    private final DecimalFormat round = new DecimalFormat("#0.00");

    protected boolean highlightDominating = false;
    protected int x=0, y=0;
    protected double energy=0;
    protected int followedFrom = -1;
    protected int dayOfDeath=-1;
    protected Set<Animal> children = new HashSet<>();
    protected Set<Animal> family = new HashSet<>();

    private int skipTime;
    private final JButton skipDo;
    private final JButton control;
    private final JButton step;
    private final JButton highlight;

    public InfoPanel(int id) {
        this.id = id;
        this.engine = MainEngine.engines[this.id];
        this.panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(Asset.INFOPANELBORDER, new EmptyBorder(10, 10, 10, 10)));
        selectedAnimal = null;

        this.daily = new JLabel();
        panel.add(daily);

        this.entry = new JLabel();
        panel.add(entry);
        rewriteInfo();

        this.control = new JButton("Start");
        panel.add(control);
        control.addActionListener(e -> doControl());

        this.step = new JButton("Step");
        panel.add(step);
        step.addActionListener(e -> doStep());

        this.highlight = new JButton("Highlight");
        panel.add(highlight);
        highlight.addActionListener(e -> doHighlight());

        JButton export = new JButton("Write to .txt");
        panel.add(export);
        export.addActionListener(e -> writeStats());

        JPanel skipPanel = new JPanel();
        skipPanel.setLayout(new BoxLayout(skipPanel, BoxLayout.Y_AXIS));
        JButton skipMinus = new JButton("-");
        JButton skipPlus = new JButton("+");
        skipDo = new JButton("Skip 0 days");
        skipMinus.addActionListener(e -> alterSkip(-1));
        skipPlus.addActionListener(e -> alterSkip(1));
        skipDo.addActionListener(e -> doSkip());
        skipPanel.add(skipPlus);
        skipPanel.add(skipDo);
        skipPanel.add(skipMinus);
        skipPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
        skipPanel.setBackground(Asset.SKIPPANELBACK);
        panel.add(skipPanel);

        if(id == 0) {
            JButton controlAll = new JButton("TOGGLE ALL");
            controlAll.setFont(new Font("Consolas", Font.BOLD, 24));
            controlAll.setBackground(Asset.CTOGGLEALL);
            controlAll.setForeground(Asset.CTOGGLEFONT);
            panel.add(controlAll);
            controlAll.addActionListener(e -> doControlAll());
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    public int getID() {
        return this.id;
    }

    public void select(Animal newSelected) {
        if(this.selectedAnimal != newSelected) {
            this.selectedAnimal = newSelected;
            children.clear();
            family.clear();
            followedFrom = engine.getDay();
            dayOfDeath = -1;
        }
        updateInfo(engine.getDay());
    }
    public void reportBreed(Animal mom, Animal dad, Animal child) {
        if(selectedAnimal == mom || selectedAnimal == dad) {
            children.add(child);
            family.add(child);
        }
        else if(family.contains(mom) || family.contains(dad)) {
            family.add(child);
        }
    }
    public void reportDeath(Animal corpse) {
        if(corpse == this.selectedAnimal) {
            dayOfDeath = engine.getDay();
        }
    }
    public Animal getSelectedAnimal() {
        return this.selectedAnimal;
    }

    public void updateInfo(int day) {
        if(selectedAnimal != null) {
            this.x = selectedAnimal.getPos().x;
            this.y = selectedAnimal.getPos().y;
            this.energy = selectedAnimal.getEnergy();
        }
        rewriteInfo();
    }
    public void rewriteInfo() {
        daily.setText(
            "<html>Map #" + (this.id+1) + " | Day: " + engine.getDay() + "<br><br>" +
            "Nuber of animals: " + engine.getAnimalNumber() + "<br>" +
            "Average energy: " + round.format(engine.getAvgEnergy()) + "<br>" +
            "Average lifespan: " + (engine.getAvgLifespan() > 0 ? round.format(engine.getAvgLifespan()) : "-") + "<br>" +
            "Dominating gene: " + engine.getDominating() + "<br>" +
            "<br><html>"
        );
        ///
        if(selectedAnimal == null) {
            entry.setText("<html>Observed for: -<br>X: -<br>Y: -<br>Energy: -<br>Children: -<br>Descendants: -<br>Dominating gene(s): -<br>Genes:<br>_, _, _, _, _, _, _, _, <br>_, _, _, _, _, _, _, _, <br>_, _, _, _, _, _, _, _, <br>_, _, _, _, _, _, _, _, </html>");
        }
        else {
            String entryText = "<html>";
            entryText += "Observed for: " + (engine.getDay() - followedFrom) + " days<br>";
            entryText += "X: " + this.x + "<br>";
            entryText += "Y: " + this.y + "<br>";
            if(dayOfDeath < 0) {
                entryText += "Energy: " + round.format(this.energy) + "<br>";
            }
            else {
                entryText += "Day of death: " + this.dayOfDeath + "<br>";
            }
            entryText += "Children: " + this.children.size() + "<br>";
            entryText += "Descendants: " + this.family.size() + "<br>";
            entryText += "Dominating gene(s): " + selectedAnimal.getDominating() + "<br>";
            entryText += "Genes: <br>" + this.selectedAnimal.stringGenome();
            entryText += "</html>";
            entry.setText(entryText);
        }
    }

    public void doControl() {
        if(control.getText().equals("Start")) {
            control.setText("Pause");
            step.setEnabled(false);
            skipDo.setEnabled(false);
        }
        else {
            control.setText("Start");
            step.setEnabled(true);
            skipDo.setEnabled(true);
        }
    }
    public void doControlAll() {
        for(int i=0; i<MainEngine.mapNumber; i++) {
            MainEngine.infoPanels[i].doControl();
        }
    }
    public void doStep() {
        if(!isRunning()) engine.runCycle();
    }
    public void doHighlight() {
        if(highlightDominating) {
            highlightDominating = false;
            highlight.setText("Highlight");
            engine.unhighlightAll();
        }
        else {
            highlightDominating = true;
            highlight.setText("Unhighlight");
            engine.highlightAll();
        }
    }
    public void doSkip() {
        while(skipTime > 0) {
            engine.runCycle();
            alterSkip(-1);
        }
    }

    public void alterSkip(int delta) {
        this.skipTime += delta;
        if(this.skipTime < 0) skipTime = 0;
        this.skipDo.setText("Skip " + skipTime + " days");
    }

    public boolean isRunning() {
        return control.getText().equals("Pause");
    }

    public void writeStats() {
        FileWriter statFW;
        try {
            if(MainEngine.statFilename == null) {
                MainEngine.statFilename = generateFilename();
                statFW = new FileWriter(MainEngine.statFilename, true);
                statFW.write("### Darwin Simulation ###\n");
                for(int i=0; i<MainEngine.mapNumber; i++) {
                    statFW.write("Map #" + (i+1) + " | seed: " + MainEngine.engines[i].getSeed() + "\n");
                }
                statFW.write("#########################");
            }
            else {
                statFW = new FileWriter(MainEngine.statFilename, true);
            }
            //statFW.write("\n" + );
            statFW.write("\n\n--------------------------------------\nMap #" + (id+1) + " | Day: " + engine.getDay());
            statFW.write("\n--- Now ---");
            statFW.write("\nAnimals: " + engine.getAnimalNumber());
            statFW.write("\nPlants: " + engine.getPlantNum());
            statFW.write("\nDominating gene: " + engine.getDominating());
            statFW.write("\nAverage Energy: " + engine.getAvgEnergy());
            statFW.write("\nNumber of Children: " + engine.getAvgChildren());
            statFW.write("\n--- Average ---");
            statFW.write("\nAnimals: " + engine.getAvgAnimals());
            statFW.write("\nPlants: " + engine.getAvgPlants());
            statFW.write("\nDominating gene: " + engine.getAllTimeDomination());
            statFW.write("\nAverage Energy: " + engine.getAvgAvgEnergy());
            statFW.write("\nNumber of Children: " + engine.getAvgAvgChildren());
            statFW.write("\nLifespan: " + engine.getAvgLifespan());
            statFW.close();
        }
        catch(IOException e) {e.printStackTrace();}
    }

    private String generateFilename() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
        LocalDateTime now = LocalDateTime.now();
        String name = "./stats/stat";
        name += dtf.format(now);
        name += ".txt";
        return name;
    }
}
