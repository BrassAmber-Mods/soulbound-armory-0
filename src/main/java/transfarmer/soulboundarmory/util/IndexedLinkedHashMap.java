package transfarmer.soulboundarmory.util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class IndexedLinkedHashMap<K, V> extends LinkedHashMap<K, V> implements IndexedMap<K, V> {
    @Nonnull
    @Override
    public List<K> keyList() {
        return new ArrayList<>(this.keySet());
    }

    @Override
    @Nonnull
    public List<V> valueList() {
        return new ArrayList<>(this.values());
    }

    @Override
    @Nonnull
    public K getKey(final int index) {
        return this.keyList().get(index);
    }

    @Override
    @Nonnull
    public V getValue(final int index) {
        return this.valueList().get(index);
    }

    @Override
    @Nonnull
    public Iterator<K> iterator() {
        return this.keyList().iterator();
    }
}
