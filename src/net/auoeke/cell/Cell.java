package net.auoeke.cell;

import net.minecraft.util.Identifier;

public class Cell {
    public static final String ID = "cell";

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }
}
