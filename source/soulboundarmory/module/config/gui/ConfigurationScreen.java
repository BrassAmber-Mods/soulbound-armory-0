package soulboundarmory.module.config.gui;

import net.minecraft.client.gui.screen.Screen;
import soulboundarmory.module.config.Entry;
import soulboundarmory.module.gui.screen.ScreenDelegate;
import soulboundarmory.module.gui.screen.ScreenWidget;

public class ConfigurationScreen extends ScreenWidget<ConfigurationScreen> {
    protected final Entry<?> entry;

    public ConfigurationScreen(Entry<?> entry, Screen parent) {
        this.entry = entry;
        this.screen = new ScreenDelegate(this.title, this, parent);
    }
}
