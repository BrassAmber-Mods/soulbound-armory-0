/*
 Copyright 2022 gudenau <https://github.com/gudenau>
 "Do whatever."
*/

package soulboundarmory.module.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

//TODO Remapping
@SuppressWarnings("Contract")
public final class Assembler {
	public record Output(
		InsnList instructions,
		List<LocalVariableNode> locals,
		int maxStack, int maxLocal
	) {}

	public static void main(String[] args) {
		assemble("""
			getstatic java/lang/System out Ljava/io/PrintStream;
			new java/lang/Object
			dup
			invokespecial java/lang/Object <init> ()V
			invokevirtual java/lang/Object toString ()Ljava/lang/String;
			invokevirtual java/io/PrintStream println (Ljava/lang/String;)V
			""");
	}

	@Contract("_ -> new")
	public static Output assemble(@NotNull String source) {
		var lines = source.lines()
			.map(line -> {
				var index = line.indexOf('#');
				return index == -1 ? line : line.substring(0, index);
			})
			.filter(Predicate.not(String::isBlank))
			.map(String::trim)
			.toList();

		var instructions = new InsnList();
		var labels = new HashMap<String, LabelNode>();
		var locals = new ArrayList<LocalVariableNode>();
		var maxLocal = -1;
		var maxStack = -1;

		for (var line : lines) {
			var labelIndex = line.indexOf(':');

			if (labelIndex != -1) {
				var name = line.substring(0, labelIndex).trim();
				line = line.substring(labelIndex + 1).trim();
				var label = labels.get(name);

				if (label == null || instructions.contains(label)) {
					label = new LabelNode(new Label());
					labels.put(name, label);
				}

				instructions.add(label);

				if (line.isBlank()) {
					continue;
				}
			}

			var instruction = split(line);
			// Non balnk, we know this is valid.
			switch (instruction[0].toLowerCase()) {
				case ".local" -> {
					if (instruction.length != 7) {
						throw new IllegalArgumentException(".local takes an identifier, type descriptor, signature, label, label and an integer");
					}
					try {
						locals.add(new LocalVariableNode(instruction[1], parseObjectDes(instruction[2]), parseSignature(instruction[3]), parseLabel(instruction[4], labels), parseLabel(instruction[5], labels), parseInt(instruction[6])));
					} catch (Throwable e) {
						throw new IllegalArgumentException(".local takes an identifier, type descriptor, signature, label, label and an integer", e);
					}
				}
				case ".maxlocal" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException(".maxlocal takes an integer");
					}
					try {
						maxLocal = parseInt(instruction[1]);
					} catch (Throwable e) {
						throw new IllegalArgumentException(".maxlocal takes an integer", e);
					}
				}
				case ".maxstack" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException(".maxstack takes an integer");
					}
					try {
						maxStack = parseInt(instruction[1]);
					} catch (Throwable e) {
						throw new IllegalArgumentException(".maxstack takes an integer", e);
					}
				}
				//TODO .frame

				case "nop" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("nop does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.NOP));
				}
				case "aconst_null" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("aconst_null does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.ACONST_NULL));
				}
				case "iconst_m1" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("iconst_m1 does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.ICONST_M1));
				}
				case "iconst_0" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("iconst_0 does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.ICONST_0));
				}
				case "iconst_1" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("iconst_1 does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.ICONST_1));
				}
				case "iconst_2" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("iconst_2 does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.ICONST_2));
				}
				case "iconst_3" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("iconst_3 does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.ICONST_3));
				}
				case "iconst_4" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("iconst_4 does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.ICONST_4));
				}
				case "iconst_5" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("iconst_5 does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.ICONST_5));
				}
				case "lconst_0" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("lconst_0 does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.LCONST_0));
				}
				case "lconst_1" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("lconst_1 does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.LCONST_1));
				}
				case "fconst_0" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("fconst_0 does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.FCONST_0));
				}
				case "fconst_1" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("fconst_1 does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.FCONST_1));
				}
				case "fconst_2" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("fconst_2 does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.FCONST_2));
				}
				case "dconst_0" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("dconst_0 does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.DCONST_0));
				}
				case "dconst_1" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("dconst_1 does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.DCONST_1));
				}

				case "bipush" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("bipush takes a single byte argument");
					}
					try {
						instructions.add(new IntInsnNode(Opcodes.BIPUSH, parseByte(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("bipush takes a single byte argument", e);
					}
				}
				case "sipush" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("sipush takes a single byte argument");
					}
					try {
						instructions.add(new IntInsnNode(Opcodes.SIPUSH, parseShort(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("sipush takes a single byte argument", e);
					}
				}

				// ldc is special
				case "ldc" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("ldc takes a single argument");
					}
					try {
						instructions.add(new LdcInsnNode(parseLdc(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("ldc constant invalid: " + instruction[1]);
					}
				}

				case "iload" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("iload takes a single integer argument");
					}
					try {
						instructions.add(new VarInsnNode(Opcodes.ILOAD, parseInt(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("iload takes a single integer argument");
					}
				}
				case "lload" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("lload takes a single integer argument");
					}
					try {
						instructions.add(new VarInsnNode(Opcodes.LLOAD, parseInt(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("lload takes a single integer argument");
					}
				}
				case "fload" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("fload takes a single integer argument");
					}
					try {
						instructions.add(new VarInsnNode(Opcodes.FLOAD, parseInt(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("fload takes a single integer argument");
					}
				}
				case "dload" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("dload takes a single integer argument");
					}
					try {
						instructions.add(new VarInsnNode(Opcodes.DLOAD, parseInt(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("dload takes a single integer argument");
					}
				}
				case "aload" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("aload takes a single integer argument");
					}
					try {
						instructions.add(new VarInsnNode(Opcodes.ALOAD, parseInt(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("aload takes a single integer argument");
					}
				}

				case "iaload" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("iaload does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.IALOAD));
				}
				case "laload" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("laload does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.LALOAD));
				}
				case "faload" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("faload does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.FALOAD));
				}
				case "daload" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("daload does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.DALOAD));
				}
				case "aaload" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("aaload does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.AALOAD));
				}
				case "baload" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("baload does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.BALOAD));
				}
				case "caload" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("caload does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.CALOAD));
				}
				case "saload" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("saload does not have arguments");
					}
					instructions.add(new InsnNode(Opcodes.SALOAD));
				}

				case "istore" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("istore takes a single int argument");
					}
					try {
						instructions.add(new VarInsnNode(Opcodes.ISTORE, parseInt(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("istore takes a single int argument", e);
					}
				}
				case "lstore" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("lstore takes a single int argument");
					}
					try {
						instructions.add(new VarInsnNode(Opcodes.LSTORE, parseInt(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("lstore takes a single int argument", e);
					}
				}
				case "fstore" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("fstore takes a single int argument");
					}
					try {
						instructions.add(new VarInsnNode(Opcodes.FSTORE, parseInt(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("fstore takes a single int argument", e);
					}
				}
				case "dstore" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("dstore takes a single int argument");
					}
					try {
						instructions.add(new VarInsnNode(Opcodes.DSTORE, parseInt(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("dstore takes a single int argument", e);
					}
				}
				case "astore" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("astore takes a single int argument");
					}
					try {
						instructions.add(new VarInsnNode(Opcodes.ASTORE, parseInt(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("astore takes a single int argument", e);
					}
				}

				case "iastore" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("iastore does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.IASTORE));
				}
				case "lastore" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("lastore does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LASTORE));
				}
				case "fastore" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("fastore does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.FASTORE));
				}
				case "dastore" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("dastore does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DASTORE));
				}
				case "aastore" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("aastore does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.AASTORE));
				}
				case "bastore" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("bastore does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.BASTORE));
				}
				case "castore" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("castore does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.CASTORE));
				}
				case "sastore" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("sastore does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.SASTORE));
				}
				case "pop" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("pop does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.POP));
				}
				case "pop2" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("pop2 does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.POP2));
				}
				case "dup" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("dup does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DUP));
				}
				case "dup_x1" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("dup_x1 does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DUP_X1));
				}
				case "dup_x2" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("dup_x2 does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DUP_X2));
				}
				case "dup2" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("dup2 does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DUP2));
				}
				case "dup2_x1" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("dup2_x1 does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DUP2_X1));
				}
				case "dup2_x2" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("dup2_x2 does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DUP2_X2));
				}
				case "swap" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("swap does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.SWAP));
				}
				case "iadd" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("iadd does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.IADD));
				}
				case "ladd" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("ladd does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LADD));
				}
				case "fadd" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("fadd does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.FADD));
				}
				case "dadd" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("dadd does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DADD));
				}
				case "isub" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("isub does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.ISUB));
				}
				case "lsub" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("lsub does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LSUB));
				}
				case "fsub" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("fsub does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.FSUB));
				}
				case "dsub" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("dsub does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DSUB));
				}
				case "imul" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("imul does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.IMUL));
				}
				case "lmul" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("lmul does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LMUL));
				}
				case "fmul" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("fmul does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.FMUL));
				}
				case "dmul" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("dmul does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DMUL));
				}
				case "idiv" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("idiv does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.IDIV));
				}
				case "ldiv" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("ldiv does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LDIV));
				}
				case "fdiv" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("fdiv does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.FDIV));
				}
				case "ddiv" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("ddiv does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DDIV));
				}
				case "irem" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("irem does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.IREM));
				}
				case "lrem" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("lrem does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LREM));
				}
				case "frem" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("frem does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.FREM));
				}
				case "drem" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("drem does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DREM));
				}
				case "ineg" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("ineg does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.INEG));
				}
				case "lneg" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("lneg does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LNEG));
				}
				case "fneg" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("fneg does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.FNEG));
				}
				case "dneg" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("dneg does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DNEG));
				}
				case "ishl" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("ishl does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.ISHL));
				}
				case "lshl" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("lshl does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LSHL));
				}
				case "ishr" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("ishr does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.ISHR));
				}
				case "lshr" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("lshr does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LSHR));
				}
				case "iushr" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("iushr does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.IUSHR));
				}
				case "lushr" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("lushr does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LUSHR));
				}
				case "iand" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("iand does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.IAND));
				}
				case "land" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("land does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LAND));
				}
				case "ior" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("ior does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.IOR));
				}
				case "lor" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("lor does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LOR));
				}
				case "ixor" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("ixor does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.IXOR));
				}
				case "lxor" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("lxor does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LXOR));
				}

				case "iinc" -> {
					if (instruction.length != 3) {
						throw new IllegalArgumentException("iinc takes two integer arguments");
					}
					try {
						instructions.add(new IincInsnNode(parseInt(instruction[1]), parseInt(instruction[2])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("iinc takes two integer arguments", e);
					}
				}

				case "i2l" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("i2l does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.I2L));
				}
				case "i2f" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("i2f does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.I2F));
				}
				case "i2d" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("i2d does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.I2D));
				}
				case "l2i" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("l2i does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.L2I));
				}
				case "l2f" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("l2f does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.L2F));
				}
				case "l2d" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("l2d does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.L2D));
				}
				case "f2i" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("f2i does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.F2I));
				}
				case "f2l" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("f2l does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.F2L));
				}
				case "f2d" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("f2d does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.F2D));
				}
				case "d2i" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("d2i does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.D2I));
				}
				case "d2l" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("d2l does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.D2L));
				}
				case "d2f" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("d2f does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.D2F));
				}
				case "i2b" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("i2b does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.I2B));
				}
				case "i2c" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("i2c does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.I2C));
				}
				case "i2s" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("i2s does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.I2S));
				}
				case "lcmp" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("lcmp does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LCMP));
				}
				case "fcmpl" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("fcmpl does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.FCMPL));
				}
				case "fcmpg" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("fcmpg does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.FCMPG));
				}
				case "dcmpl" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("dcmpl does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DCMPL));
				}
				case "dcmpg" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("dcmpg does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DCMPG));
				}

				case "ifeq" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("ifeq takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IFEQ, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("ifeq takes a single label argument", e);
					}
				}
				case "ifne" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("ifne takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IFNE, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("ifne takes a single label argument", e);
					}
				}
				case "iflt" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("iflt takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IFLT, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("iflt takes a single label argument", e);
					}
				}
				case "ifge" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("ifge takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IFGE, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("ifge takes a single label argument", e);
					}
				}
				case "ifgt" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("ifgt takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IFGT, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("ifgt takes a single label argument", e);
					}
				}
				case "ifle" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("ifle takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IFLE, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("ifle takes a single label argument", e);
					}
				}
				case "if_icmpeq" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("if_icmpeq takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("if_icmpeq takes a single label argument", e);
					}
				}
				case "if_icmpne" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("if_icmpne takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IF_ICMPNE, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("if_icmpne takes a single label argument", e);
					}
				}
				case "if_icmplt" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("if_icmplt takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IF_ICMPLT, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("if_icmplt takes a single label argument", e);
					}
				}
				case "if_icmpge" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("if_icmpge takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IF_ICMPGE, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("if_icmpge takes a single label argument", e);
					}
				}
				case "if_icmpgt" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("if_icmpgt takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IF_ICMPGT, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("if_icmpgt takes a single label argument", e);
					}
				}
				case "if_icmple" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("if_icmple takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IF_ICMPLE, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("if_icmple takes a single label argument", e);
					}
				}
				case "if_acmpeq" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("if_acmpeq takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IF_ACMPEQ, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("if_acmpeq takes a single label argument", e);
					}
				}
				case "if_acmpne" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("if_acmpne takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IF_ACMPNE, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("if_acmpne takes a single label argument", e);
					}
				}
				case "goto" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("goto takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.GOTO, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("goto takes a single label argument", e);
					}
				}
				case "jsr" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("jsr takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.JSR, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("jsr takes a single label argument", e);
					}
				}

				case "ret" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("ret takes a single integer argument");
					}
					try {
						instructions.add(new VarInsnNode(Opcodes.RET, parseInt(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("ret takes a single integer argument", e);
					}
				}

				//TODO TABLESWITCH
				//TODO LOOKUPSWITCH

				case "ireturn" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("ireturn does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.IRETURN));
				}
				case "lreturn" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("lreturn does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.LRETURN));
				}
				case "freturn" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("freturn does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.FRETURN));
				}
				case "dreturn" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("dreturn does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.DRETURN));
				}
				case "areturn" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("areturn does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.ARETURN));
				}
				case "return" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("return does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.RETURN));
				}

				case "getstatic" -> {
					if (instruction.length != 4) {
						throw new IllegalArgumentException("getstatic takes an owner, identifier and am object descriptor");
					}
					try {
						instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, parseClass(instruction[1]), instruction[2], parseObjectDes(instruction[3])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("getstatic takes an owner, identifier and am object descriptor", e);
					}
				}
				case "putstatic" -> {
					if (instruction.length != 4) {
						throw new IllegalArgumentException("putstatic takes an owner, identifier and am object descriptor");
					}
					try {
						instructions.add(new FieldInsnNode(Opcodes.PUTSTATIC, parseClass(instruction[1]), instruction[2], parseObjectDes(instruction[3])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("putstatic takes an owner, identifier and am object descriptor", e);
					}
				}
				case "getfield" -> {
					if (instruction.length != 4) {
						throw new IllegalArgumentException("getfield takes an owner, identifier and am object descriptor");
					}
					try {
						instructions.add(new FieldInsnNode(Opcodes.GETFIELD, parseClass(instruction[1]), instruction[2], parseObjectDes(instruction[3])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("getfield takes an owner, identifier and am object descriptor", e);
					}
				}
				case "putfield" -> {
					if (instruction.length != 4) {
						throw new IllegalArgumentException("putfield takes an owner, identifier and am object descriptor");
					}
					try {
						instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, parseClass(instruction[1]), instruction[2], parseObjectDes(instruction[3])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("putfield takes an owner, identifier and am object descriptor", e);
					}
				}

				case "invokevirtual" -> {
					if (instruction.length != 4) {
						throw new IllegalArgumentException("invokevirtual takes an owner, identifier and a method descriptor");
					}
					try {
						instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, parseClass(instruction[1]), instruction[2], parseMethodDesc(instruction[3])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("invokevirtual takes an owner, identifier and a method descriptor", e);
					}
				}
				case "invokespecial" -> {
					if (instruction.length != 4) {
						throw new IllegalArgumentException("invokespecial takes an owner, identifier and a method descriptor");
					}
					try {
						instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, parseClass(instruction[1]), instruction[2], parseMethodDesc(instruction[3])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("invokespecial takes an owner, identifier and a method descriptor", e);
					}
				}
				case "invokestatic" -> {
					if (instruction.length != 4) {
						throw new IllegalArgumentException("invokestatic takes an owner, identifier and a method descriptor");
					}
					try {
						instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, parseClass(instruction[1]), instruction[2], parseMethodDesc(instruction[3])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("invokestatic takes an owner, identifier and a method descriptor", e);
					}
				}
				case "invokeinterface" -> {
					if (instruction.length != 4) {
						throw new IllegalArgumentException("invokeinterface takes an owner, identifier and a method descriptor");
					}
					try {
						instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, parseClass(instruction[1]), instruction[2], parseMethodDesc(instruction[3])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("invokeinterface takes an owner, identifier and a method descriptor", e);
					}
				}

				//TODO invokedynamic

				case "new" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("new takes an owner argument");
					}
					try {
						instructions.add(new TypeInsnNode(Opcodes.NEW, parseClass(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("new takes an owner argument", e);
					}
				}

				case "newarray" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("newarray takes a type argument");
					}
					try {
						instructions.add(new IntInsnNode(Opcodes.NEWARRAY, parseArrayType(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("newarray takes a type argument", e);
					}
				}

				case "anewarray" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("anewarray takes an owner argument");
					}
					try {
						instructions.add(new TypeInsnNode(Opcodes.ANEWARRAY, parseClass(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("anewarray takes an owner argument", e);
					}
				}

				case "arraylength" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("arraylength does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.ARRAYLENGTH));
				}
				case "atrhow" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("athrow does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.ATHROW));
				}

				case "checkcast" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("checkcast takes a single type argument");
					}
					try {
						instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, parseClass(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("checkcast takes a single type argument", e);
					}
				}
				case "instanceof" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("instanceof takes a single type argument");
					}
					try {
						instructions.add(new TypeInsnNode(Opcodes.INSTANCEOF, parseClass(instruction[1])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("instanceof takes a single type argument", e);
					}
				}

				case "monitorenter" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("monitorenter does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.MONITORENTER));
				}
				case "monitorexit" -> {
					if (instruction.length != 1) {
						throw new IllegalArgumentException("monitorexit does not take arguments");
					}
					instructions.add(new InsnNode(Opcodes.MONITOREXIT));
				}

				case "multianewarray" -> {
					if (instruction.length != 3) {
						throw new IllegalArgumentException("multianewarray takes a type and int argument");
					}
					try {
						instructions.add(new MultiANewArrayInsnNode(parseClass(instruction[1]), parseInt(instruction[2])));
					} catch (Throwable e) {
						throw new IllegalArgumentException("multianewarray takes a type and int argument", e);
					}
				}

				case "ifnull" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("ifnull takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IFNULL, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("ifnull takes a single label argument", e);
					}
				}
				case "ifnonnull" -> {
					if (instruction.length != 2) {
						throw new IllegalArgumentException("ifnonnull takes a single label argument");
					}
					try {
						instructions.add(new JumpInsnNode(Opcodes.IFNONNULL, parseLabel(instruction[1], labels)));
					} catch (Throwable e) {
						throw new IllegalArgumentException("ifnonnull takes a single label argument", e);
					}
				}

				default -> {
					throw new IllegalArgumentException("Unknown opcode: " + instruction[0]);
				}
			}
		}

		return new Output(
			instructions,
			Collections.unmodifiableList(locals),
			maxStack, maxLocal
		);
	}

	private static String[] split(String line) {
		var split = new ArrayList<String>();
		var builder = new StringBuilder(line.length());

		var string = false;
		var escape = false;
		for (var codepoint : line.chars().toArray()) {
			if (escape) {
				escape = false;
				builder.append(Character.toString(codepoint));
			}
			if (!string) {
				if (codepoint == ' ') {
					if (!builder.isEmpty()) {
						split.add(builder.toString());
						builder.setLength(0);
					}
					continue;
				}
			}
			if (codepoint == '\\') {
				escape = true;
				continue;
			}
			if (codepoint == '"') {
				builder.append('"');
				if (string) {
					split.add(builder.toString());
					builder.setLength(0);
					string = false;
				} else {
					string = true;
				}
				continue;
			}
			builder.append(Character.toString(codepoint));
		}

		if (!builder.isEmpty()) {
			split.add(builder.toString());
		}

		return split.toArray(String[]::new);
	}

	private enum Radix {
		BIN("0b", 2),
		OCT("0", 8),
		HEX("0x", 16),
		DEC("", 10);

		private final String prefix;

		private final int value;

		Radix(String prefix, int value) {
			this.prefix = prefix;
			this.value = value;
		}

		private static <T extends Number> T parse(String string, BiFunction<String, Integer, T> parser) {
			var radix = get(string);
			return parser.apply(string.substring(radix.prefix.length()), radix.value);
		}

		private static Radix get(String string) {
			if (string.equals("0")) {
				return DEC;
			}
			for (var value : values()) {
				if (string.startsWith(value.prefix)) {
					return value;
				}
			}
			return DEC;
		}
	}

	private static byte parseByte(String string) {
		return Radix.parse(string, Byte::parseByte);
	}

	private static short parseShort(String string) {
		return Radix.parse(string, Short::parseShort);
	}

	private static int parseInt(String string) {
		return Radix.parse(string, Integer::parseInt);
	}

	private static Object parseLdc(String string) {
		if (string.startsWith("\"")) {
			return string.substring(1, string.length() - 1);
		}
		if (string.startsWith("L") || string.startsWith("[")) {
			return parseObjectDes(string);
		}
		if (string.endsWith("L")) {
			return Radix.parse(string, Long::parseLong);
		}
		if (string.endsWith("F")) {
			return Float.parseFloat(string);
		}
		if (string.contains(".") || string.endsWith("D")) {
			return Double.parseDouble(string);
		}
		//TODO Method type, handles, constant dynamic
		return parseInt(string);
	}

	private static LabelNode parseLabel(String string, HashMap<String, LabelNode> labels) {
		var label = labels.get(string);
		if (label != null) {
			return label;
		}
		label = new LabelNode(new Label());
		labels.put(string, label);
		return label;
	}

	private static String parseClass(String string) {
		return Type.getObjectType(string).getInternalName();
	}

	private static String parseObjectDes(String string) {
		var type = Type.getType(string);
		if (type.getSort() == Type.METHOD) {
			throw new IllegalArgumentException(string + " is a method type");
		}
		return type.getDescriptor();
	}

	private static String parseMethodDesc(String string) {
		var type = Type.getType(string);
		if (type.getSort() != Type.METHOD) {
			throw new IllegalArgumentException(string + " is not a method type");
		}
		return type.getDescriptor();
	}

	private static int parseArrayType(String string) {
		return switch (string.toLowerCase()) {
			case "boolean" -> Opcodes.T_BOOLEAN;
			case "char" -> Opcodes.T_CHAR;
			case "float" -> Opcodes.T_FLOAT;
			case "double" -> Opcodes.T_DOUBLE;
			case "byte" -> Opcodes.T_BYTE;
			case "short" -> Opcodes.T_SHORT;
			case "int" -> Opcodes.T_INT;
			case "long" -> Opcodes.T_LONG;
			default -> throw new IllegalArgumentException(string + " is not an array type");
		};
	}

	//TODO
	private static String parseSignature(String signature) {
		return signature.equalsIgnoreCase("null") ? null : signature;
	}

	private Assembler() {
		throw new AssertionError();
	}
}
