package r01f.model.catalogable.geo;

import java.util.Collection;
import java.util.Set;

import r01f.model.catalogable.CatalogableIDs.CatalogableObjectTypeID;
import r01f.model.catalogable.CatalogableOIDs.CatalogableObjectOID;
import r01f.model.catalogable.geo.GeoCatalogableOIDs.CatalogableGeoPickOID;
import r01f.model.facets.ModelObjectFacet;


/**
 * Interface for objects that can be geographically cataloged
 */
public interface GeoCatalogableModelObject {
/////////////////////////////////////////////////////////////////////////////////////////
//  R01MHasCatalogableFacet
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface ContainsGeoPicksFacet 
					extends ModelObjectFacet {
		public GeoCatalogableModelObject asGeoCatalogable();
		
		public void setGeoPicks(Set<GeoPositionPick> picks);
		public Set<GeoPositionPick> getGeoPicks();
	}
	public static interface HasGeoCatalogableFacet 
					extends ContainsGeoPicksFacet {
		
		public CatalogableObjectOID getCatalogableObjectOid();
		public CatalogableObjectTypeID getCatalogableObjectType();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GEO PICKS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return all the current geo picks
	 */
	public Set<GeoPositionPick> getGeoPicks();
	/**
	 * Returns the geo pick with the provided oid
	 * @param pickOid 
	 * @return
	 */
	public GeoPositionPick getGeoPickWithOid(CatalogableGeoPickOID pickOid);
	/**
	 * Returns the geo picks with the provided pick oids
	 * @param oids 
	 * @return
	 */
	public Set<GeoPositionPick> getGeoPicksWithOids(Collection<CatalogableGeoPickOID> oids);
	/**
	 * Returns the geo picks with the provided pick oids
	 * @param oids
	 * @return
	 */
	public Set<GeoPositionPick> getGeoPicksWithOids(CatalogableGeoPickOID... oids);
	/**
	 * Checks if a geo pick is contained within the current geo picks
	 * @param pickOid
	 * @return
	 */
	public boolean containsGeoPickWithOid(CatalogableGeoPickOID pickOid);
	/**
	 * Adds a geo pick in the current label in structure picks
	 * @param picks
	 * @return
	 */
	public void addGeoPick(GeoPositionPick pick);
	/**
	 * Adds some current geo  picks
	 * @param picks
	 * @return
	 */
	public void addGeoPicks(Collection<GeoPositionPick> picks);
	/**
	 * Adds some current geo picks
	 * @param picks
	 * @return
	 */
	public void addGeoPicks(GeoPositionPick... picks);
	/**
	 * Removes geo picks from their oids
	 * NOTE: Usually geo pick oids are obtained using one of this type filter methods
	 * @param pickOids
	 * @return true if any pick was removed, false otherwise
	 */
	public boolean removeGeoPicks(Collection<CatalogableGeoPickOID> pickOids);
	/**
	 * Removes geo picks from their oids
	 * NOTE: Usually geo pick oids are obtained using one of this type filter methods
	 * @param pickOids
	 * @return true if any pick was removed, false otherwise
	 */
	public boolean removeGeoPicks(CatalogableGeoPickOID... pickOids);
}
