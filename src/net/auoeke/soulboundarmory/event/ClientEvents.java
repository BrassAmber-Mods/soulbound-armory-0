package net.auoeke.soulboundarmory.event;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.auoeke.soulboundarmory.SoulboundArmory;
import net.auoeke.soulboundarmory.SoulboundArmoryClient;
import net.auoeke.soulboundarmory.capability.Capabilities;
import net.auoeke.soulboundarmory.client.gui.bar.ExperienceBarOverlay;
import net.auoeke.soulboundarmory.config.Configuration;
import net.auoeke.soulboundarmory.text.Translation;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.profiler.Profiler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.auoeke.cell.client.gui.CellElement;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.weapon.StaffStorage;
import net.auoeke.soulboundarmory.item.SoulboundItem;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = SoulboundArmory.ID)
public class ClientEvents {
    public static ExperienceBarOverlay overlayBar;
    public static ExperienceBarOverlay tooltipBar;

    @SubscribeEvent
    public static void scroll(InputEvent.MouseScrollEvent event) {
        if (Screen.hasAltDown()) {
            PlayerEntity player = SoulboundArmoryClient.client.player;

            if (player != null && player.world != null) {
                ItemStorage<?> storage = Capabilities.weapon.get(player).heldItemStorage();

                if (storage instanceof StaffStorage) {
                    int dY = (int) event.getMouseY();

                    if (dY != 0) {
                        StaffStorage staffStorage = (StaffStorage) storage;

                        staffStorage.cycleSpells(-dY);
                        SoulboundArmoryClient.client.inGameHud.setOverlayMessage(new Translation("§4§l%s", staffStorage.spell()), false);

                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE && Configuration.instance().client.overlayExperienceBar && overlayBar.render(event.getMatrixStack(), event.getWindow())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        PlayerEntity player = event.getPlayer();

        if (player != null) {
            ItemStack itemStack = event.getItemStack();
            Item item = itemStack.getItem();
            ItemStorage<?> storage = ItemStorage.get(player, item);

            if (storage != null) {
                List<Text> tooltip = event.getToolTip();
                int startIndex = 1;

                for (int index = 0, size = tooltip.size(); index < size; index++) {
                    Text entry = tooltip.get(index);

                    if (entry instanceof Translation && ((Translation) entry).getKey().equals("item.modifiers.mainhand")) {
                        startIndex += index;
                    }
                }

                int toIndex = tooltip.size();
//                int fromIndex = Math.min(toIndex - 1, startIndex + ((SoulboundItem) item).getMainhandAttributeEntries(itemStack, player));

                List<Text> prior = new ArrayList<>(tooltip).subList(0, startIndex);
                List<Text> insertion = storage.tooltip();
//                List<Text> posterior = new ArrayList<>(tooltip).subList(fromIndex, toIndex);

                tooltip.clear();
                tooltip.addAll(prior);
                tooltip.addAll(insertion);
//                tooltip.addAll(posterior);

                int row = insertion.lastIndexOf(LiteralText.EMPTY) + prior.size();

                tooltipBar.data(row, CellElement.textRenderer.getWidth(tooltip.get(row - 2)) - 4);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderTooltip(RenderTooltipEvent.PostBackground event) {
        if (event.getStack().getItem() instanceof SoulboundItem) {
            tooltipBar.drawTooltip(event.getMatrixStack(), event.getX(), event.getY(), event.getStack());
        }
    }

    @SubscribeEvent
    public static void onLoadResources(AddReloadListenerEvent event) {
        event.addListener(new ExperienceBarReloader());
    }

    private static class ExperienceBarReloader extends SinglePreparationResourceReloader<Void> {
        @Override
        protected Void prepare(ResourceManager manager, Profiler profiler) {
            return null;
        }

        @Override
        protected void apply(Void nothing, ResourceManager manager, Profiler profiler) {
            RenderSystem.recordRenderCall(() -> {
                overlayBar = new ExperienceBarOverlay();
                tooltipBar = new ExperienceBarOverlay();

                overlayBar.width(182).height(5).center(true);
            });
        }
    }
}
