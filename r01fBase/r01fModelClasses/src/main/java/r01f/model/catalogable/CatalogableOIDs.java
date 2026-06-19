package r01f.model.catalogable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.guids.PersistableObjectOID;

/**
 * Catalog oids
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class CatalogableOIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static interface CatalogableObjectOID
				    extends OIDTyped<String> {
		// a marker interface for a catalogable object (ie a content)
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  pick
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface CatalogablePickOID
					extends PersistableObjectOID {
		/* a marker interface */
	}
	@Immutable
	public static abstract class CatalogablePickOIDBase
	              		 extends OIDBaseMutable<String> 	
					  implements CatalogablePickOID {
		private static final long serialVersionUID = 67911121894832419L;
		public CatalogablePickOIDBase() {
			/* default no args constructor for serialization purposes */
		}
		public CatalogablePickOIDBase(final String id) {
			super(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  picks
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface CatalogablePicksOID
					extends PersistableObjectOID {
		/* a marker interface */
	}
	@Immutable
	public static abstract class CatalogablePicksOIDBase
	              		 extends OIDBaseMutable<String> 	
					  implements CatalogablePicksOID {
		private static final long serialVersionUID = 67911121894832419L;
		public CatalogablePicksOIDBase() {
			/* default no args constructor for serialization purposes */
		}
		public CatalogablePicksOIDBase(final String id) {
			super(id);
		}
	}
}
