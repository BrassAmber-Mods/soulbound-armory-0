package soulboundarmory.registry;

import soulboundarmory.SoulboundArmory;
import soulboundarmory.network.ExtendedPacketBuffer;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import soulboundarmory.network.Packet;
import soulboundarmory.network.BufferPacket;

public class Packets {
    public static final BufferPacket serverAttribute = server(new C2SAttribute());
    public static final BufferPacket serverBindSlot = server(new C2SBindSlot());
    public static final BufferPacket serverConfig = server(new C2SConfig());
    public static final BufferPacket serverEnchant = server(new C2SEnchant());
    public static final BufferPacket serverItemType = server(new C2SItemType());
    public static final BufferPacket serverReset = server(new C2SReset());
    public static final BufferPacket serverSkill = server(new C2SSkill());
    public static final BufferPacket serverSync = server(new C2SSync());

    public static final BufferPacket clientEnchant = client(new S2CEnchant());
    public static final BufferPacket clientItemType = client(new S2CItemType());
    public static final BufferPacket clientOpenGUI = client(new S2COpenGUI());
    public static final BufferPacket clientRefresh = client(new S2CRefresh());
    public static final BufferPacket clientSync = client(new S2CSync());

    private static int id;

    @SuppressWarnings("SameParameterValue")
    private static <T, P extends Packet<T>> P register(P handler, Class<T> type) {
        SoulboundArmory.channel.registerMessage(
            id++,
            type,
            handler::write,
            handler::read,
            (buffer, context) -> context.get().enqueueWork(() -> {
                handler.execute(buffer, context.get());
                context.get().setPacketHandled(true);
            })
        );

        return handler;
    }

    private static <P extends Packet<ExtendedPacketBuffer>> P server(P handler) {
        return register(handler, ExtendedPacketBuffer.class);
    }

    private static <P extends Packet<ExtendedPacketBuffer>> P client(P handler) {
        return FMLEnvironment.dist == Dist.CLIENT ? register(handler, ExtendedPacketBuffer.class) : handler;
    }
}
