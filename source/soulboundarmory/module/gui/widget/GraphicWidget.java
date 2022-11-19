package soulboundarmory.module.gui.widget;

import java.util.function.Consumer;

public class GraphicWidget extends Widget<GraphicWidget> {
	public final Consumer<GraphicWidget> render;

	public GraphicWidget(Consumer<GraphicWidget> render) {
		this.render = render;
	}

	@Override protected void render() {
		this.render.accept(this);
	}
}
