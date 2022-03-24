package soulboundarmory.module.gui.coordinate;

public class Offset {
    public Type type = Type.RELATIVE;
    public double value;

    /**
     Resolve the total absolute offset from a point on an interval with the given length.

     @param origin the interval's origin
     @param length the interval's length
     @return the offset
     */
    public int resolve(int origin, int length) {
        return (int) Math.round(this.type.resolve(origin) + length * this.value);
    }

    /**
     An offset from a parent.
     */
    public enum Type {
        /**
         No offset.
         */
        ABSOLUTE,

        /**
         Relative to the beginning of a parent.
         */
        RELATIVE;

        /**
         Calculate the offset from an interval.

         @param origin the origin of the interval
         @return the absolute value of the offset
         */
        public int resolve(int origin) {
            return switch (this) {
                case ABSOLUTE -> 0;
                case RELATIVE -> origin;
            };
        }
    }
}
