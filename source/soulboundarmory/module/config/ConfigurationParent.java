package soulboundarmory.module.config;

import java.util.List;
import java.util.stream.Stream;
import net.auoeke.reflect.Classes;
import net.auoeke.reflect.Fields;
import net.auoeke.reflect.Flags;

public abstract class ConfigurationParent extends ConfigurationNode {
    public final Class<?> type;
    public final List<ConfigurationNode> children;

    public ConfigurationParent(Class<?> type, String name, String category) {
        super(name, category);

        this.type = Classes.initialize(type);
        this.children = Stream.concat(
            Fields.staticOf(type).filter(field -> !Flags.any(field, Flags.FINAL | Flags.TRANSIENT)).map(field -> new ConfigurationField(this, field)),
            Stream.of(type.getDeclaredClasses()).filter(t -> Flags.isStatic(t) && !ConfigurationFile.class.isAssignableFrom(t)).map(t -> new ConfigurationGroup(this, t))
        ).toList();
    }

    public Stream<ConfigurationNode> category(String category) {
        return this.children.stream().filter(child -> child.category.equals(category));
    }
}
