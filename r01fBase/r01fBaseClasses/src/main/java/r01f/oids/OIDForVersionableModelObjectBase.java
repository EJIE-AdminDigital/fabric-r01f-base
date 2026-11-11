package r01f.oids;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.util.types.Strings;

@Deprecated	// use records instead of this class
@Accessors(prefix="_")
@RequiredArgsConstructor
public abstract class OIDForVersionableModelObjectBase
  		   implements OIDForVersionableModelObject {

	private static final long serialVersionUID = -6745951154429443045L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Deprecated
	@Override 
	protected Object clone() throws CloneNotSupportedException {
		if (this.getVersionIndependentOid() == null || this.getVersion() == null) throw new IllegalStateException(Strings.customized("The oid of type {} has NO state",this.getClass()));
		VersionIndependentOID clonedVersionIndependentOid = ObjectUtils.clone(this.getVersionIndependentOid());
		VersionOID clonedVersionOid = ObjectUtils.clone(this.getVersion());
		Object outOid = _createVersionableOIDFromString(clonedVersionIndependentOid.getClass(),clonedVersionOid.getClass(),
														clonedVersionIndependentOid,clonedVersionOid);
		return outOid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Deprecated
	@Override
	public int compareTo(final OID o) {
		if (o == null) return -1;
		if (this.getVersionIndependentOid() == null) return 1;
		return this.toString().compareTo(o.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Deprecated
	@Override
	public int hashCode() {
		return Objects.hash(this.getVersionIndependentOid(),this.getVersion());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Deprecated
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof OIDForVersionableModelObjectBase otherVersionable) {
			boolean oidEquals = Objects.equals(otherVersionable.getVersionIndependentOid(),this.getVersionIndependentOid()); 
			boolean versionEquals = Objects.equals(otherVersionable.getVersion(),this.getVersion());
			return oidEquals && versionEquals;
		} 
		return false;
	}
	@Deprecated
	@Override
	public <O extends OID> boolean is(final O other) {
		return this.equals(other);
	}
	@Deprecated
	@Override
	public <O extends OID> boolean isNOT(final O other) {
		return !this.is(other);
	}
	@Deprecated
	@Override
	public <O extends OID> boolean isIgnoringCase(final O other) {
		if (this == other) return true;
		if (other == null) return false;
		if (other instanceof OIDForVersionableModelObjectBase otherVersionable) {
			boolean versionIndOidEquals = otherVersionable.getVersionIndependentOid().isIgnoringCase(this.getVersionIndependentOid()); 
			boolean versionEquals = otherVersionable.getVersion().isIgnoringCase(this.getVersion());
			return versionIndOidEquals && versionEquals;
		} 
		return false;
	}
	@Deprecated
	@Override @SuppressWarnings("unchecked")
	public <O extends OID> boolean isContainedIn(final O... oids) {
		return oids != null ? this.isContainedIn(Arrays.asList(oids))
							: false;
	}
	@Deprecated
	@Override @SuppressWarnings("unchecked")
	public <O extends OID> boolean isNOTContainedIn(final O... oids) {
		return !this.isContainedIn(oids);
	}
	@Deprecated
	@Override
	public <O extends OID> boolean isContainedIn(final Iterable<O> oids) {
		boolean outIsContained = false;
		if (oids != null) {
			for (O oid : oids) {
				if (oid.equals(this)) {
					outIsContained = true;
					break;
				}
			}
		}
		return outIsContained;
	}
	@Deprecated
	@Override
	public <O extends OID> boolean isNOTContainedIn(final Iterable<O> oids) {
		return !this.isContainedIn(oids);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Deprecated
	@Override @SuppressWarnings("unchecked")
	public <O extends OID> O cast() {
		return (O)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Deprecated
	@Override
	public String toString() {
		String outStr = null;
		if ((this.getVersionIndependentOid() == null) || (this.getVersion() == null)) {
			throw new IllegalStateException(Strings.customized("A {} without oid (only oid or version is NOT allowed",this.getClass()));
		}
		outStr = Strings.customized("{}/{}",this.getVersionIndependentOid(),this.getVersion()); 
		return outStr;
	}
	@Deprecated
	@Override
	public String asString() {
		return this.toString();
	}
	@Deprecated
	public String memoCode() {
		return this.toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Deprecated
	@Override
	public boolean isValid() {
		return this.getVersionIndependentOid() != null && this.getVersionIndependentOid().isValid() 
			&& this.getVersion() != null && this.getVersion().isValid();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings("unchecked")
	private <O extends OIDForVersionableModelObject> O _createVersionableOIDFromString(final Class<? extends VersionIndependentOID> versionIndependentOidType,final Class<? extends VersionOID> versionOidType,
												   		  	  						   final VersionIndependentOID versionIndependentOid,final VersionOID versionOid) {
		if (versionIndependentOid == null
		 || versionOid == null) throw new IllegalArgumentException("an OIDForVersionableModelObject cannot have a null VersionIndependentOID nor a null VersionOID");
		// find the constructor
		Constructor<?> constructor = null;
		try {
			constructor = this.getClass()
							  .getDeclaredConstructor(new Class<?>[] {versionIndependentOidType,versionOidType}); 	// Constructor
		} catch (NoSuchMethodException nsmEx) {
			throw new IllegalArgumentException(Strings.customized("Type {} is NOT a valid {}: it does NOT have a {}({},{}) constructor",
															 	  this.getClass(),OIDForVersionableModelObject.class.getSimpleName(),
															 	  this.getClass().getSimpleName(),VersionIndependentOID.class,VersionOID.class));			
		}
		// create the oid
		O outOid = null;
		try {
			outOid = (O)constructor.newInstance(new Object[] {versionIndependentOid,versionOid});
		} catch (Throwable th) {
			th.printStackTrace();
			throw new IllegalStateException(Strings.customized("Could NOT create a {} instance using the {}({},{}) constructor: {}",
														  	   this.getClass(),
														  	   this.getClass().getSimpleName(),VersionIndependentOID.class,VersionOID.class,
														  	   th.getMessage()),
											th);
		}
		return outOid;
	}
}
