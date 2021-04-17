package user11681.usersmanual.asm.mm;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;

public class UMEarlyRiser implements Runnable {
    @Override
    public void run() {
        final String Operation = FabricLoader.getInstance().getMappingResolver().unmapClassName("named", "net.minecraft.entity.attribute.EntityAttributeModifier$Operation");

        ClassTinkerers.enumBuilder(Operation, int.class).addEnum("PERCENTAGE_ADDITION", 0x209365).build();
    }
}
