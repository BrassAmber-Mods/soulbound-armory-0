package user11681.soulboundarmory.component.soulbound.item.weapon;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import user11681.soulboundarmory.client.gui.screen.tab.AttributeTab;
import user11681.soulboundarmory.client.gui.screen.tab.EnchantmentTab;
import user11681.soulboundarmory.client.gui.screen.tab.SelectionTab;
import user11681.soulboundarmory.client.gui.screen.tab.SkillTab;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.item.SoulboundWeaponItem;
import user11681.usersmanual.client.gui.screen.ScreenTab;

public abstract class WeaponStorage<T extends ItemStorage<T>> extends ItemStorage<T> {
    public WeaponStorage(final SoulboundComponent component, final Item item) {
        super(component, item);
    }

    @Environment(EnvType.CLIENT)
    public List<ScreenTab> getTabs() {
        final List<ScreenTab> tabs = new ArrayList<>();

        tabs.add(new SelectionTab(Translations.menuWeaponSelection, this.component, tabs));
        tabs.add(new AttributeTab(this.component, tabs));
        tabs.add(new EnchantmentTab(this.component, tabs));
        tabs.add(new SkillTab(this.component, tabs));

        return tabs;
    }

    @Override
    public int getLevelXP(final int level) {
        return this.canLevelUp()
                ? Configuration.instance().initialWeaponXP + 3 * (int) Math.round(Math.pow(level, 1.65))
                : -1;
    }

    @Override
    public Class<? extends SoulboundItem> getBaseItemClass() {
        return SoulboundWeaponItem.class;
    }
}
