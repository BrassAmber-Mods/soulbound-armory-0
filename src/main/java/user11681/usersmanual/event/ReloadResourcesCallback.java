package user11681.usersmanual.event;

import java.util.List;
import java.util.concurrent.Executor;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;

public interface ReloadResourcesCallback {
    Event<ReloadResourcesCallback> EVENT = EventFactory.createArrayBacked(ReloadResourcesCallback.class,
            (final ReloadResourcesCallback[] listeners) ->
                    (final Executor prepareExecutor, final Executor applyExecutor, final ResourceManager manager, final List<ResourceReloadListener> reloadListeners) -> {
                        for (final ReloadResourcesCallback listener : listeners) {
                            listener.apply(prepareExecutor, applyExecutor, manager, reloadListeners);
                        }
                    }
    );

    void apply(Executor prepareExecutor, Executor applyExecutor, ResourceManager manager, List<ResourceReloadListener> listeners);
}
