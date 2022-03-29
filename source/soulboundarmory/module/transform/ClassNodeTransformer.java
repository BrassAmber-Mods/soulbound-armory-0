package soulboundarmory.module.transform;

import java.security.ProtectionDomain;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import com.google.common.base.Predicates;
import net.auoeke.reflect.ClassTransformer;
import net.auoeke.reflect.Reflect;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ClassNodeTransformer implements ClassTransformer {
    public final Predicate<String> predicate;
    public final BiConsumer<ClassNode, Context> transformer;

    public ClassNodeTransformer(Predicate<String> predicate, BiConsumer<ClassNode, Context> transformer) {
        this.predicate = predicate;
        this.transformer = transformer;
    }

    public static void addTransformer(BiConsumer<ClassNode, Context> transformer) {
        Reflect.instrument().value().addTransformer(new ClassNodeTransformer(Predicates.alwaysTrue(), transformer));
    }

    public static void addTransformer(Consumer<ClassNode> transformer) {
        addTransformer((node, context) -> transformer.accept(node));
    }

    public static void addSingleUseTransformer(String name, Consumer<ClassNode> transformer) {
        Reflect.instrument().value().addTransformer(new SingleUseTransformer(name, transformer), true);
    }

    @Override public byte[] transform(Module module, ClassLoader loader, String name, Class<?> type, ProtectionDomain domain, byte[] classFile) {
        if (this.predicate.test(name)) {
            var node = new ClassNode();
            new ClassReader(classFile).accept(node, 0);
            this.transformer.accept(node, new Context(module, loader, name, type, domain, classFile));

            var writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);

            return writer.toByteArray();
        }

        return null;
    }

    public record Context(Module module, ClassLoader loader, String name, Class<?> type, ProtectionDomain domain, byte[] classFile) {}
}
