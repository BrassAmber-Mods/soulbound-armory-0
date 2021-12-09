package soulboundarmory.lib.component.access;

import java.util.Map;
import soulboundarmory.lib.component.Component;
import soulboundarmory.lib.component.ComponentKey;

public interface EntityAccess {
    Map<ComponentKey<?>, Component> soulboundarmory$components();
}
