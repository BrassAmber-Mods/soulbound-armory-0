package user11681.soulboundarmory.network.client;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;

public class S2CEnchant implements ClientItemComponentPacket {
    @Override
    public void execute(MinecraftClient client, ClientPlayNetworkHandler handler, ExtendedPacketBuffer buffer, PacketSender responder, ItemStorage<?> storage) {
        Enchantment enchantment = Registry.ENCHANTMENT.get(buffer.readIdentifier());

        storage.addEnchantment(enchantment, buffer.readInt());
        storage.refresh();
    }
}
