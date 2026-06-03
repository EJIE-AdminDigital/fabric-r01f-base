package r01f.model.catalogable.geo;

import java.util.Set;

import r01f.model.catalogable.CatalogablePicksChangesBase;
import r01f.types.geo.GeoPositionBase;

/**
 * Utility type to compute the differences between two {@link GeoPositionPick} sets
 */
public class GeoPositionPicksChanges 
	 extends CatalogablePicksChangesBase<GeoPositionPick> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPositionPicksChanges(final Set<GeoPositionPick> currentPickSet,final Set<GeoPositionPick> receivedPicksSet) {
		super(currentPickSet,receivedPicksSet,
			  GeoPositionBase.LOOSE_COMPARATOR);
	}
}
