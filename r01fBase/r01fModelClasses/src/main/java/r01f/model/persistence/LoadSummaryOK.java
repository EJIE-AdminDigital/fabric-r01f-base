package r01f.model.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.ModelObject;
import r01f.model.SummarizedModelObject;
import r01f.model.services.COREServiceMethod;
import r01f.model.services.COREServiceMethodExecOK;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;

@MarshallType(as="crudResult",typeId="ok")
@Accessors(prefix="_")
public class LoadSummaryOK<M extends ModelObject>
	 extends COREServiceMethodExecOK<SummarizedModelObject<M>>
  implements LoadSummaryResult<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * The type of the entity subject of the requested operation 
	 */
	@MarshallField(as="type",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Class<M> _objectType;		
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public LoadSummaryOK() {
		super(COREServiceMethod.named("loadSummary"));
	}
	public LoadSummaryOK(final Class<M> entityType) {
		super(COREServiceMethod.named("loadSummary"));
		_objectType = entityType;
	}
	public <S extends SummarizedModelObject<M>> LoadSummaryOK(final Class<M> entityType,
				  								final S result) {
		super(COREServiceMethod.named("loadSummary"),
			  result);
		_objectType = entityType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("Load symmary requested on entity of type {}",
								  _objectType);
	}
}
