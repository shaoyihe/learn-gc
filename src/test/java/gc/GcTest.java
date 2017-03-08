package gc;

import org.junit.Test;

import java.util.Arrays;

/**
 * on 2017/3/7.
 */
public class GcTest {

    @Test
    public void testMarkSweepGc() {
        MarkAndSweepGc gc = new MarkAndSweepGc(60);
        int a = gc.put(1);
        int b = gc.put(2);
        int c = gc.put(3);
        gc.removeFromRoot(a);
        gc.removeFromRoot(c);
        int d = gc.put(4L);
        gc.put(4L);
        System.err.println(gc.getVals());
    }

    @Test
    public void testMarkCompactGc() {
        MarkAndCompactGc gc = new MarkAndCompactGc(60);
        int a = gc.put(1);
        int b = gc.put(2);
        int c = gc.put(3);
        gc.removeFromRoot(a);
        gc.removeFromRoot(c);
        int d = gc.put(4L);
        //数据碎片已合并为完整的块
        gc.put(4L);
        System.err.println(gc.getVals());
    }

    @Test
    public void testCopyGc() {
        CopingGc gc = new CopingGc(36 * 2);
        int a = gc.put(1);
        int b = gc.put(2);
        int c = gc.put(3);
        gc.removeFromRoot(a);
        gc.removeFromRoot(c);
        int d = gc.put(4L);
        //数据碎片已合并为完整的块
        gc.put(4L);
        System.err.println(gc.getVals());
    }

}
