package soulboundarmory.module.config;

public interface ConfigurationFile {
    default void initializeClient() {}

    default void initializeServer() {}
}
