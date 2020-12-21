import java.util.*;

public class Engine extends Thread {
    private final int width, height;
    private final Vector2d j1, j2;
    private int freePlains, freeJungle;
    private final ArrayList<Animal> animals;
    private final Random gen;
    private final Map map;
    private final int id;
    private Field selectedField;
    private final long seed;

    private int day = 0;
    private int dominating = -1;
    private long sumLifespan=0, sumLifespanDivisor=0;
    private long sumAnimals=0;
    private long sumPlants=0;
    private double sumAvgEnergy=0;
    private int plantNum=0;
    private double avgEnergy=0;
    private double avgChildren=0;
    private double sumAvgChildren=0;
    private final long[] allTimeGenes = {0, 0, 0, 0, 0, 0, 0, 0};

    public Engine(int w, int h, int jw, int jh, long seed, int id) {
        this.seed = seed;
        width = w;
        height = h;
        this.id = id;
        selectedField = null;
        if(jw > w) jw = w;
        if(jh > h) jh = h;
        j1 = new Vector2d((w-jw)/2, (h-jh)/2);
        j2 = new Vector2d((w-jw)/2 + jw - 1, (h-jh)/2 + jh - 1);
        freeJungle = jw * jh;
        freePlains = w * h - freeJungle;
        gen = new Random(seed);
        animals = new ArrayList<>();

        map = new Map(w, h, j1, j2);
        map.display(id + 1);

        PlaceAdamEve();
        recountDomination();

        System.out.println("Generated Map #" + (id + 1) + " with seed " + seed);
    }

    public void run() {
        try {
            while(true) {
                if(MainEngine.infoPanels[this.id].isRunning()) {
                    runCycle();
                }
                sleep(MainEngine.sleepTime);
            }
        }
        catch(InterruptedException e) {
            // nothing
        }
    }
    public void runCycle() {
        day += 1;
        clearAll();
        corpseRemoval();
        moveAll();
        eatAll();
        breedAll();
        countAvg();
        recountDomination();
        if(MainEngine.infoPanels[this.id].highlightDominating) {
            unhighlightAll();
            highlightAll();
        }
        newPlants();
        redrawAndCount();
        reselectField();
        MainEngine.infoPanels[this.id].updateInfo(this.day);
    }

    private void corpseRemoval() {
        ArrayList<Animal> trashCan = new ArrayList<>();
        for(Animal animal : animals) {
            if(animal.getEnergy() <= 0) {
                MainEngine.infoPanels[this.id].reportDeath(animal);
                trashCan.add(animal);
            }
        }
        for(Animal animal : trashCan) {
            sumLifespan += animal.getAge(day);
            sumLifespanDivisor++;
            animals.remove(animal);
        }
    }
    private void clearAll() {
        for(int i=0; i<width*height; i++) {
            map.getField(i).resetField();

        }
    }
    private void eatAll() {
        for(int i=0; i<width*height; i++) {
            map.getField(i).eat();
        }
    }
    private void breedAll() {
        Animal child;
        ArrayList<Integer> sides = new ArrayList<>();
        ArrayList<Animal> newborns = new ArrayList<>();
        for(int i=0; i<width*height; i++) {
            child = map.getField(i).breed(randInt(31), randInt(30), randInt(8), this);
            if(child != null) {
                for(int s=0; s<8; s++) {
                    if(map.getField(i % width + Asset.XMOVEMENT[s], i / width + Asset.YMOVEMENT[s]).noAnimal()) {
                        sides.add(s);
                    }
                }
                if(sides.size() == 0) {
                    child.move(randInt(8));
                }
                else {
                    child.move(sides.get(randInt(sides.size())));
                }
                newborns.add(child);
            }
        }
        for(Animal newborn : newborns) {
            this.animals.add(newborn);
            map.getField(newborn.getPos()).enroll(newborn);
        }
    }
    private void moveAll() {
        for(Animal animal : animals) {
            animal.alterEnergy(-MainEngine.movementCost);
            animal.move();
            map.getField(animal.getPos()).enroll(animal);
            animal.rotate(randInt(32));
        }
    }
    private void countAvg() {
        if(animals == null) {
            this.avgEnergy = 0;
            this.avgChildren = 0;
        }
        else{
            double sum = 0, sumc = 0;
            for(Animal animal : animals) {
                sum += animal.getEnergy();
                sumc += animal.getChildren();
            }
            this.avgEnergy = sum / animals.size();
            this.sumAvgEnergy += this.avgEnergy;
            this.avgChildren = sumc / animals.size();
            this.sumAvgChildren += this.avgChildren;
        }
        sumAnimals += getAnimalNumber();
    }
    public void newPlants() {
        int p, j;
        p = freePlains > 0 ? randInt(freePlains) : -1;
        j = freeJungle > 0 ? randInt(freeJungle) : -1;
        for(int x=0; x<width; x++) {
            for(int y=0; y<height; y++) {
                if(map.getField(x, y).noPlant() && map.getField(x, y).noAnimal()) {  // field doesn't have a plant nor an animal
                    if (!j1.precedes(x, y) || !j2.follows(x, y)) {  // field is Plains
                        if (p == 0) {
                            map.getField(x, y).plant();
                            freePlains -= 1;
                        }
                        p -= 1;
                    } else {  // field is Jungle
                        if (j == 0) {
                            map.getField(x, y).plant();
                            freeJungle -= 1;
                        }
                        j -= 1;
                    }
                }
            }
        }
    }
    private void redrawAndCount() {
        int freeP = 0, freeJ = 0, plants = 0;
        Field field;
        for(int i=0; i<width*height; i++) {
            field = map.getField(i);
            field.redrawAnimal();
            if(field.noAnimal() && field.noPlant()) {
                if(field.isJungle()) freeJ += 1;
                else freeP += 1;
            }
            else if(!field.noPlant()) {
                plants += 1;
            }
        }
        this.freePlains = freeP;
        this.freeJungle = freeJ;
        this.plantNum = plants;
        this.sumPlants += plants;
    }
    protected void reselectField() {
        if(this.selectedField != null) {
            this.selectedField.unselect();
        }
        Animal newSelection = MainEngine.infoPanels[this.id].selectedAnimal;
        this.selectedField = newSelection == null ? null : map.getField(newSelection.getPos());
        if(this.selectedField != null) {
            this.selectedField.select();
        }
    }
    protected void unhighlightAll() {
        for(int i=0; i<width*height; i++) {
            map.getField(i).unhighlight();
        }
    }
    protected void highlightAll() {
        for(Animal animal : animals) {
            if(animal.getDominating().contains(this.dominating)) {
                map.getField(animal.getPos()).highlight();
            }
        }
    }
    protected void recountDomination() {
        int[] nums = {0, 0, 0, 0, 0, 0, 0, 0};
        for(Animal animal : animals) {
            for(int i=0; i<8; i++) {
                nums[i] += animal.getGeneNumbers()[i];
                allTimeGenes[i] += animal.getGeneNumbers()[i];
            }
        }
        int dom=0, domNum=0;
        for(int i=0; i<8; i++) {
            if(nums[i] > domNum) {
                dom = i;
                domNum = nums[i];
            }
        }
        this.dominating = dom;
    }

    public Field getField(int index) {
        return this.map.getField(index);
    }
    public int getDay() {
        return this.day;
    }
    public int getDominating() {
        return dominating;
    }
    public long getSeed() {
        return seed;
    }

    public double getAvgLifespan() {
        if(sumLifespanDivisor == 0) return 0;
        else {
            return (double) sumLifespan / sumLifespanDivisor;
        }
    }
    public double getAvgEnergy() {
        return avgEnergy;
    }
    public int getPlantNum() {
        return plantNum;
    }
    public int getAnimalNumber() {
        return animals.size();
    }
    public double getAvgAnimals() {
        if(day == 0) return animals.size();
        else return (double) sumAnimals / day;
    }
    public double getAvgPlants() {
        if(day == 0) return 0;
        else return (double) sumPlants / day;
    }
    public int getAllTimeDomination() {
        int dom=0;
        long domNum=0;
        for(int i=0; i<8; i++) {
            if(allTimeGenes[i] > domNum) {
                dom = i;
                domNum = allTimeGenes[i];
            }
        }
        return dom;
    }
    public double getAvgAvgEnergy() {
        if(day == 0) return 0;
        else return (double) sumAvgEnergy / day;
    }
    public double getAvgChildren() {
        return avgChildren;
    }
    public double getAvgAvgChildren() {
        if(day == 0) return 0;
        else return (double) sumAvgChildren / day;
    }

    private void PlaceAdamEve() {
        Set<Integer> adamEvePos = new HashSet<>();
        int n;
        while(adamEvePos.size() < MainEngine.startAnimals) {
            n = randInt(width * height);
            adamEvePos.add(n);
        }
        for(Integer pos : adamEvePos) {
            int posx = pos % this.width;
            int posy = pos / this.width;
            int[] genome = generateGenome();
            Animal adamEve = new Animal(posx, posy, genome, randInt(8), 0);
            this.animals.add(adamEve);
            if(map.getField(posx, posy).isJungle()) freeJungle -= 1;
            else freePlains -= 1;

            map.getField(posx, posy).enroll(adamEve);
            map.getField(posx, posy).redrawAnimal();
        }
    }

    private int randInt(int max) {  // randInt(n) returns number between 0 and n-1
        if(max == 0) return 0;
        else return (int) (this.gen.nextDouble() * max);
    }

    private int[] generateGenome() {
        int[] genome = new int[32];
        for(int i=0; i<32; i++) {
            genome[i] = randInt(8);
        }
        repairGenome(genome);
        return genome;
    }

    public void repairGenome(int[] genome) {
        int[] geneNumber = {0, 0, 0, 0, 0, 0, 0, 0};
        ArrayList<Integer> above1 = new ArrayList<>();
        int zeros = 0, n;
        for(int i=0; i<32; i++) {
            geneNumber[genome[i]] += 1;
        }
        for(int i=0; i<8; i++) {
            if(geneNumber[i] == 0) {
                zeros += 1;
                geneNumber[i] = 1;
            }
            else if(geneNumber[i] > 1) {
                above1.add(i);
            }
        }
        for(int i=0; i<zeros; i++) {
            n = randInt(above1.size());
            geneNumber[n] -= 1;
            if(geneNumber[n] == 1) above1.remove(n);
        }

        n = 0;
        for(int i=0; i<32; i++) {
            genome[i] = n;
            geneNumber[n] -= 1;
            if(geneNumber[n] == 0) n += 1;
        }
    }

    public void forceSleep(long milis) {
        try {
            sleep(milis);
        }
        catch(InterruptedException e) {System.out.println("interrupted");}
    }
}
