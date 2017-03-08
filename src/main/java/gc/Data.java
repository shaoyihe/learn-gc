package gc;

import java.nio.ByteBuffer;

/**
 * on 2017/3/8.
 */
public class Data<T> {

    //head
    /**
     * 当前data总长度
     */
    protected int size;

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
        this.size = Integer.BYTES + Integer.BYTES + type.size();
    }


    public int getSize() {
        return size;
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
    public static int size(byte[] tar, int from) {
        return ByteBuffer.wrap(tar, from, Integer.BYTES).getInt();
    }


    /**
     * mark字段
     *
     * @param tar
     * @param from
     */
    public static Data getData(byte[] tar, int from) {
        ByteBuffer buffer = ByteBuffer.wrap(tar, from, size(tar, from));
        int size = buffer.getInt();
        DataType type = DataType.fromType(buffer.getInt());
        if (type == DataType.INTEGER) {
            return new Data<Integer>(type, buffer.getInt());
        } else {
            return new Data<Long>(type, buffer.getLong());
        }
    }

}
