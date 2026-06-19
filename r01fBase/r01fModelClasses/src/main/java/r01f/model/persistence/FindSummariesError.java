package r01f.model.persistence;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.ModelObject;
import r01f.model.SummarizedModelObject;
import r01f.model.services.COREServiceErrorType;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="findResult",typeId="findSummariesError")
@Accessors(prefix="_")
@SuppressWarnings("unchecked")
public class FindSummariesError<M extends ModelObject>
	 extends PersistenceOperationExecError<Collection<? extends SummarizedModelObject<M>>>
  implements FindSummariesResult<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Info about the model object
	 * beware that the {@link PersistenceOperationOnObjectOK} wraps a {@link Collection}
	 * of model objects
	 */
	@MarshallField(as="modelObjectType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Class<M> _modelObjectType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FindSummariesError() {
		super(PersistenceRequestedOperation.FIND);
	}
	FindSummariesError(final Class<M> entityType,
			  		   final Throwable th) {
		super(PersistenceRequestedOperation.FIND,
			  th);
		_modelObjectType = entityType;
	}
	FindSummariesError(final Class<M> entityType,
					   final COREServiceErrorType errorType,
			  		   final Throwable th) {
		super(PersistenceRequestedOperation.FIND,
			  errorType,
			  th);
		_modelObjectType = entityType;
	}
	FindSummariesError(final Class<M> entityType,
			  		   final String errMsg) {
		super(PersistenceRequestedOperation.FIND,
			  errMsg);
		_modelObjectType = entityType;
	}
	FindSummariesError(final Class<M> entityType,
					   final COREServiceErrorType errorType,
			  		   final String errMsg) {
		super(PersistenceRequestedOperation.FIND,
			  errorType,
			  errMsg);
		_modelObjectType = entityType;
	}
}



