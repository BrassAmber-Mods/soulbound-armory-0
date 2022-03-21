package soulboundarmory.text;

public record PluralizableTranslation(Translation singular, Translation plural) {
    public Translation get(int count) {
        return count == 1 ? this.singular : this.plural;
    }

    public Translation format(int count) {
        return this.get(count).format(count);
    }
}
