package soulboundarmory.component.soulbound.item;

import cell.client.gui.CellElement;
import cell.client.gui.widget.Widget;
import java.lang.invoke.MethodHandle;
import net.auoeke.reflect.Classes;
import net.auoeke.reflect.Invoker;
import net.auoeke.reflect.Pointer;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.lib.component.ItemStackComponent;
import soulboundarmory.util.Util;

public class ItemMarkerComponent implements ItemStackComponent<ItemMarkerComponent>, TooltipData {
    @OnlyIn(Dist.CLIENT) private static Pointer frameTicks;
    @OnlyIn(Dist.CLIENT) private static MethodHandle upload;

    public final ItemStack stack;
    public ItemComponent<?> item;

    private int animationTick = 40;

    public ItemMarkerComponent(ItemStack stack) {
        this.stack = stack;

        if (Util.isClient()) {
            this.item = ItemComponent.get(CellElement.client.player, stack).orElse(null);
        }
    }

    public ItemComponent<?> item() {
        return this.item;
    }

    public boolean animating() {
        return this.animationTick < 30;
    }

    public void unlock() {
        this.animationTick = 0;
        this.upload();
    }

    @Override
    public void tickStart() {
        if (Util.isClient() && this.animating() && this.animationTick++ % 2 == 0) {
            this.upload();
        }
    }

    @Override
    public void serialize(NbtCompound tag) {
        if (this.item != null) {
            tag.putUuid("player", this.item.player.getUuid());
            tag.putString("item", this.item.type().string());
        }
    }

    @Override
    public void deserialize(NbtCompound tag) {
        if (!Util.isClient()) {
            this.item = ItemComponentType.get(tag.getString("item")).of(Util.server().getPlayerManager().getPlayer(tag.getUuid("player")));
        }
    }

    private TextureTickListener animation() {
        var player = this.item.player;
        return Widget.itemRenderer.getModel(this.stack, player.world, player, player.getId()).getParticleSprite().getAnimation();
    }

    private void upload() {
        var animation = this.animation();
        frameTicks.putInt(animation, Integer.MIN_VALUE);
        Widget.bind(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        Invoker.invoke(upload, animation, Math.max(0, this.animationTick - 5) / 2);
    }

    static {
        if (Util.isPhysicalClient) {
            var type = Classes.load(Sprite.class.getName() + "$Animation");
            frameTicks = new Pointer().instanceField(type, "frameTicks");
            upload = Invoker.findVirtual(type, "upload", void.class, int.class);
        }
    }
}
