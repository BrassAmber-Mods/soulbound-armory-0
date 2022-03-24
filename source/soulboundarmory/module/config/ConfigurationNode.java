package soulboundarmory.module.config;

import java.util.Objects;

public class ConfigurationNode {
    public final String name;
    public final String category;

    public ConfigurationNode(String name, String category) {
        this.name = Objects.requireNonNull(name);
        this.category = Objects.requireNonNull(category);
    }
}
