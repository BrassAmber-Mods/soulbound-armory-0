package cell.client.gui.widget;

public final class Coordinate {
    public Type type = Type.RELATIVE;

    private int value;

    public int get(int parent, int parentLength) {
        return this.value + this.type.get(parent, parentLength);
    }

    public void set(int value) {
        this.value = value;
    }

    public enum Type {
        ABSOLUTE,
        RELATIVE,
        CENTER;

        public int get(int parent, int parentLength) {
            return switch (this) {
                case ABSOLUTE -> 0;
                case RELATIVE -> parent;
                case CENTER -> parent + parentLength / 2;
            };
        }
    }
}
