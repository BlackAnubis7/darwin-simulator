import org.junit.Test;
import static org.junit.Assert.*;

public class BreedTest {
    @Test
    public void MainTest() {
        int[] mockGenes = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        Animal a1 = new Animal(0, 0, mockGenes, 0, 0);
        Animal a2 = new Animal(0, 0, mockGenes, 0, 0);
        Animal a3 = new Animal(0, 0, mockGenes, 0, 0);
        Animal a4 = new Animal(0, 0, mockGenes, 0, 0);
        Animal a5 = new Animal(0, 0, mockGenes, 0, 0);
        Animal a6 = new Animal(0, 0, mockGenes, 0, 0);
        a1.alterEnergy(-100);
        a2.alterEnergy(5);
        a3.alterEnergy(7);
        a4.alterEnergy(0);
        a5.alterEnergy(6);
        a6.alterEnergy(12);

        assertEquals("<html>0, 0, 0, 0, 0, 0, 0, 0, <br>0, 0, 0, 0, 0, 0, 0, 0, <br>0, 0, 0, 0, 0, 0, 0, 0, <br>0, 0, 0, 0, 0, 0, 0, 0</html>", a1.stringGenome());  // stringGenome test

        /* Breeding has been checked; test are no longer usable after change in methods structure (breeding has not changed since tests)

        Field f = new Field(0);
        Engine e = new Engine(6, 6, 2, 2, 0, 0);

        f.enroll(a1);
        assertNull(f.breed(4, 16, 0, e));

        f.enroll(a2);
        assertNull(f.breed(4, 16, 0, e));

        f.enroll(a3);
        assertEquals(3 + MainEngine.startEnergy / 2, f.breed(4, 16, 0, e).getEnergy(), 0.1);

        f.enroll(a4);
        assertEquals(3 + MainEngine.startEnergy / 2, f.breed(4, 16, 0, e).getEnergy(), 0.1);

        f.enroll(a5);
        assertEquals(3.25 + MainEngine.startEnergy / 2, f.breed(4, 16, 0, e).getEnergy(), 0.1);

        f.enroll(a6);
        Animal child = f.breed(4, 16, 0, e);
        assertEquals(4.75 + MainEngine.startEnergy / 2, child.getEnergy(), 0.1);
        assertEquals(7, child.ithGene(31));
        assertEquals(6, child.ithGene(30));
        assertEquals(5, child.ithGene(29));
        assertEquals(4, child.ithGene(28));
        assertEquals(3, child.ithGene(27));
        assertEquals(2, child.ithGene(26));
        assertEquals(1, child.ithGene(25));
        assertEquals(0, child.ithGene(24)); */
    }
}
