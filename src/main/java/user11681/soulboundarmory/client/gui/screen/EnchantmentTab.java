package user11681.soulboundarmory.client.gui.screen;

import net.minecraft.client.util.math.MatrixStack;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.LiteralText;
import net.minecraftforge.registries.ForgeRegistries;
import user11681.cell.client.gui.widget.callback.PressCallback;
import user11681.cell.client.gui.widget.scalable.ScalableWidget;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Packets;
import user11681.soulboundarmory.util.Util;

import static user11681.soulboundarmory.capability.statistics.Category.enchantment;
import static user11681.soulboundarmory.capability.statistics.StatisticType.enchantmentPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentEnchantmentPoints;

public class EnchantmentTab extends SoulboundTab {
    public EnchantmentTab() {
        super(Translations.menuButtonEnchantments);
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        super.init(client, width, height);

        ItemStorage<?> storage = this.parent.storage;
        Map<Enchantment, Integer> enchantments = storage.enchantments;
        ScalableWidget resetButton = this.add(this.resetButton(this.resetAction(enchantment)));
        resetButton.active = storage.datum(spentEnchantmentPoints) > 0;

        Util.enumerate(enchantments, (enchantment, level, row) -> {
            ScalableWidget disenchant = this.add(this.squareButton(width + 122 >> 1, this.height(enchantments.size(), row), new LiteralText("-"), this.disenchantAction(enchantment)));
            ScalableWidget enchant = this.add(this.squareButton(width + 162 >> 1, this.height(enchantments.size(), row), new LiteralText("+"), this.enchantAction(enchantment)));
            disenchant.active = level > 0;
            enchant.active = storage.datum(enchantmentPoints) > 0;
        });
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);

        Map<Enchantment, Integer> enchantments = this.parent.storage.enchantments;
        int points = this.parent.storage.datum(enchantmentPoints);

        if (points > 0) {
            drawCenteredText(matrices, this.textRenderer, String.format("%s: %d", Translations.menuUnspentPoints, points), Math.round(this.width / 2F), 4, 0xFFFFFF);
        }

        Util.enumerate(enchantments, (enchantment, level, row) -> this.textRenderer.draw(
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
