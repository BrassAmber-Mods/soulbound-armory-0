package soulboundarmory.module.config;

import java.util.Locale;
import soulboundarmory.util.Util;

public class Group extends Parent {
	public final Parent parent;
	public final boolean flat;
	public final String comment;

	public Group(Parent parent, Class<?> type) {
		super(type, Util.value(type, Name::value, type.getSimpleName()).toLowerCase(Locale.ROOT), Util.value(type, (Category category) -> {
			if (!type.getDeclaringClass().isAnnotationPresent(ConfigurationFile.class)) {
				throw new IllegalArgumentException("@Category found on 2+ level nested " + type);
			}

			return category.value();
		}, parent.category));

		this.parent = parent;
		this.flat = type.isAnnotationPresent(Flat.class);
		this.comment = Util.value(type, (Comment comment) -> String.join("\n", comment.value()));
	}
}
