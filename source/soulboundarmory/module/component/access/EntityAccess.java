package soulboundarmory.module.component.access;

import java.util.Map;
import soulboundarmory.module.component.EntityComponent;
import soulboundarmory.module.component.EntityComponentKey;

public interface EntityAccess {
	Map<EntityComponentKey<?>, EntityComponent<?>> soulboundarmory$components();
}
