package r01f.types.contact;

import r01f.enums.EnumExtended;

public enum NotificationMedium
 implements EnumExtended<NotificationMedium> {
	EMAIL,
	SMS,
	VOICE,
	LOG,
	PUSH;

	public String asStringLowerCase() {
		return this.name().toLowerCase();
	}
}
