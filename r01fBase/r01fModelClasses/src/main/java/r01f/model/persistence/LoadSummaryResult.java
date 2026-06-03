package r01f.model.persistence;

import r01f.model.ModelObject;
import r01f.model.SummarizedModelObject;
import r01f.model.services.COREServiceMethodExecResult;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshalTypeInfoIncludeCase;

@MarshallPolymorphicTypeInfo(includeTypeInfo = MarshalTypeInfoIncludeCase.ALWAYS)
public interface LoadSummaryResult<M extends ModelObject>
		 extends COREServiceMethodExecResult<SummarizedModelObject<M>> {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a {@link CRUDOK} instance
	 */
	public default LoadSummaryOK<M> asLoadSummaryOK() {
		if (this instanceof LoadSummaryError) throw new ClassCastException(this.getClass() + " cannot be cast to " + LoadSummaryOK.class);
		return (LoadSummaryOK<M>)this;
	}
	/**
	 * @return a {@link CRUDError} instance
	 */
	public default LoadSummaryError<M> asLoadSummaryError() {
		if (this instanceof LoadSummaryOK) throw new ClassCastException(this.getClass() + " cannot be cast to " + LoadSummaryError.class);
		return (LoadSummaryError<M>)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public default <S extends SummarizedModelObject<M>> S getSummaryOrNull() {
		if (this.hasFailed()
		 && this.asLoadSummaryError().wasBecauseClientRequestedEntityWasNOTFound()) return null;
		return (S)this.getOrThrow();
	}
	@SuppressWarnings("unchecked")
	public default <S extends SummarizedModelObject<M>> S getSummaryOrThrow() {
		return (S)this.getOrThrow();
	}
}
