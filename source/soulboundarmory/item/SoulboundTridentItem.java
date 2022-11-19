package soulboundarmory.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.TridentItem;

public class SoulboundTridentItem extends TridentItem implements SoulboundWeaponItem {
	public SoulboundTridentItem() {
		super(new Settings().group(ItemGroup.COMBAT));
	}
}
