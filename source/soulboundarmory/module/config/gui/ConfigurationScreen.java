package soulboundarmory.module.config.gui;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.client.gui.screen.Screen;
import soulboundarmory.module.config.ConfigurationInstance;
import soulboundarmory.module.config.Node;
import soulboundarmory.module.gui.screen.ScreenWidget;
import soulboundarmory.module.gui.widget.ScalableWidget;
import soulboundarmory.module.gui.widget.WidgetBox;

public class ConfigurationScreen extends ScreenWidget<ConfigurationScreen> {
    protected final ConfigurationInstance instance;
    protected final WidgetBox categories = this.add(new WidgetBox<>().centerX().x(0.5).y(16).xSpacing(8));
    protected CategoryWidget category;

    public ConfigurationScreen(ConfigurationInstance instance, Screen parent) {
        super(parent);

        this.instance = instance;
        var categories = this.instance.children().collect(Collectors.groupingBy(node -> node.category));
        var main = categories.remove(this.instance.category);

        if (main != null) {
            this.addCategory(this.instance.category, main);
        }

        categories.forEach(this::addCategory);
    }

    @Override public void preinitialize() {
        keyboard.setRepeatEvents(true);
    }

    @Override protected void render() {
        this.renderBackground(this.instance.background);
        drawHorizontalLine(this.matrixes, 0, this.absoluteEndX(), this.category.absoluteY(), this.z(), 0xFF000000);
    }

    private void addCategory(String category, List<Node> nodes) {
        var tab = this.add(new CategoryWidget(nodes).with(c -> c.present(() -> c == this.category)));
        this.category = Objects.requireNonNullElse(this.category, tab);
        var button = this.categories.add(new ScalableWidget<>().button().text(category).width(b -> Math.max(70, b.descendantWidth() + 10)).height(20).active(() -> !tab.isPresent()).primaryAction(() -> this.category = tab));
        tab.y(t -> button.endY() + 20);
    }
}
