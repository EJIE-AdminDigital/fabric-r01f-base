package r01f.model.catalogable.geo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import r01f.facets.delegates.FacetDelegateBase;
import r01f.model.catalogable.geo.GeoCatalogableModelObject.ContainsGeoPicksFacet;
import r01f.model.catalogable.geo.GeoCatalogableOIDs.CatalogableGeoPickOID;
import r01f.util.types.collections.CollectionUtils;

/**
 * Delegate type for {@link R01MStructureCatalogableModelObject} 
 * @param <M>
 */
public class GeoCatalogableDelegate<M extends ContainsGeoPicksFacet> 
	 extends FacetDelegateBase<M> 	
  implements GeoCatalogableModelObject {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoCatalogableDelegate(final M hasCatalogableFacet) {
		super(hasCatalogableFacet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Set<GeoPositionPick> getGeoPicks() {
		return _modelObject.getGeoPicks();
	}
	@Override
	public GeoPositionPick getGeoPickWithOid(final CatalogableGeoPickOID pickOid) {
		Set<GeoPositionPick> picksOfLabel = this.getGeoPicksWithOids(pickOid);
		return CollectionUtils.of(picksOfLabel)
							  .pickOneAndOnlyElementOrNull();
	}
	@Override
	public Set<GeoPositionPick> getGeoPicksWithOids(final CatalogableGeoPickOID... oids) {
		if (CollectionUtils.isNullOrEmpty(oids)) return null;
		return this.getGeoPicksWithOids(CollectionUtils.of(oids).asCollection());
	}
	@Override
	public Set<GeoPositionPick> getGeoPicksWithOids(final Collection<CatalogableGeoPickOID> oids) {
		if (CollectionUtils.isNullOrEmpty(this.getGeoPicks()) || CollectionUtils.isNullOrEmpty(oids)) return null;
		Predicate<GeoPositionPick> filter = pick -> pick.getOid() != null ? oids.contains(pick.getOid())
							  		 									  : false;
		Set<GeoPositionPick> picksOfLabel = this.getGeoPicks()
												.stream()
												.filter(filter)
												.collect(Collectors.toSet());
		return picksOfLabel;
	}
	@Override
	public void addGeoPick(final GeoPositionPick pick) {
		if (_modelObject.getGeoPicks() == null) _modelObject.setGeoPicks(Sets.<GeoPositionPick>newLinkedHashSet());
		if (pick != null && !this.containsGeoPickWithOid(pick.getOid())) {
			this.getGeoPicks().add(pick);
		}
	}
	@Override
	public void addGeoPicks(final Collection<GeoPositionPick> picks) {
    	if (CollectionUtils.hasData(picks)) {
    		for (GeoPositionPick pick : picks) this.addGeoPick(pick);
    	}
	}
	@Override
	public void addGeoPicks(final GeoPositionPick... picks) {
    	if (CollectionUtils.hasData(picks)) {
    		Collection<GeoPositionPick> picksCol = Arrays.asList(picks);
    		this.addGeoPicks(picksCol);
    	}
	}
	@Override
	public boolean removeGeoPicks(final Collection<CatalogableGeoPickOID> pickOids) {
		Set<GeoPositionPick> picksWithOids = this.getGeoPicksWithOids(pickOids);
		boolean anyRemoved = false;
		if (!CollectionUtils.isNullOrEmpty(picksWithOids)) {
			for (GeoPositionPick pick : picksWithOids) {
				anyRemoved = anyRemoved | this.getGeoPicks().remove(pick);		// remove
			}
		}
		return anyRemoved;
	}
	@Override
	public boolean removeGeoPicks(final CatalogableGeoPickOID... pickOids) {
		if (CollectionUtils.isNullOrEmpty(pickOids)) return false;
		return this.removeGeoPicks(CollectionUtils.of(pickOids).asCollection());
	}
	@Override
	public boolean containsGeoPickWithOid(final CatalogableGeoPickOID pickOid) {
		return this.getGeoPickWithOid(pickOid) != null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public static Set<GeoPositionPick> addAll(final Set<GeoPositionPick>... picks) {
		if (CollectionUtils.isNullOrEmpty(picks)) return null;
		
		int size = 0;
		for (Set<GeoPositionPick> col : picks) {
			if (col != null) size = size + col.size();
		}
		
		Set<GeoPositionPick> outPicks = Sets.newLinkedHashSetWithExpectedSize(size);
		for (Set<GeoPositionPick> col : picks) {
			if (col != null) outPicks.addAll(col);
		} 
		return outPicks;
	}
}
