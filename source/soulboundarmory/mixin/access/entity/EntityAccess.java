package soulboundarmory.mixin.access.entity;

import java.util.Map;
import soulboundarmory.component.Component;
import soulboundarmory.component.ComponentKey;

public interface EntityAccess {
    Map<ComponentKey<?, ?>, Component> soulboundarmory$components();
}
