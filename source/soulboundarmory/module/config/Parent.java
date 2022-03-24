package soulboundarmory.module.config;

import java.util.List;
import java.util.stream.Stream;
import net.auoeke.reflect.Classes;
import net.auoeke.reflect.Fields;
import net.auoeke.reflect.Flags;

public abstract class Parent extends Node {
    public final Class<?> type;
    public final List<Node> children;

    public Parent(Class<?> type, String name, String category) {
        super(name, category);

        this.type = Classes.initialize(type);
        this.children = Stream.concat(
            Fields.staticOf(type).filter(field -> !Flags.any(field, Flags.FINAL | Flags.TRANSIENT)).map(field -> new Property<>(this, field)),
            Stream.of(type.getDeclaredClasses()).filter(t -> Flags.isStatic(t) && !ConfigurationFile.class.isAssignableFrom(t)).map(t -> new Group(this, t))
        ).toList();
    }

    public Stream<Node> category(String category) {
        return this.children.stream().filter(child -> child.category.equals(category));
    }
}
