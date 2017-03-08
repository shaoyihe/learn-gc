package gc;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * on 2017/3/6.
 */
public class Data<T> {

    //head
    /**
     * 当前data总长度
     */
    private int size;
    /**
     * 是否被标记
     * 0 false , 1 true
     */
    private byte marked = 0;

    // body
    /**
     * 数据类型
     */
    private DataType type;

    /**
     * 值
     */
    private T val;

    public Data(DataType type, T val) {
        this.type = type;
        this.val = val;
        this.size = Integer.BYTES + Byte.BYTES + Integer.BYTES + type.size();
    }

    public int getSize() {
        return size;
    }

    public byte getMarked() {
        return marked;
    }

    public DataType getType() {
        return type;
    }


    public T getVal() {
        return val;
    }


    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(this.size);
        buffer.putInt(size);
        buffer.put(marked);
        buffer.putInt(type.getType());
        if (type == DataType.INTEGER) {
            buffer.putInt((Integer) val);
        } else {
            buffer.putLong((Long) val);
        }
        return buffer.array();
    }

    /**
     * mark字段
     *
     * @param tar
     * @param from
     */
    public static void mark(byte[] tar, int from) {
        //offset this.size 大小
        tar[Integer.BYTES + from] = 1;
    }

    /**
     * mark字段
     *
     * @param tar
     * @param from
     */
    public static Data getData(byte[] tar, int from) {
        int size = Data.size(tar, from);
        ByteBuffer buffer = ByteBuffer.wrap(Arrays.copyOfRange(tar, from, from + size));
        size = buffer.getInt();
        byte marked = buffer.get();
        DataType type = DataType.fromType(buffer.getInt());
        if (type == DataType.INTEGER) {
            return new Data<Integer>(type, buffer.getInt());
        } else {
            return new Data<Long>(type, buffer.getLong());
        }
    }

    /**
     * mark字段
     *
     * @param tar
     * @param from
     */
    public static void unMark(byte[] tar, int from) {
        //offset this.size 大小
        tar[Integer.BYTES + from] = 0;
    }

    /**
     * mark字段
     *
     * @param tar
     * @param from
     */
    public static int size(byte[] tar, int from) {
        return ByteBuffer.wrap(Arrays.copyOfRange(tar, from, from + Integer.BYTES)).getInt();
    }

    /**
     * 是否被标记
     *
     * @param tar
     * @param from
     */
    public static boolean isMarked(byte[] tar, int from) {
        //offset this.size 大小
        return tar[Integer.BYTES + from] == 1;
    }
}
