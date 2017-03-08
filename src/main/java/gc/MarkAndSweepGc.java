package gc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * mark - sweep
 * on 2017/3/6.
 */
public class MarkAndSweepGc {
    /**
     * （模拟）总内存
     */
    private final byte[] stores;
    /**
     * 空闲列表
     */
    private List<Block> emptyList = new ArrayList<>();
    private int emptySize;
    /**
     * root,值为内存块起点值
     */
    private Set<Integer> roots = new HashSet<>();

    public MarkAndSweepGc(int initSize) {
        this.emptySize = initSize;
        this.stores = new byte[initSize];
        emptyList.add(new Block(0, initSize));
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
        if (emptySize < data.getSize()) {
            gc();
        }
        if (emptySize < data.getSize()) {
            throw new OutOfMemoryError();
        }
        int from = putInner(data);
        if (from >= 0) {
            return from;
        }
        gc();
        from = putInner(data);
        if (from >= 0) {
            return from;
        }
        throw new OutOfMemoryError();
    }

    /**
     * 写到空闲列表中
     *
     * @param data
     * @return
     */
    private int putInner(Data data) {
        for (Block block : emptyList) {
            if (block.size >= data.getSize()) {
                int from = block.from;
                //写入root
                roots.add(from);
                //block重写
                writeTo(data, from);
                block.size -= data.getSize();
                emptySize -= data.getSize();
                block.from += data.getSize();
                return from;
            }
        }
        return -1;
    }

    private void writeTo(Data data, int from) {
        System.arraycopy(data.getBytes(), 0, stores, from, data.getSize());
    }

    /**
     * 分配内存
     */
    private void gc() {
        mark();
        sweep();
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
     * 清除
     */
    private void sweep() {
        //重写empty list
        List<Block> newEmptyList = new ArrayList<>();
        for (int offset = 0, emptyPos = 0; offset < stores.length; ) {
            boolean hadApply = false;
            //一直找到不在空列表的offset
            while (emptyPos < emptyList.size() && emptyList.get(emptyPos).from == offset) {
                if (emptyList.get(emptyPos).size > 0) {
                    offset += emptyList.get(emptyPos).size;
                    newEmptyList.add(emptyList.get(emptyPos));
                }
                ++emptyPos;
                hadApply = true;
            }
            if (hadApply) {
                continue;
            }

            //object
            int objSize = Data.size(stores, offset);
            if (Data.isMarked(stores, offset)) {
                Data.unMark(stores, offset);
            } else {
                //未被标记的块放入空闲列表中
                newEmptyList.add(new Block(offset, objSize));
            }
            offset += objSize;
        }
        emptyList = newEmptyList;
        //重写emptyList尺寸
        emptySize = emptyList.parallelStream().mapToInt(Block::getSize).sum();
    }


    class Block {
        /**
         * 起点
         */
        int from;
        /**
         * 大小
         */
        int size;

        Block(int from, int size) {
            this.from = from;
            this.size = size;
        }

        public int getFrom() {
            return from;
        }

        public int getSize() {
            return size;
        }
    }
}
