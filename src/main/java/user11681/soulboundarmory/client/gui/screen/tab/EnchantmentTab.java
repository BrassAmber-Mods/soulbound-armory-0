package user11681.soulboundarmory.client.gui.screen.tab;

import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;
import user11681.cell.client.gui.screen.ScreenTab;
import user11681.cell.client.gui.widget.scalable.ScalableWidget;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Packets;

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
        Map<Enchantment, Integer> enchantments = storage.enchantments();
        int size = enchantments.size();
        ScalableWidget resetButton = this.add(this.resetButton(this.resetAction(enchantment)));
        resetButton.active = storage.getDatum(spentEnchantmentPoints) > 0;

        for (int row = 0; row < size; row++) {
            ButtonWidget disenchant = this.add(this.squareButton((width + 162) / 2 - 20, this.getHeight(size, row), new StringTextComponent("-"), this.disenchantAction(enchantments.getKey(row))));
            ButtonWidget enchant = this.add(this.squareButton((width + 162) / 2, this.getHeight(size, row), new StringTextComponent("+"), this.enchantAction(enchantments.getKey(row))));
            disenchant.active = enchantments.get(row) > 0;
            enchant.active = storage.getDatum(enchantmentPoints) > 0;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);

        Map<Enchantment, Integer> enchantments = this.storage.enchantments();
        int points = this.storage.getDatum(enchantmentPoints);

        if (points > 0) {
            drawCenteredString(matrices, this.textRenderer, String.format("%s: %d", Translations.menuUnspentPoints, points), Math.round(width / 2F), 4, 0xFFFFFF);
        }

        for (int row = 0, size = enchantments.size(); row < size; row++) {
            textRenderer.draw(matrices, enchantments.getKey(row).getName(enchantments.get(row)), (this.width - 182) / 2F, this.getHeight(size, row) - textRenderer.fontHeight / 2F, 0xFFFFFF);
        }
    }

    protected PressAction enchantAction(Enchantment enchantment) {
        return (ButtonWidget button) ->
            ClientPlayNetworking.send(Packets.serverEnchant, new ExtendedPacketBuffer(this.storage).writeResourceLocation(Registry.ENCHANTMENT.getId(enchantment)).writeBoolean(true).writeBoolean(hasShiftDown()));
    }

    protected PressAction disenchantAction(Enchantment enchantment) {
        return (ButtonWidget button) ->
            ClientPlayNetworking.send(Packets.serverEnchant, new ExtendedPacketBuffer(this.storage).writeResourceLocation(Registry.ENCHANTMENT.getId(enchantment)).writeBoolean(false).writeBoolean(hasShiftDown()));
    }
}
