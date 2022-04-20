package soulboundarmory.module.config;

import java.util.Map;
import java.util.stream.Stream;
import it.unimi.dsi.fastutil.objects.Object2ReferenceLinkedOpenHashMap;
import net.auoeke.reflect.Classes;
import net.auoeke.reflect.Fields;
import net.auoeke.reflect.Flags;

public abstract class Parent extends Node {
    public final Class<?> type;
    public final Map<String, Node> children = new Object2ReferenceLinkedOpenHashMap<>();

    public Parent(Class<?> type, String name, String category) {
        super(name, category);

        this.type = Classes.initialize(type);
        Stream.concat(
            Fields.staticOf(type).filter(field -> !Flags.any(field, Flags.FINAL | Flags.TRANSIENT)).map(field -> new Property<>(this, field)),
            Stream.of(type.getDeclaredClasses()).filter(inner -> Flags.isStatic(inner) && !inner.isAnnotationPresent(ConfigurationFile.class)).map(t -> new Group(this, t))
        ).forEach(node -> this.children.put(node.name, node));
    }

    public Stream<Node> category(String category) {
        return this.children.values().stream().filter(child -> child.category.equals(category));
    }

    public Stream<Node> children() {
        return this.children.values().stream().flatMap(node -> node instanceof Group group && group.flat ? group.children() : Stream.of(node));
    }
}
