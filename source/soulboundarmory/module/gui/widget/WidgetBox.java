package soulboundarmory.module.gui.widget;

import soulboundarmory.module.gui.Length;
import soulboundarmory.module.gui.Node;

public class WidgetBox extends Widget<WidgetBox> {
    public int xSpacing;
    public int ySpacing;
    public boolean horizontal = true;

    public WidgetBox() {
        this.width.type(Length.Type.CHILD_SUM);
    }

    @Override public int width() {
        return this.horizontal ? this.children.stream().mapToInt(Node::width).sum() + this.xSpacing * Math.max(0, this.degree() - 1) : super.width();
    }

    @Override public int height() {
        return this.horizontal ? super.height() : this.children.stream().mapToInt(Node::height).sum() + this.ySpacing * Math.max(0, this.degree() - 1);
    }

    @Override public <C extends Widget> C add(int index, C child) {
        return this.update(super.add(index, child));
    }

    public WidgetBox xSpacing(int spacing) {
        this.xSpacing = spacing;

        return this.horizontal();
    }

    public WidgetBox ySpacing(int spacing) {
        this.xSpacing = spacing;

        return this.vertical();
    }

    public WidgetBox horizontal() {
        this.horizontal = true;

        return this;
    }

    public WidgetBox vertical() {
        this.horizontal = false;

        return this;
    }

    protected <W extends Widget<?>> W update(W child) {
        var previous = this.degree() < 2 ? null : this.children.get(this.degree() - 2);

        if (this.horizontal) {
            child.x(previous == null ? 0 : previous.endX() + this.xSpacing);
        } else {
            child.y(previous == null ? 0 : previous.endY() + this.ySpacing);
        }

        return child;
    }
}
