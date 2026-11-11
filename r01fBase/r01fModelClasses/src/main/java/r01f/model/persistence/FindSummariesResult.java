package r01f.model.persistence;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import r01f.exceptions.Throwables;
import r01f.facets.HasOID;
import r01f.model.HasModelObjectTypeInfo;
import r01f.model.ModelObject;
import r01f.model.SummarizedModelObject;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.oids.OID;
import r01f.util.types.collections.CollectionUtils;

@MarshallPolymorphicTypeInfo
public interface FindSummariesResult<M extends ModelObject>
       	 extends PersistenceOperationOnObjectResult<Collection<? extends SummarizedModelObject<M>>>,
       	 		 HasModelObjectTypeInfo<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings({ "unchecked" })
	public default Class<Collection<? extends SummarizedModelObject<M>>> getObjectType() {
		return (Class<Collection<? extends SummarizedModelObject<M>>>)new TypeToken<Class<Collection<? extends SummarizedModelObject<M>>>>() { /* nothing */ }
																			.getComponentType()
																			.getRawType();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a {@link FindSummariesOK}
	 */
	public default FindSummariesOK<M> asFindSummariesOK() {
		if (this instanceof FindSummariesError<M>) throw new ClassCastException(this.getClass() + " cannot be cast to " + FindSummariesOK.class);
		return (FindSummariesOK<M>)this;
	}
	/**
	 * @return a {@link FindSummariesError}
	 */
	public default FindSummariesError<M> asFindSummariesError() {
		if (this instanceof FindSummariesOK<M>) throw new ClassCastException(this.getClass() + " cannot be cast to " + FindSummariesError.class);
		return (FindSummariesError<M>)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @param <S>
	 * @return the summaries
	 * @throws Throwable 
	 */
	@SuppressWarnings("unchecked")
	public default <S extends SummarizedModelObject<M>> Collection<S> getSummariesOrThrow() {
		if (this instanceof FindSummariesError<M> err) {
			throw err.getPersistenceException();
		} else if (this instanceof FindSummariesOK<M> ok) {
			return (Collection<S>)ok.getMethodExecResult();
		} else {
			throw new IllegalStateException("Unknown state");
		}
	}
	/**
	 * @param <S>
	 * @return
	 * @throws Throwable 
	 */
	public default <S extends SummarizedModelObject<M>> Collection<S> getSummariesOrNull() {
		if (this.hasFailed()
		 && this.asCOREServiceMethodExecError().wasBecauseAClientError()) return null;
		return this.getSummariesOrThrow();
	}
	/**
	 * @return the found entities' oids if the persistence find operation was successful or a PersistenteException if not
	 * @throws Throwable 
	 */
	@SuppressWarnings("unchecked")
	public default <O extends OID> Collection<O> getOidsOrThrow() {
		if (this instanceof FindSummariesError<M> err) {
			throw err.getPersistenceException();
		} else if (this instanceof FindSummariesOK<M> ok) {
			if (CollectionUtils.isNullOrEmpty(ok.getMethodExecResult())) return Lists.newArrayList();
			return ok.getMethodExecResult()
					 .stream()
					 .map(entitySummary -> {
								if (entitySummary instanceof HasOID) return ((HasOID<O>)entitySummary).getOid();
								throw new IllegalStateException(Throwables.message("The entity of type {} does NOT implements {}",
																				   entitySummary.getModelObjectType(),HasOID.class));
						 })
					 .collect(Collectors.toList());
		} else {
			throw new IllegalStateException("Unknown state");
		}
	}
	/**
	 * When a single result is expected, this method returns this entity's oid
	 * @return
	 * @throws Throwable 
	 */
	@SuppressWarnings({ "unchecked","unused" })
	public default <O extends OID> O getSingleExpectedOidOrThrow() {
		if (this instanceof FindSummariesError<M> err) {
			throw err.getPersistenceException();
		} else if (this instanceof FindSummariesOK<M> ok) {
			SummarizedModelObject<M> outEntitySummary = this.getSingleExpectedOrThrow();
			if (outEntitySummary != null) {
				if (outEntitySummary instanceof HasOID) return ((HasOID<O>)outEntitySummary).getOid();
				throw new IllegalStateException(Throwables.message("The entity of type {} does NOT implements {}",
																   outEntitySummary.getModelObjectType(),HasOID.class));	
			} else {
				return null;
			}
		} else {
			throw new IllegalStateException("Unknown state");
		}
	}
	/**
	 * When a single result is expected, this method returns this entity
	 * @return
	 * @throws Throwable 
	 */
	@SuppressWarnings("unchecked")
	public default <S extends SummarizedModelObject<M>> S getSingleExpectedOrThrow() {
		if (this instanceof FindSummariesError<M> err) {
			throw err.getPersistenceException();
		} else if (this instanceof FindSummariesOK<M> ok) {
			S outEntitySummary = null;
			Collection<S> entities = (Collection<S>)ok.getMethodExecResult();
			if (CollectionUtils.hasData(entities)) {
				outEntitySummary = CollectionUtils.of(entities)
												  .pickOneAndOnlyElement("A single instance of {} was expected to be found BUT {} were found",
														  				 SummarizedModelObject.class,
														  				 entities.size());
			} 
			return outEntitySummary;
		} else {
			throw new IllegalStateException("Unknown state");
		}
	}
}
