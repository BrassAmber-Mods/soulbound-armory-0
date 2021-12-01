package cell;

import net.minecraft.util.ResourceLocation;

public class Cell {
    public static final String ID = "cell";

    public static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }
}
