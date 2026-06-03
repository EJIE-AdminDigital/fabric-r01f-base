package r01f.model.catalogable.geo.builders;

import java.util.Set;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.model.catalogable.CatalogableIDs.CatalogableObjectTypeID;
import r01f.model.catalogable.CatalogableOIDs.CatalogableObjectOID;
import r01f.model.catalogable.geo.CatalogableGeoPicks;
import r01f.model.catalogable.geo.GeoCatalogableOIDs.CatalogableGeoPickOID;
import r01f.model.catalogable.geo.GeoCatalogableOIDs.CatalogableGeoPicksOID;
import r01f.model.catalogable.geo.GeoPositionPick;
import r01f.patterns.IsBuilder;
import r01f.util.types.collections.CollectionUtils;

/**
 * {@link GeoPositionPick} builder
 * Usage
 * <pre class='brush:java'>
 * 		// current picks
 *		CatalogableGeoPicks picks = CatalogableGeoPicksBuilder.create()
 *											.forCatalogableObjectWithOid(R01MContentOID.forId("myContOid"))
 *											.ofType(CatalogableObjectTypeID.forId("content"))
 *											.geoPick(...)
 *											.build();
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class CatalogableGeoPicksBuilder 
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
// 	OID
/////////////////////////////////////////////////////////////////////////////////////////
	public static CatalogableGeoPicksBuilderObjectOidStep withSnapshotOid(final CatalogableGeoPicksOID oid) {
		CatalogableGeoPicks picks = new CatalogableGeoPicks();
		picks.setOid(oid);
		return new CatalogableGeoPicksBuilder() { /* nothing */ }
						.new CatalogableGeoPicksBuilderObjectOidStep(picks);
	}
	public static CatalogableGeoPicksBuilderObjectOidStep create() {
		return CatalogableGeoPicksBuilder.withSnapshotOid(CatalogableGeoPicksOID.supply());
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	CATALOGABLE OBJECT
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class CatalogableGeoPicksBuilderObjectOidStep {
		private final CatalogableGeoPicks _picks;
		public CatalogableGeoPicksBuilderObjectTypeStep forCatalogableObjectWithOid(final CatalogableObjectOID catalogableObjOid) {
			_picks.setCatalogableObjectOid(catalogableObjOid);
			return new CatalogableGeoPicksBuilder() { /* nothing */ }
							.new CatalogableGeoPicksBuilderObjectTypeStep(_picks);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class CatalogableGeoPicksBuilderObjectTypeStep {
		private final CatalogableGeoPicks _picks;
		public CatalogableGeoPicksBuilderPicksStep ofType(final CatalogableObjectTypeID catalogableObjType) {
			_picks.setCatalogableObjectType(catalogableObjType);
			return new CatalogableGeoPicksBuilder() { /* nothing */ }
							.new CatalogableGeoPicksBuilderPicksStep(_picks);
		}
		public <E extends Enum<E>> CatalogableGeoPicksBuilderPicksStep ofType(final E en) {
			return this.ofType(CatalogableObjectTypeID.from(en));
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	PICKS
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class CatalogableGeoPicksBuilderPicksStep {
		private final CatalogableGeoPicks _picks;
		
		public CatalogableGeoPicksBuilderPicksStep geoPick(final GeoPositionPick pick) {
			if (pick != null) {
				if (pick.getOid() == null) pick.setOid(CatalogableGeoPickOID.supply());
				_picks.addPick(pick);
			}
			return this;
		}
		public CatalogableGeoPicksBuilderPicksStep geoPicks(final Set<GeoPositionPick> picks) {
			if (CollectionUtils.hasData(picks)) picks.stream()
													 .forEach(pick -> {
														 			if (pick.getOid() == null) pick.setOid(CatalogableGeoPickOID.supply());
													 		  });
			_picks.setPicks(picks);
			return this;
		}
		public CatalogableGeoPicks build() {
			return _picks;
		}
	}
}
