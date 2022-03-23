package dev;

import net.auoeke.reflect.Accessor;
import net.auoeke.reflect.Reflect;
import net.minecraft.server.command.CommandManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.spongepowered.tools.agent.MixinAgent;
import soulboundarmory.SoulboundArmory;

@EventBusSubscriber(modid = SoulboundArmory.ID)
public class DevCommonEvents {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        Accessor.<Logger>getReference(Accessor.<Object>getReference(CommandManager.class, "LOGGER"), "logger").setLevel(Level.DEBUG);
    }

    static {
        MixinAgent.agentmain(null, Reflect.instrumentation());
    }
}
