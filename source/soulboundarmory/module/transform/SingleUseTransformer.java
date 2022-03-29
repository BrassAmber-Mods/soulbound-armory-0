package soulboundarmory.module.transform;

import java.security.ProtectionDomain;
import java.util.function.Consumer;
import net.auoeke.reflect.Reflect;
import org.objectweb.asm.tree.ClassNode;

public class SingleUseTransformer extends ClassNodeTransformer {
    public SingleUseTransformer(String type, Consumer<ClassNode> transformer) {
        super(type.replace('.', '/')::equals, (node, context) -> transformer.accept(node));
    }

    @Override public byte[] transform(Module module, ClassLoader loader, String name, Class<?> type, ProtectionDomain domain, byte[] classFile) {
        if (this.predicate.test(name)) {
            Reflect.instrument().value().removeTransformer(this);
            return super.transform(module, loader, name, type, domain, classFile);
        }

        return null;
    }
}
