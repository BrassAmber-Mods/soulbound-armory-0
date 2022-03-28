package soulboundarmory.module.transform;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import net.auoeke.reflect.ClassTransformer;

public record SingleUseTransformer(Instrumentation instrumentation, String type, ClassTransformer transformer) implements ClassTransformer {
    @Override public byte[] transform(Module module, ClassLoader loader, String name, Class<?> type, ProtectionDomain domain, byte[] classFile) {
        if (name.equals(this.type)) {
            this.instrumentation.removeTransformer(this);
            return this.transformer.transform(module, loader, name, type, domain, classFile);
        }

        return null;
    }
}
