package soulboundarmory.module.config.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Stream;
import net.minecraft.util.Formatting;
import soulboundarmory.module.config.Property;
import soulboundarmory.module.gui.widget.TextBoxWidget;
import soulboundarmory.module.gui.widget.TextWidget;
import soulboundarmory.module.gui.widget.TooltipWidget;

public abstract class TextPropertyWidget<T> extends TextBoxWidget {
	protected final Property<T> property;

	public TextPropertyWidget(Property<T> property) {
		this.property = property;
		this.line().append(property.get());
	}

	@Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			this.validate0();

			return true;
		}

		return false;
	}

	@Override public boolean charTyped(char character, int modifiers) {
		if (super.charTyped(character, modifiers)) {
			this.validate0();

			return true;
		}

		return false;
	}

	protected abstract void validate();

	private void validate0() {
		this.text.clearTooltips();

		try {
			this.validate();
			this.text.color(Formatting.WHITE);
		} catch (Throwable trouble) {
			this.text.color(Formatting.RED);
			var trace = new StringWriter();
			trouble.setStackTrace(Stream.of(trouble.getStackTrace()).filter(frame -> !frame.getClassName().startsWith("java.util.stream.")).toArray(StackTraceElement[]::new));
			trouble.printStackTrace(new PrintWriter(trace));
			this.text.tooltip(new TooltipWidget().with(new TextWidget().text(trace.toString())));
		}
	}
}
