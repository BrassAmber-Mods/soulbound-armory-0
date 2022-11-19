package soulboundarmory.mixin.mixin.client;

import java.util.stream.IntStream;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import soulboundarmory.client.keyboard.GUIKeyBinding;
import soulboundarmory.component.Components;
import soulboundarmory.module.gui.widget.Widget;

@Mixin(Keyboard.class)
abstract class KeyboardMixin {
	@Inject(method = "onKey", at = @At(value = "JUMP", opcode = Opcodes.IFNE, ordinal = 0), cancellable = true)
	private void tryOpenSoulboundArmoryMenu(long window, int key, int scanCode, int action, int modifiers, CallbackInfo info) {
		if (GUIKeyBinding.instance.matchesKey(key, scanCode)) switch (action) {
			case GLFW.GLFW_RELEASE -> GUIKeyBinding.instance.setPressed(false);
			case GLFW.GLFW_PRESS -> {
				a:
				if (!GUIKeyBinding.instance.isPressed()) {
					IntStream stacks;

					if (Widget.screen() instanceof HandledScreen<?> screen) {
						var slot = screen.getSlotUnderMouse();

						if (slot != null && slot.inventory == Widget.player().getInventory() && slot.hasStack()) {
							stacks = IntStream.of(slot.getIndex());
						} else {
							break a;
						}
					} else if (Widget.screen() == null) {
						stacks = IntStream.of(Widget.player().getInventory().selectedSlot, 40);
					} else {
						break a;
					}

					if (stacks.anyMatch(slot -> Components.soulbound(Widget.player()).anyMatch(component -> component.tryOpenGUI(component.player.getInventory().getStack(slot), slot)))) {
						info.cancel();
					}
				}

				GUIKeyBinding.instance.setPressed(true);
			}
		}
	}
}
