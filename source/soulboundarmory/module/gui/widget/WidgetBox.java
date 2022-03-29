package soulboundarmory.module.gui.widget;

import soulboundarmory.module.gui.Node;

public class WidgetBox<T extends WidgetBox<T>> extends Widget<T> {
    public int xSpacing;
    public int ySpacing;
    public boolean horizontal = true;

    @Override public int width() {
        return this.horizontal ? Math.max(this.minWidth, this.children.stream().filter(Widget::isVisible).mapToInt(Node::width).sum() + this.xSpacing * Math.max(0, this.degree() - 1)) : super.width();
    }

    @Override public int height() {
        return this.horizontal ? super.height() : Math.max(this.minHeight, this.children.stream().filter(Widget::isVisible).mapToInt(Node::height).sum() + this.ySpacing * Math.max(0, this.degree() - 1));
    }

    @Override public <C extends Widget> C add(int index, C child) {
        return this.update(super.add(index, child));
    }

    public T xSpacing(int spacing) {
        this.xSpacing = spacing;

        return this.horizontal();
    }

    public T ySpacing(int spacing) {
        this.xSpacing = spacing;

        return this.vertical();
    }

    public T horizontal() {
        this.horizontal = true;

        return (T) this;
    }

    public T vertical() {
        this.horizontal = false;

        return (T) this;
    }

    protected <C extends Widget<?>> C update(C child) {
        var previous = this.degree() < 2 ? null : this.children.get(this.degree() - 2);

        if (this.horizontal) {
            child.x(previous == null ? 0 : previous.endX() + this.xSpacing);
        } else {
            child.y(previous == null ? 0 : previous.endY() + this.ySpacing);
        }

        return child;
    }
}
