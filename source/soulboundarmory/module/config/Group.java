package soulboundarmory.module.config;

import java.util.Locale;
import soulboundarmory.util.Util2;

public class Group extends Parent {
	public final Parent parent;
	public final boolean flat;
	public final String comment;

	public Group(Parent parent, Class<?> type) {
		super(type, Util2.value(type, Name::value, type.getSimpleName()).toLowerCase(Locale.ROOT), Util2.value(type, (Category category) -> {
			if (!type.getDeclaringClass().isAnnotationPresent(ConfigurationFile.class)) {
				throw new IllegalArgumentException("@Category found on 2+ level nested " + type);
			}

			return category.value();
		}, parent.category));

		this.parent = parent;
		this.flat = type.isAnnotationPresent(Flat.class);
		this.comment = Util2.value(type, (Comment comment) -> String.join("\n", comment.value()));
	}
}
