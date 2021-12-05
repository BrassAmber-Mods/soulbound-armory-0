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
    public static final PacketKey.Server<ExtendedPacketBuffer, C2SAttribute> serverAttribute = server(C2SAttribute.class);
    public static final PacketKey.Server<ExtendedPacketBuffer, C2SBindSlot> serverBindSlot = server(C2SBindSlot.class);
    public static final PacketKey.Server<ExtendedPacketBuffer, C2SConfig> serverConfig = server(C2SConfig.class);
    public static final PacketKey.Server<ExtendedPacketBuffer, C2SEnchant> serverEnchant = server(C2SEnchant.class);
    public static final PacketKey.Server<ExtendedPacketBuffer, C2SItemType> serverItemType = server(C2SItemType.class);
    public static final PacketKey.Server<ExtendedPacketBuffer, C2SReset> serverReset = server(C2SReset.class);
    public static final PacketKey.Server<ExtendedPacketBuffer, C2SSkill> serverSkill = server(C2SSkill.class);
    public static final PacketKey.Server<ExtendedPacketBuffer, C2SSync> serverSync = server(C2SSync.class);

    public static final PacketKey.Client<ExtendedPacketBuffer, S2CEnchant> clientEnchant = client(S2CEnchant.class);
    public static final PacketKey.Client<ExtendedPacketBuffer, S2CItemType> clientItemType = client(S2CItemType.class);
    public static final PacketKey.Client<ExtendedPacketBuffer, S2COpenGUI> clientOpenGUI = client(S2COpenGUI.class);
    public static final PacketKey.Client<ExtendedPacketBuffer, S2CRefresh> clientRefresh = client(S2CRefresh.class);
    public static final PacketKey.Client<ExtendedPacketBuffer, S2CSync> clientSync = client(S2CSync.class);

    private static byte id;

    @SuppressWarnings("SameParameterValue")
    private static <T, P extends Packet<T>, K extends PacketKey<T, P>> K register(K key) {
        SoulboundArmory.channel.registerMessage(
            id++,
            key.type,
            Packet::write,
            buffer -> {
                var packet = key.instantiate();
                packet.read(buffer);

                return packet;
            },
            (packet, context) -> context.get().enqueueWork(() -> packet.execute(context.get()))
        );

        return key;
    }

    private static <T, P extends Packet<T>> PacketKey.Server<T, P> server(Class<P> type) {
        return register(new PacketKey.Server<>(type));
    }

    private static <T, P extends Packet<T>> PacketKey.Client<T, P> client(Class<P> type) {
        var key = new PacketKey.Client<>(type);
        return FMLEnvironment.dist == Dist.CLIENT ? register(key) : key;
    }
}
