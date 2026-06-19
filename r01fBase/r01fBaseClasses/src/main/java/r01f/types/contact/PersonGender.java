package r01f.types.contact;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.enums.EnumExtended;
import r01f.locale.I18NBundleAccess;
import r01f.locale.I18NKey;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum PersonGender 
 implements EnumExtended<PersonGender>	{
	MALE(I18NKey.named("person.gender.male")),
	FEMALE(I18NKey.named("person.gender.female")),
	NOT_SPECIFIED(I18NKey.named("person.gender.other"));
	
	private final I18NKey _i18nKey;
	
	public String nameUsing(final I18NBundleAccess i18n) {
		return i18n.getMessage(_i18nKey);
	}
}
