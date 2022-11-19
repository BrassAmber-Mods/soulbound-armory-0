package soulboundarmory.module.gui.widget.slider;

public interface SlideCallback {
	void onSlide(Slider slider);

	default SlideCallback then(SlideCallback callback) {
		return slider -> {
			this.onSlide(slider);
			callback.onSlide(slider);
		};
	}
}
