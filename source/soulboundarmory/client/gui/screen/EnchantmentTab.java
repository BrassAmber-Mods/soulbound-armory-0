package soulboundarmory.client.gui.screen;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.util.Util;

public class EnchantmentTab extends SoulboundTab {
    public EnchantmentTab() {
        super(Translations.guiButtonEnchantments);
    }

    @Override
    public void initialize() {
        var component = this.container().item();
        var enchantments = component.enchantments;
        this.add(this.resetButton(Category.enchantment)).active(() -> enchantments.values().intStream().anyMatch(level -> level > 0));
        this.displayPoints(() -> this.container().item().intValue(StatisticType.enchantmentPoints));

        Util.enumerate(enchantments, (enchantment, level, row) -> {
            this.add(this.squareButton(122 / 2, this.height(enchantments.size(), row), "-", () -> this.enchant(enchantment, false))).active(() -> component.enchantment(enchantment) > 0);
            this.add(this.squareButton(162 / 2, this.height(enchantments.size(), row), "+", () -> this.enchant(enchantment, true))).active(() -> component.intValue(StatisticType.enchantmentPoints) > 0);
            this.text(text -> text.shadow().centerY().x(.5, -91).y(this.height(enchantments.size(), row)).text(() -> enchantment.getName(component.enchantment(enchantment))));
        });
    }

    private void enchant(Enchantment enchantment, boolean enchant) {
        Packets.serverEnchant.send(new ExtendedPacketBuffer(this.container().item())
            .writeIdentifier(ForgeRegistries.ENCHANTMENTS.getKey(enchantment))
            .writeBoolean(enchant)
            .writeBoolean(isShiftDown())
        );
    }
}
