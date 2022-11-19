package soulboundarmory.text;

import net.minecraft.text.MutableText;

public record PluralizableTranslation(Translation singular, Translation plural) {
	public Translation get(int count) {
		return count == 1 ? this.singular : this.plural;
	}

	public MutableText text(int count) {
		return this.get(count).text(count);
	}
}
