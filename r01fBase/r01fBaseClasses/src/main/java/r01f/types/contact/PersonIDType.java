package r01f.types.contact;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.enums.EnumExtended;
import r01f.locale.I18NBundleAccess;
import r01f.locale.I18NKey;
import r01f.util.types.Strings;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum PersonIDType
 implements EnumExtended<PersonIDType> {
	SPANISH_NIF(I18NKey.named("personId.type.spanish-nif")),
	PASSPORT(I18NKey.named("personId.type.passport")),
	UNKNOWN(I18NKey.named("personId.type.unknown"));
	
	private final I18NKey _i18nKey;
	
	public String nameUsing(final I18NBundleAccess i18n) {
		return i18n.getMessage(_i18nKey);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean isSpanishNIF() {
		return this.is(SPANISH_NIF);
	}
	public boolean isPassport() {
		return this.is(PASSPORT);
	}
	public boolean isUnknown() {
		return this.is(UNKNOWN);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static PersonIDType from(final String idStr) {
		if (Strings.isNullOrEmpty(idStr)) return UNKNOWN;
		if (NIFValidator.isValidNIF(idStr)) return SPANISH_NIF;
		if (IsPersonID.canBePassport(idStr)) return PASSPORT;	// BEWARE!!! this is not 100% reliable since passport numbers don't have a single format, but it's the best that can be done
		return UNKNOWN;
	}
	public static PersonIDType from(final IsPersonID personId) {
		if (personId == null) return UNKNOWN;
		return PersonIDType.from(personId.asString());
	}
}
