package r01f.oids.refs;

import r01f.facets.HasOID;
import r01f.oids.PersistableObjectOID;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Objects;

public interface IsPersistableObjectRef<O extends PersistableObjectOID>
		 extends HasOID<O>,
		 		 CanBeRepresentedAsString {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public default boolean is(final IsPersistableObjectRef<O> other) {
		if (this == other) return true;
		if (other == null) return false;
		if (this.getClass() != other.getClass()) return false;
		return Objects.areEqual(this.getOid(),other.getOid(),
								(oid1,oid2) -> oid1.is(oid2));
	}
	public default boolean isNOT(final IsPersistableObjectRef<O> other) {
		return !this.is(other);
	}
	public default boolean matchesOid(final O oid) {
		return this.getOid() != null 
			&& this.getOid().is(oid);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FACTORY	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * A factory to create refs to DENA objects from their OID
	 * @param <O>
	 * @param <R>
	 */
	public interface PersistableObjectRefFactory<O extends PersistableObjectOID,R extends IsPersistableObjectRef<O>> {
		public R createRefFrom(final O oid);
	}
}
