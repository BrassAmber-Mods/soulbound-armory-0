package soulboundarmory.component.soulbound.item;

import java.util.Optional;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.client.gui.bar.ExperienceBar;
import soulboundarmory.lib.component.ItemStackComponent;
import soulboundarmory.lib.gui.AbstractNode;
import soulboundarmory.lib.gui.Node;
import soulboundarmory.util.Util;

public class ItemMarkerComponent implements ItemStackComponent<ItemMarkerComponent>, TooltipData {
    public final ItemStack stack;

    private ItemComponent<?> item;
    private int animationTick = 40;

    public ItemMarkerComponent(ItemStack stack) {
        this.stack = stack;

        if (Util.isClient()) {
            this.initializeItem();
        }
    }

    public Optional<ItemComponent<?>> optionalItem() {
        return Optional.ofNullable(this.item());
    }

    public ItemComponent<?> item() {
        if (this.item == null && Util.isClient()) {
            return this.initializeItem();
        }

        return this.item;
    }

    public void item(ItemComponent<?> item) {
        this.item = item;
    }

    public boolean animating() {
        return this.animationTick < 30;
    }

    public void unlock() {
        this.animationTick = 0;
        this.upload();
    }

    @OnlyIn(Dist.CLIENT)
    public ExperienceBar tooltip() {
        return new ExperienceBar().item(this.item()).width(AbstractNode.width(this.stack.getName()));
    }

    @Override
    public void tickStart() {
        if (Util.isClient() && this.animating() && this.animationTick++ % 2 == 0) {
            this.upload();
        }
    }

    @Override
    public void serialize(NbtCompound tag) {
        this.optionalItem().ifPresent(item -> {
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

    @OnlyIn(Dist.CLIENT)
    private ItemComponent<?> initializeItem() {
        return this.item = ItemComponent.of(AbstractNode.player(), this.stack).orElse(null);
    }

    @OnlyIn(Dist.CLIENT)
    private void upload() {
        var player = this.item.player;
        var animation = (Sprite.Animation) Node.itemRenderer.getModel(this.stack, player.world, player, player.getId()).getParticleSprite().getAnimation();
        animation.frameTicks = Integer.MIN_VALUE;
        AbstractNode.bind(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        animation.upload(Math.max(0, this.animationTick - 5) / 2);
    }
}
