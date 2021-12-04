package soulboundarmory.network;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.network.client.S2CEnchant;
import soulboundarmory.network.client.S2CItemType;
import soulboundarmory.network.client.S2COpenGUI;
import soulboundarmory.network.client.S2CRefresh;
import soulboundarmory.network.client.S2CSync;
import soulboundarmory.network.server.C2SAttribute;
import soulboundarmory.network.server.C2SBindSlot;
import soulboundarmory.network.server.C2SConfig;
import soulboundarmory.network.server.C2SEnchant;
import soulboundarmory.network.server.C2SItemType;
import soulboundarmory.network.server.C2SReset;
import soulboundarmory.network.server.C2SSkill;
import soulboundarmory.network.server.C2SSync;

public class Packets {
    public static final PacketKey<ExtendedPacketBuffer> serverAttribute = server(C2SAttribute.class);
    public static final PacketKey<ExtendedPacketBuffer> serverBindSlot = server(C2SBindSlot.class);
    public static final PacketKey<ExtendedPacketBuffer> serverConfig = server(C2SConfig.class);
    public static final PacketKey<ExtendedPacketBuffer> serverEnchant = server(C2SEnchant.class);
    public static final PacketKey<ExtendedPacketBuffer> serverItemType = server(C2SItemType.class);
    public static final PacketKey<ExtendedPacketBuffer> serverReset = server(C2SReset.class);
    public static final PacketKey<ExtendedPacketBuffer> serverSkill = server(C2SSkill.class);
    public static final PacketKey<ExtendedPacketBuffer> serverSync = server(C2SSync.class);

    public static final PacketKey<ExtendedPacketBuffer> clientEnchant = client(S2CEnchant.class);
    public static final PacketKey<ExtendedPacketBuffer> clientItemType = client(S2CItemType.class);
    public static final PacketKey<ExtendedPacketBuffer> clientOpenGUI = client(S2COpenGUI.class);
    public static final PacketKey<ExtendedPacketBuffer> clientRefresh = client(S2CRefresh.class);
    public static final PacketKey<ExtendedPacketBuffer> clientSync = client(S2CSync.class);

    private static byte id;

    @SuppressWarnings("SameParameterValue")
    private static <T, P extends Packet<T>> PacketKey<T> register(Class<P> type) {
        var key = new PacketKey<>(type);

        SoulboundArmory.channel.registerMessage(
            id++,
            type,
            Packet::write,
            buffer -> {
                var packet = (P) key.instantiate();
                packet.read(buffer);

                return packet;
            },
            (packet, context) -> context.get().enqueueWork(() -> packet.execute(context.get()))
        );

        return key;
    }

    private static <T, P extends Packet<T>> PacketKey<T> server(Class<P> type) {
        return register(type);
    }

    private static <T, P extends Packet<T>> PacketKey<T> client(Class<P> type) {
        return FMLEnvironment.dist == Dist.CLIENT ? register(type) : new PacketKey<>(type);
    }
}
