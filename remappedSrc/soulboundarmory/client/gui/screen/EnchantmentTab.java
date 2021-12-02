package soulboundarmory.client.gui.screen;

import I;
import cell.client.gui.widget.callback.PressCallback;
import cell.client.gui.widget.scalable.ScalableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.LiteralText;
import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.EnchantmentStorage;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.util.Util;

public class EnchantmentTab extends SoulboundTab {
    public EnchantmentTab() {
        super(Translations.menuButtonEnchantments);
    }

    @Override
    protected void init() {
        ItemStorage storage = this.parent.storage;
        EnchantmentStorage enchantments = storage.enchantments;
        ScalableWidget resetButton = this.add(this.resetButton(this.resetAction(Category.enchantment)));
        resetButton.active = storage.datum(StatisticType.spentEnchantmentPoints) > 0;

        Util.enumerate(enchantments, (enchantment, level, row) -> {
            ScalableWidget disenchant = this.add(this.squareButton(this.width + 122 >> 1, this.height(enchantments.size(), row), new LiteralText("-"), this.disenchantAction(enchantment)));
            ScalableWidget enchant = this.add(this.squareButton(this.width + 162 >> 1, this.height(enchantments.size(), row), new LiteralText("+"), this.enchantAction(enchantment)));
            disenchant.active = level > 0;
            enchant.active = storage.datum(StatisticType.enchantmentPoints) > 0;
        });
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);

        EnchantmentStorage enchantments = this.parent.storage.enchantments;
        I points = this.parent.storage.datum(StatisticType.enchantmentPoints);

        if (points > 0) {
            drawCenteredText(matrices, this.textRenderer, String.format("%s: %d", Translations.menuUnspentPoints, points), Math.round(this.width / 2F), 4, 0xFFFFFF);
        }

        Util.enumerate(enchantments, (enchantment, level, row) -> this.textRenderer.drawWithShadow(
            matrices,
            enchantment.getName(level),
            (this.width - 182) / 2F,
            this.height(enchantments.size(), row) - this.textRenderer.fontHeight / 2F,
            0xFFFFFF
        ));
    }

    protected PressCallback<ScalableWidget> enchantAction(Enchantment enchantment) {
        return button -> Packets.serverEnchant.send(new ExtendedPacketBuffer(this.parent.storage)
            .writeIdentifier(ForgeRegistries.ENCHANTMENTS.getKey(enchantment))
            .writeBoolean(true)
            .writeBoolean(hasShiftDown())
        );
    }

    protected PressCallback<ScalableWidget> disenchantAction(Enchantment enchantment) {
        return button -> Packets.serverEnchant.send(new ExtendedPacketBuffer(this.parent.storage)
            .writeIdentifier(ForgeRegistries.ENCHANTMENTS.getKey(enchantment))
            .writeBoolean(false)
            .writeBoolean(hasShiftDown())
        );
    }
}
