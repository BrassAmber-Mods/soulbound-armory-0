package net.auoeke.soulboundarmory.registry;

import net.auoeke.soulboundarmory.SoulboundArmory;
import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.network.client.S2CEnchant;
import net.auoeke.soulboundarmory.network.client.S2CItemType;
import net.auoeke.soulboundarmory.network.client.S2COpenGUI;
import net.auoeke.soulboundarmory.network.client.S2CRefresh;
import net.auoeke.soulboundarmory.network.client.S2CSync;
import net.auoeke.soulboundarmory.network.server.C2SAttribute;
import net.auoeke.soulboundarmory.network.server.C2SBindSlot;
import net.auoeke.soulboundarmory.network.server.C2SConfig;
import net.auoeke.soulboundarmory.network.server.C2SEnchant;
import net.auoeke.soulboundarmory.network.server.C2SItemType;
import net.auoeke.soulboundarmory.network.server.C2SReset;
import net.auoeke.soulboundarmory.network.server.C2SSkill;
import net.auoeke.soulboundarmory.network.server.C2SSync;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.auoeke.soulboundarmory.network.Packet;
import net.auoeke.soulboundarmory.network.SimplePacket;

public class Packets {
    public static final SimplePacket serverAttribute = server(new C2SAttribute());
    public static final SimplePacket serverBindSlot = server(new C2SBindSlot());
    public static final SimplePacket serverConfig = server(new C2SConfig());
    public static final SimplePacket serverEnchant = server(new C2SEnchant());
    public static final SimplePacket serverItemType = server(new C2SItemType());
    public static final SimplePacket serverReset = server(new C2SReset());
    public static final SimplePacket serverSkill = server(new C2SSkill());
    public static final SimplePacket serverSync = server(new C2SSync());

    public static final SimplePacket clientEnchant = client(new S2CEnchant());
    public static final SimplePacket clientItemType = client(new S2CItemType());
    public static final SimplePacket clientOpenGUI = client(new S2COpenGUI());
    public static final SimplePacket clientRefresh = client(new S2CRefresh());
    public static final SimplePacket clientSync = client(new S2CSync());

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
