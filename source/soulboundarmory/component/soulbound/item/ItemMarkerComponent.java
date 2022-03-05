package soulboundarmory.component.soulbound.item;

import soulboundarmory.lib.gui.CellElement;
import soulboundarmory.lib.gui.widget.Widget;
import java.util.Optional;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PlayerScreenHandler;
import soulboundarmory.lib.component.ItemStackComponent;
import soulboundarmory.util.Util;

public class ItemMarkerComponent implements ItemStackComponent<ItemMarkerComponent>, TooltipData {
    public final ItemStack stack;
    public ItemComponent<?> item;

    private int animationTick = 40;

    public ItemMarkerComponent(ItemStack stack) {
        this.stack = stack;

        if (Util.isClient()) {
            this.item = ItemComponent.of(CellElement.client.player, stack).orElse(null);
        }
    }

    public Optional<ItemComponent<?>> item() {
        return Optional.ofNullable(this.item);
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
        this.item().ifPresent(item -> {
            tag.putUuid("player", item.player.getUuid());
            tag.putString("item", item.type().string());
        });
    }

    @Override
    public void deserialize(NbtCompound tag) {
        if (!Util.isClient()) {
            this.item = ItemComponentType.get(tag.getString("item")).of(Util.server().getPlayerManager().getPlayer(tag.getUuid("player")));
        }
    }

    private void upload() {
        var player = this.item.player;
        var animation = (Sprite.Animation) Widget.itemRenderer.getModel(this.stack, player.world, player, player.getId()).getParticleSprite().getAnimation();
        animation.frameTicks = Integer.MIN_VALUE;
        Widget.bind(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        animation.upload(Math.max(0, this.animationTick - 5) / 2);
    }
}
