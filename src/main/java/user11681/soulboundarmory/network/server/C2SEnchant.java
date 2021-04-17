package user11681.soulboundarmory.network.server;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.statistics.StatisticType;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;

public class C2SEnchant implements ServerItemComponentPacket {
    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, ExtendedPacketBuffer buffer, PacketSender responder, ItemStorage<?> storage) {
        Enchantment enchantment = Registry.ENCHANTMENT.get(buffer.readIdentifier());
        boolean add = buffer.readBoolean();
        int change = add ? 1 : -1;

        if (buffer.readBoolean()) {
            change *= add ? storage.getDatum(StatisticType.enchantmentPoints) : storage.getEnchantment(enchantment);
        }

        storage.addEnchantment(enchantment, change);
        storage.refresh();
    }
}
