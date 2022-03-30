package soulboundarmory.client.gui.screen;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.module.gui.widget.Widget;
import soulboundarmory.module.gui.widget.WidgetBox;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;

public class EnchantmentTab extends SoulboundTab {
    public EnchantmentTab() {
        super(Translations.guiButtonEnchantments);
    }

    @Override
    public void initialize() {
        var component = this.container().item();
        var enchantments = component.enchantments;
        var length = Math.max(this.container().xpBar.width(), width(enchantments.reference2IntEntrySet().stream().map(entry -> entry.getKey().getName(entry.getIntValue()))) + 60);
        this.add(this.resetButton(Category.enchantment)).active(() -> enchantments.values().intStream().anyMatch(level -> level > 0));
        this.displayPoints(() -> this.container().item().intValue(StatisticType.enchantmentPoints));

        var box = this.add(new WidgetBox<>().ySpacing(4).center().x(.5).y(.5).width(length));
        enchantments.forEach(enchantment -> box.add(new Widget<>().width(length).height(20).with(row -> {
            row.add(this.squareButton("-", () -> this.enchant(enchantment, false)).alignRight().x(1, -20).active(() -> component.enchantment(enchantment) > 0));
            row.add(this.squareButton("+", () -> this.enchant(enchantment, true)).alignRight().x(1D).active(() -> component.enchantmentPoints() > 0));
            row.text(widget -> widget.shadow().text(() -> enchantment.getName(component.enchantment(enchantment))));
        })));
    }

    private void enchant(Enchantment enchantment, boolean enchant) {
        Packets.serverEnchant.send(new ExtendedPacketBuffer(this.container().item())
            .writeIdentifier(ForgeRegistries.ENCHANTMENTS.getKey(enchantment))
            .writeBoolean(enchant)
            .writeBoolean(isShiftDown())
        );
    }
}
