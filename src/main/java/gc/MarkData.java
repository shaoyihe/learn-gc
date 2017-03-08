package gc;

import java.nio.ByteBuffer;

/**
 * on 2017/3/6.
 */
public class MarkData<T> extends Data<T> {

    /**
     * 是否被标记
     * 0 false , 1 true
     */
    private byte marked = 0;


    public MarkData(DataType type, T val) {
        super(type, val);
        this.size = Integer.BYTES + Byte.BYTES + Integer.BYTES + type.size();
    }

    public byte getMarked() {
        return marked;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(this.size);
        buffer.putInt(size);
        buffer.put(marked);
        buffer.putInt(getType().getType());
        if (getType() == DataType.INTEGER) {
            buffer.putInt((Integer) getVal());
        } else {
            buffer.putLong((Long) getVal());
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
    public static MarkData getData(byte[] tar, int from) {
        ByteBuffer buffer = ByteBuffer.wrap(tar, from, MarkData.size(tar, from));
        int size = buffer.getInt();
        byte marked = buffer.get();
        DataType type = DataType.fromType(buffer.getInt());
        if (type == DataType.INTEGER) {
            return new MarkData<Integer>(type, buffer.getInt());
        } else {
            return new MarkData<Long>(type, buffer.getLong());
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
