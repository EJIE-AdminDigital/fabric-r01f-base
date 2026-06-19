package r01f.model.persistence;

import java.util.Collection;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.ModelObject;
import r01f.model.SummarizedModelObject;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="findResult",typeId="findSummariesOK")
@Accessors(prefix="_")
public class FindSummariesOK<M extends ModelObject>
	 extends PersistenceOperationExecOK<Collection<? extends SummarizedModelObject<M>>>
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
	public FindSummariesOK() {
		super(PersistenceRequestedOperation.FIND,PersistencePerformedOperation.FOUND);
	}
	protected FindSummariesOK(final Class<M> entityType) {
		this();
		_modelObjectType = entityType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public <S extends SummarizedModelObject<M>> Stream<S> stream() throws Throwable {
		Collection<S> col = this.getSummariesOrThrow();
		return col != null ? col.stream()
						   : Stream.empty();
	}
}
