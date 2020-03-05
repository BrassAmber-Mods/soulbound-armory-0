package transfarmer.soulboundarmory.network.server.tool;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.network.client.tool.CToolResetEnchantments;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

import static transfarmer.soulboundarmory.statistics.SoulDatum.DATA;


public class SToolResetEnchantments implements IMessage {
    private int index;

    public SToolResetEnchantments() {
    }

    public SToolResetEnchantments(final SoulType type) {
        this.index = type.getIndex();
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.index = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.index);
    }

    public static final class Handler implements IMessageHandler<SToolResetEnchantments, IMessage> {
        @Override
        public IMessage onMessage(final SToolResetEnchantments message, final MessageContext context) {
            final ISoulCapability capability = SoulToolProvider.get(context.getServerHandler().player);
            final SoulType type = SoulToolType.get(message.index);

            capability.addDatum(capability.getDatum(DATA.spentEnchantmentPoints, type), DATA.enchantmentPoints, type);
            capability.setDatum(0, DATA.spentEnchantmentPoints, type);
            capability.setEnchantments(new int[capability.getEnchantmentAmount()], type);

            return new CToolResetEnchantments(type);
        }
    }
}
