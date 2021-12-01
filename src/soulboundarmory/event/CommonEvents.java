package soulboundarmory.event;

import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.command.SoulboundArmoryCommand;

@EventBusSubscriber(modid = SoulboundArmory.ID)
public class CommonEvents {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        SoulboundArmoryCommand.register(event);
    }
}
