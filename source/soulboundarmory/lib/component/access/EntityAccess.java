package soulboundarmory.lib.component.access;

import java.util.Map;
import soulboundarmory.lib.component.EntityComponent;
import soulboundarmory.lib.component.EntityComponentKey;

public interface EntityAccess {
    Map<EntityComponentKey<?>, EntityComponent<?>> soulboundarmory$components();
}
