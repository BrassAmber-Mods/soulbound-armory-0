package soulboundarmory.lib.component.access;

import soulboundarmory.lib.component.ItemStackComponent;
import soulboundarmory.lib.component.ItemStackComponentKey;

public interface ItemStackAccess {
    /**
     Get the instance a component.

     @param key the component's key
     @return the component instance if it exists or null.
     */
    ItemStackComponent<?> soulboundarmory$component(ItemStackComponentKey<?> key);

    /**
     Add a component instance.

     @param key the key of the component
     @param component the component instance
     @return the previous component instance if it exists or null.
     */
    ItemStackComponent<?> soulboundarmory$component(ItemStackComponentKey<?> key, ItemStackComponent<?> component);
}
