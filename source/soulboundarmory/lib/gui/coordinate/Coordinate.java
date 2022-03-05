package soulboundarmory.lib.gui.coordinate;

/**
 A point on an axis indicating one of start, center and end of the dimension of an element on the axis and possibly offset from a point on the element's parent.
 */
public final class Coordinate {
    public Offset offset = new Offset();
    public Position position = Position.START;

    private int value;

    /**
     Compute the absolute value of this coordinate.

     @param dimension the value of the dimension whereon this coordinate resides
     @param parentCoordinate the parent's origin on the axis
     @param parentDimension the parent's dimension on the axis
     @return this coordinate's value
     */
    public int resolve(int dimension, int parentCoordinate, int parentDimension) {
        return this.value + this.offset.resolve(parentCoordinate, parentDimension) + this.position.resolve(dimension);
    }

    public void set(int value) {
        this.value = value;
    }

    /**
     A position on an interval.
     */
    public enum Position {
        /**
         The beginning of an interval.
         */
        START,

        /**
         The center of an interval.
         */
        CENTER,

        /**
         The end of an interval.
         */
        END;

        /**
         Compute the displacement from this position to the beginning of an interval.

         @param interval the length of the interval
         @return the displacement
         */
        public int resolve(int interval) {
            return switch (this) {
                case START -> 0;
                case CENTER -> -interval / 2;
                case END -> -interval;
            };
        }
    }
}
