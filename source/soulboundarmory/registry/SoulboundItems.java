package soulboundarmory.registry;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.List;
import java.util.Map;
import net.auoeke.reflect.Invoker;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.item.SoulboundBigswordItem;
import soulboundarmory.item.SoulboundDagger;
import soulboundarmory.item.SoulboundGreatsword;
import soulboundarmory.item.SoulboundPickItem;
import soulboundarmory.item.SoulboundStaffItem;
import soulboundarmory.item.SoulboundSword;

public class SoulboundItems {
    private static final Reference2ReferenceOpenHashMap<ToolMaterial, Map<Tag<Block>, ToolMaterial>> materials = new Reference2ReferenceOpenHashMap<>();

    public static final ToolMaterial material = material(ToolMaterials.WOOD);

    public static final SoulboundDagger dagger = name(new SoulboundDagger(), "dagger");
    public static final SoulboundSword sword = name(new SoulboundSword(), "sword");
    public static final SoulboundGreatsword greatsword = name(new SoulboundGreatsword(), "greatsword");
    public static final SoulboundStaffItem staff = name(new SoulboundStaffItem(), "staff");
    public static final SoulboundPickItem pick = name(new SoulboundPickItem(), "pick");
    public static final SoulboundBigswordItem bigsword = name(new SoulboundBigswordItem(), "bigsword");

    public static ToolMaterial material(ToolMaterial previous) {
        return materials.computeIfAbsent(previous, previous1 -> new Reference2ReferenceOpenHashMap<>()).computeIfAbsent(previous.getTag(), tag -> {
            var material = TierSortingRegistry.registerTier(
                new ForgeTier(previous.getMiningLevel(), 0, 1.5F, 0, 0, tag, () -> null),
                new Identifier(SoulboundArmory.ID, TierSortingRegistry.getSortedTiers().contains(previous) ? TierSortingRegistry.getName(previous).toUnderscoreSeparatedString() : "material"),
                List.of(previous),
                List.of()
            );

            Invoker.invoke(Invoker.findStatic(TierSortingRegistry.class, "recalculateItemTiers", void.class));

            return material;
        });
    }

    private static <T extends Item> T name(T item, String path) {
        return (T) item.setRegistryName(path);
    }
}
