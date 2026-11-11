package r01f.model.persistence;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import lombok.experimental.Accessors;
import r01f.model.PersistableModelObject;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.OID;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="findResult",typeId="findOnModelObjectOK")
@Accessors(prefix="_")
public class FindOnModelObjectOK<M extends PersistableModelObject<? extends OID>>
	 extends FindOK<M>
  implements FindOnModelObjectResult<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FindOnModelObjectOK() {
		super();
	}
	protected FindOnModelObjectOK(final Class<M> entityType) {
		super(entityType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Class<M> getModelObjectType() {
		return _foundObjectType;
	}
	@Override public void setModelObjectType(final Class<M> type) {
		_foundObjectType = type;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the found entities' oids if the persistence find operation was successful or a PersistenteException if not
	 * @throws PersistenceException
	 */
	@SuppressWarnings("unchecked")
	public <O extends OID> Collection<O> getOidsOrThrow() throws PersistenceException {
		return CollectionUtils.isNullOrEmpty(_methodExecResult)
					? Lists.newArrayList()
					: _methodExecResult.stream()
									   .map(entity -> (O)entity.getOid())
									   .collect(Collectors.toList());
	}
	/**
	 * When a single result is expected, this method returns this entity's oid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <O extends OID> O getSingleExpectedOidOrThrow() {
		M outEntity = this.getSingleExpectedOrThrow();
		return (O)(outEntity != null ? outEntity.getOid()
								 	 : null);
	}
}
