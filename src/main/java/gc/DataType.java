package gc;

/**
 * on 2017/3/6.
 */
public enum DataType {
    INTEGER(0) {
        @Override
        public int size() {
            return Integer.BYTES;
        }
    },
    LONG(1) {
        @Override
        public int size() {
            return Long.BYTES;
        }
    };

    private int type;

    public abstract int size();

    DataType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static DataType fromType(int type) {
        for (DataType dataType : DataType.values()) {
            if (type == dataType.type) {
                return dataType;
            }
        }
        return null;
    }
}
