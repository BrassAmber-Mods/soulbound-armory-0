package soulboundarmory.module.component;

/**
 A component for an entity.
 */
public interface EntityComponent<C extends EntityComponent<C>> extends Component<C> {
	/**
	 Do something after the entity has been spawned.
	 */
	default void spawn() {}
}
