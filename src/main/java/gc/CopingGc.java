package gc;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * mark - compact
 * on 2017/3/6.
 */
public class CopingGc {
    /**
     * （模拟）总内存
     */
    private final byte[] stores;

    /**
     * 当前块
     */
    private byte curBlock = 0;
    /**
     * 空闲偏差，剩余块一直是一个完整的数据块
     */
    private int emptyOffset;
    /**
     * root,值为内存块起点值
     */
    private Set<Integer> roots = new HashSet<>();

    public CopingGc(int initSize) {
        this.stores = new byte[initSize];
    }

    /**
     * @return (模拟)内存地址
     */
    public int put(int val) {
        Data<Integer> data = new Data<>(DataType.INTEGER, val);
        return put(data);
    }

    /**
     * 从roots移除地址
     *
     * @return
     */
    public boolean removeFromRoot(int address) {
        return roots.remove(address);
    }

    /**
     * @return 内存地址
     */
    public int put(long val) {
        MarkData<Long> data = new MarkData<>(DataType.LONG, val);
        return put(data);
    }

    public Set getVals() {
        //实际为递归调用
        return roots.stream().map((root) -> {
            return MarkData.getData(stores, root).getVal();
        }).collect(Collectors.toSet());
    }

    /**
     * @param data
     * @return
     */
    private int put(Data data) {
        if (emptySize() < data.getSize()) {
            gc();
        }
        if (emptySize() < data.getSize()) {
            throw new OutOfMemoryError();
        }
        return putInner(data);
    }

    /**
     * 写到空闲列表中
     *
     * @param data
     * @return
     */
    private int putInner(Data data) {
        int from = emptyOffset;
        //写入root
        roots.add(from);
        //写入block
        writeTo(data, from);
        emptyOffset += data.getSize();
        return from;
    }

    private void writeTo(Data data, int from) {
        System.arraycopy(data.getBytes(), 0, stores, from, data.getSize());
    }

    /**
     * 分配内存
     */
    private void gc() {
        copy();
    }

    /**
     * 清除压缩
     */
    private void copy() {
        curBlock ^= 1;
        int tempEmptyOffset = curBlock == 0 ? 0 : stores.length / 2;
        Set<Integer> tempRoots = new HashSet<>();
        for (int root : roots) {
            tempRoots.add(tempEmptyOffset);
            int size = Data.size(stores, root);
            System.arraycopy(stores, root, stores, tempEmptyOffset, size);
            tempEmptyOffset += size;
        }
        emptyOffset = tempEmptyOffset;
        roots = tempRoots;
    }

    /**
     * 空余尺寸
     *
     * @return
     */
    private int emptySize() {
        return curBlock == 0 ? stores.length / 2 - emptyOffset : stores.length - emptyOffset;
    }

}
