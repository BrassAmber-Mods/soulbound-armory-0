package soulboundarmory;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import soulboundarmory.util.Util;

public class Transformer implements ITransformer<ClassNode>, ITransformationService, IMixinConfigPlugin {
    @Nonnull
    @Override
    public String name() {
        return SoulboundArmory.ID;
    }

    @Override
    public void initialize(IEnvironment environment) {}

    @Override
    public void beginScanning(IEnvironment environment) {}

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {}

    @SuppressWarnings("rawtypes")
    @Nonnull
    @Override
    public List<ITransformer> transformers() {
        return List.of(this);
    }

    @Nonnull
    @Override
    public ClassNode transform(ClassNode type, ITransformerVotingContext context) {
        switch (type.name) {
            case "net/minecraft/util/text/TextFormatting" -> {
                type.access &= ~Opcodes.ACC_FINAL;

                type.fields.forEach(field -> {
                    switch (field.name) {
                        case "$VALUES", "code" -> field.access = field.access & ~Opcodes.ACC_PRIVATE | Opcodes.ACC_PUBLIC;
                    }
                });

                type.methods.stream().filter(method -> method.name.equals("<init>")).forEach(method -> method.access = method.access & ~Opcodes.ACC_PRIVATE | Opcodes.ACC_PUBLIC);
            }
            case "soulboundarmory/text/format/ExtendedFormatting" -> {
                type.superName = "net/minecraft/util/text/TextFormatting";

                type.methods.forEach(method -> {
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
                            method.visitFieldInsn(Opcodes.GETSTATIC, type.superName, "$VALUES", "[Lnet/minecraft/util/text/TextFormatting;");
                            method.visitInsn(Opcodes.ARRAYLENGTH);
                            IntStream.range(1, parameterTypes.length + 1).forEach(index -> method.visitVarInsn(parameterTypes[index - 1].getOpcode(Opcodes.ILOAD), index));
                            method.visitMethodInsn(Opcodes.INVOKESPECIAL, type.superName, method.name, Type.getMethodDescriptor(Type.VOID_TYPE, actualTypes), false);
                            method.visitInsn(Opcodes.RETURN);
                        }
                        case "super$getColor" -> {
                            method.access &= ~Opcodes.ACC_NATIVE;

                            method.visitVarInsn(Opcodes.ALOAD, 0);
                            method.visitMethodInsn(Opcodes.INVOKESPECIAL, type.superName, "getColor", "()Ljava/lang/Integer;", false);
                            method.visitInsn(Opcodes.ARETURN);
                        }
                        case "code" -> {
                            method.access &= ~Opcodes.ACC_NATIVE;

                            method.visitVarInsn(Opcodes.ALOAD, 0);
                            method.visitFieldInsn(Opcodes.GETFIELD, type.superName, "getColor", "C");
                            method.visitInsn(Opcodes.IRETURN);
                        }
                    }
                });
            }
        }

        return type;
    }

    @Nonnull
    @Override
    public TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Nonnull
    @Override
    public Set<Target> targets() {
        return Set.of(Target.targetClass("net.minecraft.util.text.TextFormatting"), Target.targetClass("soulboundarmory.text.format.ExtendedFormatting"));
    }

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        switch (mixinClassName) {
            case "soulboundarmory.text.format.asm.mixin.StyleMixin" -> {
                var methodNames = Set.of(Util.mapMethod("func_240720_a_"), Util.mapMethod("func_240721_b_"), Util.mapMethod("func_240723_c_"));

                for (var method : targetClass.methods) {
                    var name = method.name;

                    if (methodNames.contains(name)) {
                        var instruction = method.instructions.getFirst();

                        while (instruction != null) {
                            if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                var methodInstruction = (MethodInsnNode) instruction;

                                if (methodInstruction.name.equals("ordinal")) {
                                    method.instructions.set(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, targetClassName.replace('.', '/'), "phormat_hackOrdinal", "(L" + methodInstruction.owner + ";)I"));

                                    break;
                                }
                            }

                            instruction = instruction.getNext();
                        }
                    }
                }
            }
            case "soulboundarmory.text.format.asm.mixin.ColorMixin" -> {
                var getValue = Util.mapMethod("func_240746_a_");

                for (var method : targetClass.methods) {
                    if (getValue.equals(method.name)) {
                        var internalName = targetClassName.replace('.', '/');
                        var value = Util.mapField("field_240740_c_");

                        var insertion = new InsnList();
                        var endIf = new LabelNode();

                        /*if (this.phormat_hasColorFunction) {
                            return this.phormat_previousColor = this.phormat_colorFunction.apply(this.phormat_previousColor);
                        }*/

                        insertion.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                        insertion.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this this
                        insertion.add(new FieldInsnNode(Opcodes.GETFIELD, internalName, "phormat_hasColorFunction", "Z")); // this boolean
                        insertion.add(new JumpInsnNode(Opcodes.IFEQ, endIf)); // this
                        insertion.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this this
                        insertion.add(new FieldInsnNode(Opcodes.GETFIELD, internalName, "phormat_colorFunction", "Lsoulboundarmory/text/format/ColorFunction;")); // this ColorFunction
                        insertion.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this ColorFunction this
                        insertion.add(new FieldInsnNode(Opcodes.GETFIELD, internalName, "phormat_previousColor", "I")); // this ColorFunction int
                        insertion.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "soulboundarmory/text/format/ColorFunction", "apply", "(I)I", true)); // this int
                        insertion.add(new InsnNode(Opcodes.DUP_X1)); // int this int
                        insertion.add(new FieldInsnNode(Opcodes.PUTFIELD, internalName, "phormat_previousColor", "I")); // int
                        insertion.add(new InsnNode(Opcodes.IRETURN));
                        insertion.add(endIf);

                        method.instructions.insertBefore(method.instructions.getFirst(), insertion);

                        break;
                    }
                }
            }
            case "soulboundarmory.mixin.mixin.roman.LanguageDummyMixin" -> {
                for (var method : targetClass.methods) {
                    if (method.name.equals(Util.mapMethod("func_230503_a_"))) {
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
                        instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "soulboundarmory/text/RomanNumerals", "fromDecimal", "(J)Ljava/lang/String;"));
                        instructions.add(new InsnNode(Opcodes.ARETURN));
                        instructions.add(end);

                        method.instructions.insertBefore(method.instructions.getFirst(), instructions);

                        break;
                    }
                }
            }
        }
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

}
