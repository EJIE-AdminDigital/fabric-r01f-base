package r01f.oids.refs;

import r01f.facets.HasID;
import r01f.oids.PersistableObjectID;
import r01f.oids.PersistableObjectOID;
import r01f.util.types.Objects;

public interface IsPersistableObjectWithIDRef<O extends PersistableObjectOID,I extends PersistableObjectID<O>> 
		 extends IsPersistableObjectRef<O>,
		 		 HasID<I> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public default boolean is(final IsPersistableObjectWithIDRef<O,I> other) {
		if (this == other) return true;
		if (other == null) return false;
		if (this.getClass() != other.getClass()) return false;
		
		boolean oidEqs = Objects.areEqual(this.getOid(),other.getOid(),
										  (oid1,oid2) -> oid1.is(oid2));
		boolean idEqs = Objects.areEqual(this.getId(),other.getId(),
										 (id1,id2) -> id1.is(id2));
		return oidEqs && idEqs;
	}
	public default boolean isNOT(final IsPersistableObjectWithIDRef<O,I> other) {
		return !this.is(other);
	}
	public default boolean matchesId(final I id) {
		return this.getId() != null 
			&& this.getId().is(id);
	}
	public default boolean matchesOidOrId(final IsPersistableObjectWithIDRef<O,I> other) {
		if (this == other) return true;
		if (other == null) return false;
		if (this.getClass() != other.getClass()) return false;
		
		if (this.getOid() != null && other.getOid() != null) return this.getOid().is(other.getOid());
		if (this.getId() != null && other.getId() != null) return this.getId().is(other.getId());
		return false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public default boolean containsBothOidAndId() {
		return this.getOid() != null 
			&& this.getId() != null;
	}
	public default boolean containsJustOneOfOidOrId() {
		return (this.getOid() != null && this.getId() == null) 
			|| (this.getOid() == null && this.getId() != null);
	}
	public default boolean containsAnyOidOrId() {
		return this.getOid() != null 
			|| this.getId() != null;
	}
	public default boolean containsNeitherOidNorId() {
		return this.getOid() == null 
			&& this.getId() == null;
	}	
	public default boolean containsOid() {
		return this.getOid() != null; 		
	}
	public default boolean containsId() {
		return this.getId() != null; 		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FACTORY	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * A factory to create refs to DENA objects from their OID
	 * @param <O>
	 * @param <I>
	 * @param <R>
	 */
	public interface PersistableObjectWithIDRefFactory<O extends PersistableObjectOID,I extends PersistableObjectID<O>,R extends IsPersistableObjectWithIDRef<O,I>> {
		public R createRefFrom(final O oid,final I id);
	}
}
