package soulboundarmory.client.gui.screen;

import cell.client.gui.widget.callback.PressCallback;
import cell.client.gui.widget.scalable.ScalableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Text;
import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.statistics.Category;
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
        var storage = this.parent.storage;
        var enchantments = storage.enchantments;
        var resetButton = this.add(this.resetButton(this.resetAction(Category.enchantment)));
        resetButton.active = storage.datum(StatisticType.spentEnchantmentPoints) > 0;

        Util.enumerate(enchantments, (enchantment, level, row) -> {
            var disenchant = this.add(this.squareButton(this.width + 122 >> 1, this.height(enchantments.size(), row), Text.of("-"), this.disenchantAction(enchantment)));
            var enchant = this.add(this.squareButton(this.width + 162 >> 1, this.height(enchantments.size(), row), Text.of("+"), this.enchantAction(enchantment)));
            disenchant.active = level > 0;
            enchant.active = storage.datum(StatisticType.enchantmentPoints) > 0;
        });
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);

        var enchantments = this.parent.storage.enchantments;
        var points = this.parent.storage.datum(StatisticType.enchantmentPoints);

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
