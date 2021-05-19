package user11681.soulboundarmory.registry;

import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.network.Packet;
import user11681.soulboundarmory.network.SimplePacket;
import user11681.soulboundarmory.network.client.S2CEnchant;
import user11681.soulboundarmory.network.client.S2CItemType;
import user11681.soulboundarmory.network.client.S2COpenGUI;
import user11681.soulboundarmory.network.client.S2CRefresh;
import user11681.soulboundarmory.network.client.S2CSync;
import user11681.soulboundarmory.network.server.C2SAttribute;
import user11681.soulboundarmory.network.server.C2SBindSlot;
import user11681.soulboundarmory.network.server.C2SConfig;
import user11681.soulboundarmory.network.server.C2SEnchant;
import user11681.soulboundarmory.network.server.C2SItemType;
import user11681.soulboundarmory.network.server.C2SReset;
import user11681.soulboundarmory.network.server.C2SSkill;
import user11681.soulboundarmory.network.server.C2SSync;

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

    @SuppressWarnings("unchecked")
    private static <T, P extends Packet<T>> P server(P handler) {
        SoulboundArmory.channel.registerMessage(
            id++,
            (Class<T>) Object.class,
            handler::write,
            handler::read,
            (T buffer, Supplier<NetworkEvent.Context> context) -> context.get().enqueueWork(() -> handler.execute(buffer, context.get()))
        );

        return handler;
    }

    private static <T, P extends Packet<T>> P client(P handler) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return server(handler);
        }

        return handler;
    }
}
