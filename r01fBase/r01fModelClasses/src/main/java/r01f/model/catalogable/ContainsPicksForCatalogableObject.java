package r01f.model.catalogable;

import java.util.Set;

import r01f.model.catalogable.CatalogableIDs.CatalogableObjectTypeID;
import r01f.model.catalogable.CatalogableOIDs.CatalogableObjectOID;

public interface ContainsPicksForCatalogableObject<P> {
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////	
	public CatalogableObjectOID getCatalogableObjectOid();
	public void setCatalogableObjectOid(final CatalogableObjectOID oid);
	
	public CatalogableObjectTypeID getCatalogableObjectType();
	public void setCatalogableObjectType(final CatalogableObjectTypeID objType);
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Set<P> getPicks();
	public void setPicks(final Set<P> picks);
	
	public void addPick(P pick);
}
