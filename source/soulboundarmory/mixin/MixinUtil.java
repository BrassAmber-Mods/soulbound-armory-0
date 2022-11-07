package soulboundarmory.mixin;

import java.util.function.BiFunction;
import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;

public class MixinUtil {
	public static final String formattingValueField = map("field_1072", "$VALUES");
	private static BiFunction<INameMappingService.Domain, String, String> mapper;

	public static String map(String development, String production) {
		return FMLEnvironment.production ? production : development;
	}

	public static String mapClass(String production) {
		return map(INameMappingService.Domain.CLASS, production);
	}

	public static String mapMethod(int production) {
		return map(INameMappingService.Domain.METHOD, "m_%d_".formatted(production));
	}

	public static String mapField(int production) {
		return map(INameMappingService.Domain.FIELD, "f_%d_".formatted(production));
	}

	private static String map(INameMappingService.Domain domain, String production) {
		if (FMLEnvironment.production) {
			return production;
		}

		if (mapper == null) {
			mapper = FMLLoader.getNameFunction("srg").get();
		}

		return mapper.apply(domain, production);
	}
}
