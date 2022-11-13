package soulboundarmory.module.text;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.mixin.EmptyMixinPlugin;
import soulboundarmory.mixin.MixinUtil;

public class Transformer implements EmptyMixinPlugin {
	@Override
	public void preApply(String targetClassName, ClassNode target, String mixinClassName, IMixinInfo mixinInfo) {
		switch (mixinClassName) {
			case "soulboundarmory.module.text.mixin.StyleMixin" -> {
				var methodNames = new HashSet<>(List.of(MixinUtil.mapMethod(131152), MixinUtil.mapMethod(131157), MixinUtil.mapMethod(131164)));

				target.methods.stream().filter(method -> methodNames.contains(method.name)).forEach(method -> {
					for (var instruction : method.instructions) {
						if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
							var invocation = (MethodInsnNode) instruction;

							if (invocation.name.equals("ordinal")) {
								invocation.setOpcode(Opcodes.INVOKESTATIC);
								invocation.desc = "(L" + invocation.owner + ";)I";
								invocation.owner = target.name;
								invocation.name = "phormat_hackOrdinal";

								break;
							}
						}
					}
				});
			}
			case "soulboundarmory.module.text.mixin.dummy.LanguageDummyMixin" -> {
				var get = MixinUtil.mapMethod(6834);
				var method = target.methods.stream().filter(m -> m.name.equals(get)).findAny().get();
				var instructions = new InsnList();
				var end = new LabelNode();

				instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
				instructions.add(new LdcInsnNode("enchantment\\.level\\.\\d+"));
				instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "matches", "(Ljava/lang/String;)Z", false));
				instructions.add(new JumpInsnNode(Opcodes.IFEQ, end));
				instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
				instructions.add(new LdcInsnNode("\\D"));
				instructions.add(new LdcInsnNode(""));
				instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "replaceAll", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
				instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I"));
				instructions.add(new InsnNode(Opcodes.I2L));
				instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "soulboundarmory/module/text/RomanNumerals", "fromDecimal", "(J)Ljava/lang/String;"));
				instructions.add(new InsnNode(Opcodes.ARETURN));
				instructions.add(end);

				method.instructions.insertBefore(method.instructions.getFirst(), instructions);
			}
			case "soulboundarmory.module.text.mixin.dummy.ExtendedFormattingDummyMixin" -> {
				target.superName = MixinUtil.mapClass("net/minecraft/ChatFormatting");

				target.methods.forEach(method -> {
					switch (method.name) {
						case "<init>" -> {
							method.instructions.clear();

							var descriptor = Type.getMethodType(method.desc);
							var parameterTypes = descriptor.getArgumentTypes();
							var actualTypes = new Type[parameterTypes.length + 2];
							System.arraycopy(parameterTypes, 0, actualTypes, 2, parameterTypes.length);
							actualTypes[0] = actualTypes[2];
							actualTypes[1] = Type.INT_TYPE;

							method.visitVarInsn(Opcodes.ALOAD, 0);
							method.visitVarInsn(Opcodes.ALOAD, 1);
							method.visitFieldInsn(Opcodes.GETSTATIC, target.superName, MixinUtil.formattingValueField, "[L%s;".formatted(target.superName));
							method.visitInsn(Opcodes.ARRAYLENGTH);
							IntStream.range(1, parameterTypes.length + 1).forEach(index -> method.visitVarInsn(parameterTypes[index - 1].getOpcode(Opcodes.ILOAD), index));
							method.visitMethodInsn(Opcodes.INVOKESPECIAL, target.superName, method.name, Type.getMethodDescriptor(Type.VOID_TYPE, actualTypes), false);
							method.visitInsn(Opcodes.RETURN);
						}
						case "super$getColor" -> {
							method.access &= ~Opcodes.ACC_NATIVE;

							method.visitVarInsn(Opcodes.ALOAD, 0);
							method.visitMethodInsn(Opcodes.INVOKESPECIAL, target.superName, MixinUtil.mapMethod(126665), "()Ljava/lang/Integer;", false);
							method.visitInsn(Opcodes.ARETURN);
						}
						case "code" -> {
							method.access &= ~Opcodes.ACC_NATIVE;

							method.visitVarInsn(Opcodes.ALOAD, 0);
							method.visitMethodInsn(Opcodes.INVOKESPECIAL, target.superName, MixinUtil.mapMethod(178510), "()C", false);
							method.visitInsn(Opcodes.IRETURN);
						}
					}
				});
			}
		}
	}
}
