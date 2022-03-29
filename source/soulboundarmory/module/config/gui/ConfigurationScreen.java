package soulboundarmory.module.config.gui;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.gui.screen.Screen;
import soulboundarmory.module.config.Entry;
import soulboundarmory.module.config.Node;
import soulboundarmory.module.gui.screen.ScreenWidget;
import soulboundarmory.module.gui.widget.ScalableWidget;
import soulboundarmory.module.gui.widget.WidgetBox;

public class ConfigurationScreen extends ScreenWidget<ConfigurationScreen> {
    protected final Entry<?> entry;
    protected final List<CategoryWidget> categories = ReferenceArrayList.of();
    protected final WidgetBox box = this.add(new WidgetBox().centerX().x(0.5).y(16).xSpacing(8));
    protected CategoryWidget category;

    public ConfigurationScreen(Entry<?> entry, Screen parent) {
        super(parent);

        this.entry = entry;
        var categories = this.entry.children().collect(Collectors.groupingBy(node -> node.category));
        var main = categories.remove(this.entry.category);

        if (main != null) {
            this.addCategory(this.entry.category, main);
        }

        categories.forEach(this::addCategory);
    }

    @Override public void preinitialize() {}

    @Override protected void render() {
        this.renderBackground(this.entry.background);
        drawHorizontalLine(this.matrixes, 0, this.absoluteEndX(), this.category.absoluteY(), this.z(), 0xFF000000);
    }

    private void addCategory(String category, List<Node> nodes) {
        var tab = this.add(new CategoryWidget(nodes).with(c -> c.present(() -> c == this.category)));
        this.category = Objects.requireNonNullElse(this.category, tab);
        var button = this.box.add(new ScalableWidget<>().button().text(category).width(b -> Math.max(70, b.descendantWidth() + 10)).height(20).active(() -> !tab.isPresent()).primaryAction(() -> this.category = tab));
        tab.y(t -> button.endY() + 20);
    }
}
