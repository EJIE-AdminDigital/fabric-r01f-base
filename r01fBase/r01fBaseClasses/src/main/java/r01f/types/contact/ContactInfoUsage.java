package r01f.types.contact;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import r01f.enums.EnumExtended;
import r01f.locale.I18NBundleAccess;
import r01f.locale.I18NKey;

/**
 * Contact info usage
 */
@RequiredArgsConstructor
public enum ContactInfoUsage
 implements EnumExtended<ContactInfoUsage> {
	ANY			(I18NKey.named("contact.usage.any")),			// any usage
	WORK		(I18NKey.named("contact.usage.work")),			// Work usage
	PERSONAL	(I18NKey.named("contact.usage.personal")),		// Personal
	COMPANY		(I18NKey.named("contact.usage.company")),		// Company's
	OTHER		(I18NKey.named("contact.usage.other"));			// Other usage

	private final I18NKey _i18nKey;
	
	public String nameUsing(final I18NBundleAccess i18n) {
		return i18n.getMessage(_i18nKey);
	}
	
	public static Collection<String> namesUsing(final I18NBundleAccess i18n) {
		return Stream.of(ContactInfoUsage.values())
					 .map(u -> u.nameUsing(i18n))
					 .collect(Collectors.toList());
	}
}
