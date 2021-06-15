package user11681.soulboundarmory.client.gui.screen.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import user11681.cell.client.gui.widget.callback.PressCallback;
import user11681.cell.client.gui.widget.scalable.ScalableWidget;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Packets;
import user11681.soulboundarmory.util.Util;

import static user11681.soulboundarmory.capability.statistics.Category.enchantment;
import static user11681.soulboundarmory.capability.statistics.StatisticType.enchantmentPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentEnchantmentPoints;

public class EnchantmentTab extends SoulboundTab {
    public EnchantmentTab(SoulboundCapability component, List<ScreenTab> tabs) {
        super(Translations.menuButtonEnchantments, component, tabs);
    }

    @Override
    public void init(Minecraft client, int width, int height) {
        super.init(client, width, height);

        ItemStorage<?> storage = this.storage;
        Map<Enchantment, Integer> enchantments = storage.enchantments;
        ScalableWidget resetButton = this.add(this.resetButton(this.resetAction(enchantment)));
        resetButton.active = storage.datum(spentEnchantmentPoints) > 0;

        Util.enumerate(enchantments, (enchantment, level, row) -> {
            ScalableWidget disenchant = this.add(this.squareButton((width + 162) / 2 - 20, this.height(enchantments.size(), row), new StringTextComponent("-"), this.disenchantAction(enchantment)));
            ScalableWidget enchant = this.add(this.squareButton((width + 162) / 2, this.height(enchantments.size(), row), new StringTextComponent("+"), this.enchantAction(enchantment)));
            disenchant.active = level > 0;
            enchant.active = storage.datum(enchantmentPoints) > 0;
        });
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);

        Map<Enchantment, Integer> enchantments = this.storage.enchantments;
        int points = this.storage.datum(enchantmentPoints);

        if (points > 0) {
            drawCenteredString(matrices, this.fontRenderer, String.format("%s: %d", Translations.menuUnspentPoints, points), Math.round(width / 2F), 4, 0xFFFFFF);
        }

        Util.enumerate(enchantments, (enchantment, level, row) -> fontRenderer.draw(
            matrices,
            enchantment.getFullname(level),
            (this.width - 182) / 2F,
            this.height(enchantments.size(), row) - fontRenderer.lineHeight / 2F,
            0xFFFFFF
        ));
    }

    protected PressCallback<ScalableWidget> enchantAction(Enchantment enchantment) {
        return button -> Packets.serverEnchant.send(new ExtendedPacketBuffer(this.storage).writeResourceLocation(ForgeRegistries.ENCHANTMENTS.getKey(enchantment)).writeBoolean(true).writeBoolean(hasShiftDown()));
    }

    protected PressCallback<ScalableWidget> disenchantAction(Enchantment enchantment) {
        return button -> Packets.serverEnchant.send(new ExtendedPacketBuffer(this.storage).writeResourceLocation(ForgeRegistries.ENCHANTMENTS.getKey(enchantment)).writeBoolean(false).writeBoolean(hasShiftDown()));
    }
}
