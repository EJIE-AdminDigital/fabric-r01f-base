package r01f.locale;

import lombok.Getter;
import r01f.guids.OID;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;

/**
 * A view key
 */
@MarshallType(as="i18nKey")
public record I18NKey(@Getter String id)
   implements OIDTyped<String> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static I18NKey named(final String id) {
		return new I18NKey(id);
	}
	public static I18NKey valueOf(final String str) {
		return new I18NKey(str);
	}
	public static I18NKey fromString(final String s) {
		return I18NKey.forId(s);
	}
	/**
	 * Dirty trick used when the I18NKey is NOT really a key but a literal string  
	 * @param id
	 * @return
	 */
	public static I18NKey literal(final String id) {
		return new I18NKey("$!" + id);
	}
	public static <O extends OID> I18NKey named(final O id) {
		return I18NKey.forId(id);
	}
	public static I18NKey named(final CanBeRepresentedAsString id) {
		return I18NKey.forId(id.asString());
	}
	public static I18NKey forId(final String id) {
		return new I18NKey(id);
	}
	public static <O extends OID> I18NKey forId(final O oid) {
		return new I18NKey(oid.toString());
	}
	@Override
	public String toString() {
		return this.id;
	}
	@Override
	public String asString() {
		return this.getId();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean is(final String id) {
		return this.asString().equals(id);
	}
	public boolean isLiteral() {
		return this.getId() != null && this.getId().startsWith("$!");
	}
	public boolean isNOTLiteral() {
		return !this.isLiteral();
	}
}
