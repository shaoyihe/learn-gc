package gc;

import org.junit.Test;

import java.util.Arrays;

/**
 * on 2017/3/7.
 */
public class MarkAndSweepGcTest {

    @Test
    public void test() {
        MarkAndSweepGc gc = new MarkAndSweepGc(60);
        int a = gc.put(1);
        int b = gc.put(2);
        int c = gc.put(3);
        gc.removeFromRoot(a);
        gc.removeFromRoot(c);
        int d = gc.put(4L);
         d = gc.put(4L);
         d = gc.put(4L);
        System.err.println(gc.getVals());
    }
}
