package soulboundarmory.event;

import java.util.stream.IntStream;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.text.TranslatableTextContent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.component.soulbound.item.ItemComponent;

@EventBusSubscriber(value = Dist.CLIENT, modid = SoulboundArmory.ID)
public final class ClientEvents {
	@SubscribeEvent public static void tooltip(ItemTooltipEvent event) {
		ItemComponent.of(event.getEntity(), event.getItemStack()).ifPresent(component -> {
			var tooltip = event.getToolTip();
			var startIndex = 1 + IntStream.range(0, tooltip.size())
				.filter(index -> tooltip.get(index).getContent() instanceof TranslatableTextContent translation && translation.getKey().equals("item.modifiers.mainhand"))
				.sum();

			var prior = new ReferenceArrayList<>(tooltip).subList(0, startIndex);

			tooltip.clear();
			tooltip.addAll(prior);
			tooltip.addAll(component.tooltip());
		});
	}
}
