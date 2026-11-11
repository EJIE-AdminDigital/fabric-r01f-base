package r01f.model;

import r01f.facets.Facetable;
import r01f.model.facets.HasEntityVersion;
import r01f.model.facets.HasNumericID;




/**
 * interface for objects that can be persisted in some kind of storage
 */
public interface PersistableObject
		 extends ModelObject,
         		 Facetable, 				// can have facets
 		 	     HasNumericID,				// all persistable model objects MUST have a numeric oid
 		 	     HasEntityVersion,			// all persistable model objects are persisted assuming persistence conflicts are unlike to happen (optimistic locking)
 		 	     HasTrackableFacet {		// all persistable model object has create / update dates and creator / last updator user code
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
}
