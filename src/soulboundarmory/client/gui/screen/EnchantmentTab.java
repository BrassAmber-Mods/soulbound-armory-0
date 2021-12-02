package soulboundarmory.client.gui.screen;

import cell.client.gui.widget.callback.PressCallback;
import cell.client.gui.widget.scalable.ScalableWidget;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.text.StringTextComponent;
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
            var disenchant = this.add(this.squareButton(this.width + 122 >> 1, this.height(enchantments.size(), row), new StringTextComponent("-"), this.disenchantAction(enchantment)));
            var enchant = this.add(this.squareButton(this.width + 162 >> 1, this.height(enchantments.size(), row), new StringTextComponent("+"), this.enchantAction(enchantment)));
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
            drawCenteredString(matrices, this.textRenderer, String.format("%s: %d", Translations.menuUnspentPoints, points), Math.round(this.width / 2F), 4, 0xFFFFFF);
        }

        Util.enumerate(enchantments, (enchantment, level, row) -> this.textRenderer.func_243246_a(
            matrices,
            enchantment.getDisplayName(level),
            (this.width - 182) / 2F,
            this.height(enchantments.size(), row) - this.textRenderer.FONT_HEIGHT / 2F,
            0xFFFFFF
        ));
    }

    protected PressCallback<ScalableWidget> enchantAction(Enchantment enchantment) {
        return button -> Packets.serverEnchant.send(new ExtendedPacketBuffer(this.parent.storage)
            .writeResourceLocation(ForgeRegistries.ENCHANTMENTS.getKey(enchantment))
            .writeBoolean(true)
            .writeBoolean(hasShiftDown())
        );
    }

    protected PressCallback<ScalableWidget> disenchantAction(Enchantment enchantment) {
        return button -> Packets.serverEnchant.send(new ExtendedPacketBuffer(this.parent.storage)
            .writeResourceLocation(ForgeRegistries.ENCHANTMENTS.getKey(enchantment))
            .writeBoolean(false)
            .writeBoolean(hasShiftDown())
        );
    }
}
