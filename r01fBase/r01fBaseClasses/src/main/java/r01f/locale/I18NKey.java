package r01f.locale;

import r01f.guids.OID;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.types.CanBeRepresentedAsString;

/**
 * A view key
 */
@MarshallType(as="i18nKey")
public class I18NKey
	 extends OIDBaseMutable<String> {

	private static final long serialVersionUID = 7659549152655411096L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public I18NKey(final String oid) {
		super(oid);
	}
	public static I18NKey named(final String id) {
		return new I18NKey(id);
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
	public static I18NKey valueOf(final String id) {
		return I18NKey.forId(id);
	}
	@Override
	public boolean is(final String id) {
		return this.asString().equals(id);
	}
	private final transient Memoized<Boolean> _isLiteral = Memoized.using(() -> this.getId() != null && this.getId().startsWith("$!"));
	public boolean isLiteral() {
		return _isLiteral.get();
	}
	public boolean isNOTLiteral() {
		return !this.isLiteral();
	}
}
