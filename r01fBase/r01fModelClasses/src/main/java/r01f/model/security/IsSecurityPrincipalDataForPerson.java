package r01f.model.security;

import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;

@MarshallPolymorphicTypeInfo
public interface IsSecurityPrincipalDataForPerson 
		 extends IsSecurityObject {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public default <D extends IsSecurityPrincipalDataForPerson> D as(final Class<D> type) {
		return type.cast(this);
	}
}
