package dev;

import net.auoeke.reflect.Accessor;
import net.auoeke.reflect.Reflect;
import net.minecraft.server.command.CommandManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.tools.agent.MixinAgent;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.module.transform.SingleUseTransformer;

@EventBusSubscriber(modid = SoulboundArmory.ID)
public class DevCommonEvents {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        Accessor.<Logger>getReference(Accessor.<Object>getReference(CommandManager.class, "LOGGER"), "logger").setLevel(Level.DEBUG);
    }

    static {
        var instrumentation = Reflect.instrument().value();
        MixinAgent.agentmain(null, instrumentation);

        instrumentation.addTransformer(new SingleUseTransformer(instrumentation, "com/mojang/text2speech/Narrator", (module, loader, name, type, domain, classFile) -> {
            var node = new ClassNode();
            new ClassReader(classFile).accept(node, 0);

            var getNarrator = node.methods.stream().filter(method -> method.name.equals("getNarrator")).findAny().get();
            var dummy = "com/mojang/text2speech/NarratorDummy";
            getNarrator.instructions.clear();
            getNarrator.tryCatchBlocks.clear();
            getNarrator.visitTypeInsn(Opcodes.NEW, dummy);
            getNarrator.visitInsn(Opcodes.DUP);
            getNarrator.visitMethodInsn(Opcodes.INVOKESPECIAL, dummy, "<init>", "()V", false);
            getNarrator.visitInsn(Opcodes.ARETURN);

            var writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);

            return writer.toByteArray();
        }));
    }
}
