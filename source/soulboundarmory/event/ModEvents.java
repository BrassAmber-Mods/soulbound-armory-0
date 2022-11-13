package soulboundarmory.event;

import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.command.argument.ItemComponentArgumentType;
import soulboundarmory.command.argument.RegistryArgumentType;
import soulboundarmory.component.Components;
import soulboundarmory.util.Util2;

@EventBusSubscriber(modid = SoulboundArmory.ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModEvents {
	@SubscribeEvent
	public static void register(RegisterEvent event) {
		event.register(Registry.COMMAND_ARGUMENT_TYPE_KEY, helper -> {
			registerArgumentType(helper, "item_component", ItemComponentArgumentType.class, ConstantArgumentSerializer.of(ItemComponentArgumentType::itemComponents));
			registerArgumentType(helper, "registry", RegistryArgumentType.class, new RegistryArgumentType.Serializer());
		});
	}

	@SubscribeEvent
	public static void registerRegistries(NewRegistryEvent event) {
		Unsafe.ensureClassInitialized(Components.class);
	}

	private static void registerArgumentType(RegisterEvent.RegisterHelper<ArgumentSerializer<?, ?>> helper, String name, Class<?> type, ArgumentSerializer<?, ?> serializer) {
		ArgumentTypes.registerByClass(Util2.cast(type), serializer);
		helper.register(name, serializer);
	}
}
