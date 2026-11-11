package r01f.types;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import r01f.oids.CommonOIDs.IsAppCode;
import r01f.oids.OID;

/**
 * Represents a java package
 */
public record JavaPackage(String pckg)
   implements CanBeRepresentedAsString,
			  Serializable {
	private static final long serialVersionUID = 7316410643001225993L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaPackage {
		Objects.requireNonNull(pckg);
	}
	public <O extends OID> JavaPackage(final O oid) {
		this(oid.asString());
	}
	public JavaPackage(final Package pckg) {
		this(pckg.getName());
	}
	public JavaPackage(final Class<?> type) {
		this(type.getPackage());
	}
	public static JavaPackage named(final IsAppCode appCode) {
		return new JavaPackage(appCode.asString());
	}
	public static JavaPackage named(final String pckg) {
		return new JavaPackage(pckg);
	}
	public static JavaPackage of(final IsAppCode appCode) {
		return new JavaPackage(appCode.asString());
	}
	public static JavaPackage of(final String pckg) {
		return new JavaPackage(pckg);
	}
	public static JavaPackage of(final Package pckg) {
		return new JavaPackage(pckg);
	}
	public static JavaPackage of(final Class<?> type) {
		return new JavaPackage(type);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaPackage joinedWith(final JavaPackage other) {
		if (other == null) return this;
		return new JavaPackage(this.pckg() + "." + other.pckg());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return this.pckg;
	}
	@Override
	public String toString() {
		return this.asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isJavaLang() {
		return this.pckg().startsWith("java.lang");
	}
	public boolean isJavax() {
		return this.pckg().startsWith("javax");
	}
	public boolean isJakarta() {
		return this.pckg().startsWith("jakarta");
	}
	public boolean startsWith(final JavaPackage other) {
		if (other == null) return false;
		return this.pckg().startsWith(other.pckg());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Path asPath() {
		return Path.from(this.pckg().replace('.', '/'));	// replaces ALL dots with slashes
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Function<IsAppCode,JavaPackage> APP_CODE_TO_JAVA_PACKAGE = JavaPackage::of;
}
