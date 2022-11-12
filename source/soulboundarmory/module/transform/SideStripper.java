package soulboundarmory.module.transform;

import net.auoeke.reflect.Classes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import soulboundarmory.config.Configuration;
import soulboundarmory.util.Util;

/**
 This class adds a transformer that strips inner classes from the wrong environment in order to prevent errors arising from {@link Class#getDeclaredClasses}.

 @see Configuration.Client
 */
@EventBusSubscriber
public class SideStripper {
	private static boolean transform(ClassNode type) {
		var innerClasses = type.innerClasses.listIterator();
		var transformed = false;

		for (var innerClass : Util.iterate(innerClasses)) {
			var node = new ClassNode();
			var classFile = Classes.classFile(SideStripper.class.getClassLoader(), innerClass.name);

			if (classFile != null) {
				new ClassReader(classFile).accept(node, ClassReader.SKIP_CODE);

				if (node.visibleAnnotations != null) {
					var onlyIn = node.visibleAnnotations.stream().filter(annotation -> annotation.desc.equals(OnlyIn.class.descriptorString())).findAny();

					if (onlyIn.isPresent()) {
						var values = onlyIn.get().values.listIterator();

						for (var value : Util.iterate(values)) {
							if (value instanceof String name && name.equals("value") && values.next() instanceof String[] array && array[0].equals(Dist.class.descriptorString()) && !array[1].equals(FMLEnvironment.dist.name())) {
								transformed = true;
								innerClasses.remove();

								break;
							}
						}
					}
				}
			}
		}

		return transformed;
	}

	static {
		TransformerManager.addTransformer(SideStripper::transform);
	}
}
