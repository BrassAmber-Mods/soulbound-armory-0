package user11681.soulboundarmory.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.network.client.ClientPacket;
import user11681.soulboundarmory.network.server.ServerPacket;
import user11681.soulboundarmory.network.server.C2SAttribute;
import user11681.soulboundarmory.network.server.C2SBindSlot;
import user11681.soulboundarmory.network.server.C2SConfig;
import user11681.soulboundarmory.network.server.C2SEnchant;
import user11681.soulboundarmory.network.server.C2SItemType;
import user11681.soulboundarmory.network.server.C2SReset;
import user11681.soulboundarmory.network.server.C2SSkill;
import user11681.soulboundarmory.network.server.C2SSync;
import user11681.soulboundarmory.network.client.S2CEnchant;
import user11681.soulboundarmory.network.client.S2CItemType;
import user11681.soulboundarmory.network.client.S2COpenGUI;
import user11681.soulboundarmory.network.client.S2CRefresh;
import user11681.soulboundarmory.network.client.S2CSync;

import static user11681.soulboundarmory.SoulboundArmory.id;

public class Packets {
    public static final Identifier serverAttribute = id("server_attribute");
    public static final Identifier serverBindSlot = id("server_bind_slot");
    public static final Identifier serverConfig = id("server_config");
    public static final Identifier serverEnchant = id("server_enchant");
    public static final Identifier serverItemType = id("server_item_type");
    public static final Identifier serverReset = id("server_reset");
    public static final Identifier serverSkill = id("server_skill");
    public static final Identifier serverSync = id("server_sync");

    public static final Identifier clientEnchant = id("client_enchant");
    public static final Identifier clientItemType = id("client_item_type");
    public static final Identifier clientOpenGUI = id("client_open_gui");
    public static final Identifier clientRefresh = id("client_refresh");
    public static final Identifier clientSync = id("client_sync");

    public static void register() {
        register(serverAttribute, new C2SAttribute());
        register(serverBindSlot, new C2SBindSlot());
        register(serverConfig, new C2SConfig());
        register(serverEnchant, new C2SEnchant());
        register(serverItemType, new C2SItemType());
        register(serverReset, new C2SReset());
        register(serverSkill, new C2SSkill());
        register(serverSync, new C2SSync());

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            register(clientEnchant, new S2CEnchant());
            register(clientItemType, new S2CItemType());
            register(clientOpenGUI, new S2COpenGUI());
            register(clientRefresh, new S2CRefresh());
            register(clientSync, new S2CSync());
        }
    }

    private static void register(Identifier identifier, ServerPacket handler) {
        ServerPlayNetworking.registerGlobalReceiver(identifier, handler);
    }

    @Environment(EnvType.CLIENT)
    private static void register(Identifier identifier, ClientPacket handler) {
        ClientPlayNetworking.registerGlobalReceiver(identifier, handler);
    }
}
