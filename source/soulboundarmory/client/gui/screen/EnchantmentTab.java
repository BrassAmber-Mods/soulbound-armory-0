package soulboundarmory.client.gui.screen;

import net.minecraft.client.util.math.MatrixStack;
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
        var component = this.parent().item;
        var enchantments = component.enchantments;
        this.add(this.resetButton(this.resetAction(Category.enchantment))).active(enchantments.values().stream().anyMatch(level -> level > 0));

        Util.enumerate(enchantments, (enchantment, level, row) -> {
            this.add(this.squareButton(this.width() + 122 >> 1, this.height(enchantments.size(), row), "-", this.disenchantAction(enchantment))).active(level > 0);
            this.add(this.squareButton(this.width() + 162 >> 1, this.height(enchantments.size(), row), "+", this.enchantAction(enchantment))).active(component.intValue(StatisticType.enchantmentPoints) > 0);
        });
    }

    @Override
    public void render(MatrixStack matrixes, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixes, mouseX, mouseY, partialTicks);

        this.displayPoints(this.parent().item.intValue(StatisticType.enchantmentPoints));

        var enchantments = this.parent().item.enchantments;

        Util.enumerate(enchantments, (enchantment, level, row) -> textDrawer.drawWithShadow(
            matrixes,
            enchantment.getName(level),
            this.width() - 182 >> 1,
            this.height(enchantments.size(), row) - fontHeight() / 2F,
            0xFFFFFF
        ));
    }

    protected Runnable enchantAction(Enchantment enchantment) {
        return () -> Packets.serverEnchant.send(new ExtendedPacketBuffer(this.parent().item)
            .writeIdentifier(ForgeRegistries.ENCHANTMENTS.getKey(enchantment))
            .writeBoolean(true)
            .writeBoolean(isShiftDown())
        );
    }

    protected Runnable disenchantAction(Enchantment enchantment) {
        return () -> Packets.serverEnchant.send(new ExtendedPacketBuffer(this.parent().item)
            .writeIdentifier(ForgeRegistries.ENCHANTMENTS.getKey(enchantment))
            .writeBoolean(false)
            .writeBoolean(isShiftDown())
        );
    }
}
