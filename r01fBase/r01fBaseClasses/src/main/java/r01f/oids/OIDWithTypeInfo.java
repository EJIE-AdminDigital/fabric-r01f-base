package r01f.oids;

import com.google.common.base.Splitter;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.util.types.Objects;
import r01f.util.types.StringSplitter;
import r01f.util.types.Strings;

/**
 * Encodes an OID alongside with the OID type info as {oid type}:{id}
 * Usage:
 * <pre class='brush:java'>
 *		public static class MyOIDWithTypeInfo
 *					extends OIDWithTypeInfo<String> {
 *
 *			private static final long serialVersionUID = 7271144297005572505L;
 *
 *			public MyOIDWithTypeInfo(final String idWithType) {
 *				super(idWithType);
 *			}
 *			public <O extends OID> MyOIDWithTypeInfo(final O oid) {
 *				super(oid);
 *			}
 *	 		public MyOIDWithTypeInfo(final Class<? extends OID> oidType,final String id) {
 *				super(oidType,id);
 *			}
 *			public MyOIDWithTypeInfo(final String oidTypeStr,final String id) {
 *				super(oidTypeStr,id);
 *			}
 *			public static <O extends OID> MyOIDWithTypeInfo of(final Class<O> oidType,final String oid) {
 *				return new MyOIDWithTypeInfo(oidType,oid);
 *			}
 *			public static MyOIDWithTypeInfo of(final String oidTypeStr,final String oid) {
 *				return new MyOIDWithTypeInfo(oidTypeStr,oid);
 *			}
 *			public static <O extends OID> MyOIDWithTypeInfo of(final O oid) {
 *				return new MyOIDWithTypeInfo(oid);
 *			}
 *			public static MyOIDWithTypeInfo valueOf(final String idWithType) {
 *				return new MyOIDWithTypeInfo(idWithType);
 *			}
 *			public static MyOIDWithTypeInfo forId(final String idWithType) {
 *				return new MyOIDWithTypeInfo(idWithType);
 *			}
 *		}
 * </pre>
 * @param <T>
 */
@Immutable
@MarshallPolymorphicTypeInfo
@Accessors(prefix="_")
public abstract class OIDWithTypeInfo<T>
  		   implements OID {
	private static final long serialVersionUID = -5958498889278418170L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Encodes the id alongside with the type as {type}:{id}
	 */
	@Getter private final String _idWithTypeInfo;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	protected OIDWithTypeInfo(final String idWithTypeInfo) {
		_idWithTypeInfo = idWithTypeInfo;
	}
	protected OIDWithTypeInfo(final String oidType,final T id) {
		this(_encode(oidType,id.toString()));
	}
	protected OIDWithTypeInfo(final Class<? extends OID> oidType,final T id) {
		this(_encode(oidType,id.toString()));
	}
	protected <O extends OID> OIDWithTypeInfo(final O oid) {
		_idWithTypeInfo = _encode(oid);
	}
	private static <O extends OID> String _encode(final O oid) {
		if (oid == null) throw new IllegalArgumentException("oid cannot be null!");
		return _encode(oid.getClass().getName(),oid.toString());
	}
	private static <T> String _encode(final Class<? extends OID> oidType,final String id) {
		return Strings.customized("{}:{}",
								  oidType.getName(),id.toString());
	}
	private static <T> String _encode(final String oidTypeStr,final String id) {
		return Strings.customized("{}:{}",
								  oidTypeStr,id.toString());
	}
//	public static <O extends OID> OIDWithTypeInfo of(final O oid) {
//		return new OIDWithTypeInfo(oid);
//	}
//	public static OIDWithTypeInfo from(final String oidType,final String id) {
//
//	}
//	public static OIDWithTypeInfo from(final Class<? extends OID> oid,final String id) {
//
//	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SneakyThrows
	@SuppressWarnings("unchecked")
	public Class<? extends OID> getOIDType() {
		return (Class<? extends OID>)OIDWithTypeInfo.class.getClassLoader()
												  		  .loadClass(this.getOIDTypeAsString());
	}
	public String getOIDTypeAsString() {
		return StringSplitter.using(Splitter.on(':'))
			  				   			    .at(_idWithTypeInfo)
			  				   			    .group(1);
	}
	public String getId() {
		return StringSplitter.using(Splitter.on(':'))
		  				     .at(_idWithTypeInfo)
		  				     .group(2);
	}
	@SuppressWarnings("unchecked")
	public <O extends OID> O getOIDAs(final Class<O> oidType) {
		Class<O> theOidType = (Class<O>)this.getOIDType();
		String id = this.getId();
		return OIDFromString.createOIDFromString(theOidType,id);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return this.toString();
	}
	@Override
	public String toString() {
		return _idWithTypeInfo;
	}
	@Override
	public int compareTo(final OID o) {
		return this.getId().toString().compareTo(o.toString());
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_idWithTypeInfo);
	}
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof OIDWithTypeInfo) {
			@SuppressWarnings("rawtypes")
			OIDWithTypeInfo other = (OIDWithTypeInfo)obj;
			return other.getIdWithTypeInfo().equals(this.getIdWithTypeInfo());
		}
		return super.equals(obj);
	}
	@Override
	public <O extends OID> boolean is(final O other) {
		return this.equals(other);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VALID
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isValid() {
		return this.getIdWithTypeInfo() != null;
	}
}
