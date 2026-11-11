package r01f.model.security;

import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;

@MarshallPolymorphicTypeInfo
public interface IsLoginData
		 extends IsSecurityObject {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings("unchecked")
	public default <D extends IsLoginData> D as(final Class<D> type) {
		return (D)this;
	}
}
