package dev;

import net.auoeke.reflect.Reflect;
import net.minecraftforge.fml.common.Mod;
import org.spongepowered.tools.agent.MixinAgent;
import soulboundarmory.SoulboundArmory;

@Mod.EventBusSubscriber(modid = SoulboundArmory.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Dev {
    static {
        MixinAgent.agentmain(null, Reflect.instrumentation());
    }
}
