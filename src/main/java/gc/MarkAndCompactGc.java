package gc;

import java.util.*;
import java.util.stream.Collectors;

/**
 * mark - compact
 * on 2017/3/6.
 */
public class MarkAndCompactGc {
    /**
     * （模拟）总内存
     */
    private final byte[] stores;

    /**
     * 空闲偏差，剩余块一直是一个完整的数据块
     */
    private int emptyOffset;
    /**
     * root,值为内存块起点值
     */
    private Set<Integer> roots = new HashSet<>();

    public MarkAndCompactGc(int initSize) {
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
     * @return (模拟)内存地址
     */
    public boolean removeFromRoot(int address) {
        return roots.remove(address);
    }

    /**
     * @return 内存地址
     */
    public int put(long val) {
        Data<Long> data = new Data<>(DataType.LONG, val);
        return put(data);
    }

    public Set getVals() {
        //实际为递归调用
        return roots.stream().map((root) -> {
            return Data.getData(stores, root).getVal();
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
        mark();
        compact();
    }

    /**
     * 标记（此处没有递归引用，只做标记）
     */
    private void mark() {
        for (int from : roots) {
            Data.mark(stores, from);
        }
    }

    /**
     * 清除压缩
     */
    private void compact() {
        //上次空闲起点
        int lastEmptyStart = 0;
        for (int dataOffset = 0; dataOffset < this.emptyOffset; ) {
            //object
            int objSize = Data.size(stores, dataOffset);
            if (Data.isMarked(stores, dataOffset)) {
                Data.unMark(stores, dataOffset);
                //如果数据块左边有空块，则移动之
                if (dataOffset > lastEmptyStart) {
                    //重写roots
                    roots.remove(dataOffset);
                    roots.add(lastEmptyStart);

                    System.arraycopy(stores, dataOffset, stores, lastEmptyStart, objSize);
                }
                lastEmptyStart += objSize;
            }
            dataOffset += objSize;
        }
        this.emptyOffset = lastEmptyStart;
    }

    /**
     * 空余尺寸
     * @return
     */
    private int emptySize() {
        return stores.length - emptyOffset;
    }

}
