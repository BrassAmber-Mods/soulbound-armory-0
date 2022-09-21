package soulboundarmory.module.transform;

import java.security.ProtectionDomain;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.auoeke.reflect.ClassTransformer;
import net.auoeke.reflect.Reflect;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ClassNodeTransformer implements ClassTransformer {
    public final BiConsumer<ClassNode, Context> transformer;

    public ClassNodeTransformer(BiConsumer<ClassNode, Context> transformer) {
        this.transformer = transformer;
    }

    public static void addTransformer(BiConsumer<ClassNode, Context> transformer) {
        Reflect.instrument().value().addTransformer(new ClassNodeTransformer(transformer));
    }

    public static void addSingleUseTransformer(String name, Consumer<ClassNode> transformer) {
        var instrumentation = Reflect.instrument().value();
        instrumentation.addTransformer(new ClassNodeTransformer((node, context) -> transformer.accept(node)).exceptionLogging().ofType(name).singleUse(instrumentation), true);
    }

    @Override public byte[] transform(Module module, ClassLoader loader, String name, Class<?> type, ProtectionDomain domain, byte[] classFile) {
        var node = new ClassNode();
        new ClassReader(classFile).accept(node, 0);
        this.transformer.accept(node, new Context(module, loader, name, type, domain, classFile));

        var writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);

        return writer.toByteArray();
    }

    public record Context(Module module, ClassLoader loader, String name, Class<?> type, ProtectionDomain domain, byte[] classFile) {}
}
