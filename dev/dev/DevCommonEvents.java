package dev;

import net.auoeke.reflect.Accessor;
import net.auoeke.reflect.Reflect;
import net.minecraft.server.command.CommandManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.objectweb.asm.Opcodes;
import org.spongepowered.tools.agent.MixinAgent;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.module.transform.ClassNodeTransformer;

@EventBusSubscriber(modid = SoulboundArmory.ID)
public class DevCommonEvents implements Opcodes {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		Accessor.<Logger>getReference(Accessor.<Object>getReference(CommandManager.class, "LOGGER"), "logger").setLevel(Level.DEBUG);
	}

	static {
		MixinAgent.agentmain(null, Reflect.instrument().value());

		ClassNodeTransformer.addSingleUseTransformer("com/mojang/text2speech/Narrator", node -> {
			var getNarrator = node.methods.stream().filter(method -> method.name.equals("getNarrator")).findAny().get();
			var dummy = "com/mojang/text2speech/NarratorDummy";
			getNarrator.instructions.clear();
			getNarrator.tryCatchBlocks.clear();
			getNarrator.visitTypeInsn(NEW, dummy);
			getNarrator.visitInsn(DUP);
			getNarrator.visitMethodInsn(INVOKESPECIAL, dummy, "<init>", "()V", false);
			getNarrator.visitInsn(ARETURN);
		});
	}
}
