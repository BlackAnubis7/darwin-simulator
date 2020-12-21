import java.util.HashSet;
import java.util.Set;

public class Animal {
    private int posx, posy;
    private int orient;
    private double energy;
    private final int[] genes;
    private final int[] geneNumbers = {0, 0, 0, 0, 0, 0, 0, 0};
    private final int birthday;
    private final Set<Integer> dominatingGene;
    private int children=0;

    public Animal(int locx, int locy, int[] genome, int orientation, int birthday) {
        this.posx = locx;
        this.posy = locy;
        this.genes = genome;
        this.dominatingGene = this.countDominating();
        this.energy = MainEngine.startEnergy;
        this.orient = orientation;
        this.birthday = birthday;
    }
    public Animal(Vector2d pos, int[] genome, int orientation, double initialEnergy, int birthday) {
        this.posx = pos.x;
        this.posy = pos.y;
        this.genes = genome;
        this.dominatingGene = this.countDominating();
        this.orient = orientation;
        this.energy = initialEnergy;
        this.birthday = birthday;
    }

    public double getEnergy() {
        return this.energy;
    }
    public void alterEnergy(double delta) {
        energy += delta;
    }
    public Vector2d getPos() {
        return new Vector2d(posx, posy);
    }
    public int getOrient() {
        return this.orient;
    }
    public int getAge(int day) {
        return day - this.birthday;
    }

    public String stringGenome() {
        String genome = "<html>";
        for(int i=0; i<31; i++) {
            if(i%8 == 0 && i > 0) {
                genome += "<br>";
            }
            genome += String.valueOf(genes[i]) + ", ";
        }
        genome += String.valueOf(genes[31]) + "</html>";
        return genome;
    }

    protected Set<Integer> countDominating() {
        Set<Integer> dom = new HashSet<>();
        int domNum=0, num=0, nexti=0;
        for(int n=0; n<8; n++) {
            for(int i=nexti; i < 32 && genes[i] == n; i++) {
                num++;
                geneNumbers[n] += 1;
            }
            if(num > domNum) {
                dom.clear();
                dom.add(n);
                domNum = num;
            }
            else if(num == domNum) {
                dom.add(n);
            }
            nexti += num;
            num = 0;
        }
        return dom;
    }
    public Set<Integer> getDominating() {
        return this.dominatingGene;
    }
    public int ithGene(int i) {
        return genes[i];
    }
    public int[] getGenes() {
        return genes;
    }
    public int[] getGeneNumbers() {
        return geneNumbers;
    }

    public void rotate(int geneIndex) {
        orient = (orient + genes[geneIndex]) % 8;
    }
    public void move() {
        int xmov = Asset.XMOVEMENT[orient];
        int ymov = Asset.YMOVEMENT[orient];
        posx = (posx + xmov) >= 0 ? (posx + xmov) % MainEngine.width : MainEngine.width - 1;
        posy = (posy + ymov) >= 0 ? (posy + ymov) % MainEngine.height : MainEngine.height - 1;
    }
    public void move(int overrideSide) {
        int xmov = Asset.XMOVEMENT[overrideSide];
        int ymov = Asset.YMOVEMENT[overrideSide];
        posx = (posx + xmov) >= 0 ? (posx + xmov) % MainEngine.width : MainEngine.width - 1;
        posy = (posy + ymov) >= 0 ? (posy + ymov) % MainEngine.height : MainEngine.height - 1;
    }

    public void addChild() {
        this.children += 1;
    }
    public int getChildren() {
        return children;
    }
}
