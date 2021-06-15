package user11681.soulboundarmory.event;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.client.gui.ExperienceBarOverlay;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.text.Translation;

import static user11681.soulboundarmory.SoulboundArmoryClient.client;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = SoulboundArmory.ID)
public class ClientEvents {
    public static ExperienceBarOverlay overlayBar;
    public static ExperienceBarOverlay tooltipBar;

    @SubscribeEvent
    public static void scroll(InputEvent.MouseScrollEvent event) {
        if (Screen.hasAltDown()) {
            PlayerEntity player = client.player;

            if (player != null && player.level != null) {
                ItemStorage<?> storage = Capabilities.weapon.get(player).heldItemStorage();

                if (storage instanceof StaffStorage) {
                    int dY = (int) event.getMouseY();

                    if (dY != 0) {
                        StaffStorage staffStorage = (StaffStorage) storage;

                        staffStorage.cycleSpells(-dY);
                        client.gui.setOverlayMessage(new Translation("§4§l%s", staffStorage.spell()), false);

                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (Configuration.instance().client.overlayExperienceBar && overlayBar.render()) {
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
                List<ITextComponent> tooltip = event.getToolTip();
                int startIndex = 1;

                for (int index = 0, size = tooltip.size(); index < size; index++) {
                    ITextComponent entry = tooltip.get(index);

                    if (entry instanceof Translation && ((Translation) entry).getKey().equals("item.modifiers.mainhand")) {
                        startIndex += index;
                    }
                }

                int toIndex = tooltip.size();
//                int fromIndex = Math.min(toIndex - 1, startIndex + ((SoulboundItem) item).getMainhandAttributeEntries(itemStack, player));

                List<ITextComponent> prior = new ArrayList<>(tooltip).subList(0, startIndex);
                List<ITextComponent> insertion = storage.getTooltip();
//                List<ITextComponent> posterior = new ArrayList<>(tooltip).subList(fromIndex, toIndex);

                tooltip.clear();
                tooltip.addAll(prior);
                tooltip.addAll(insertion);
//                tooltip.addAll(posterior);

                int row = insertion.lastIndexOf(StringTextComponent.EMPTY) + prior.size();

                tooltipBar.setData(row, client.font.width(tooltip.get(row - 2)) - 4);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderTooltip(RenderTooltipEvent.PostBackground event) {
        if (event.getStack().getItem() instanceof SoulboundItem) {
            tooltipBar.drawTooltip(event.getX(), event.getY(), event.getStack());
        }
    }

    @SubscribeEvent
    public static void onLoadResources(AddReloadListenerEvent event) {
        RenderSystem.recordRenderCall(() -> {
            overlayBar = new ExperienceBarOverlay();
            tooltipBar = new ExperienceBarOverlay();
        });
    }
}
