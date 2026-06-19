package r01f.model.persistence;

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.ModelObject;
import r01f.model.SummarizedModelObject;
import r01f.model.services.COREServiceErrorType;
import r01f.model.services.COREServiceMethod;
import r01f.model.services.COREServiceMethodExecError;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="crudResult",typeId="error")
@Accessors(prefix="_")
public class LoadSummaryError<M extends ModelObject>
	 extends COREServiceMethodExecError<SummarizedModelObject<M>>
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
	/**
	 * Some data about the requested operation target entity such as it's oid
	 */
	@MarshallField(as="requestedOperationTarget")
	@Getter @Setter protected Map<String,String> _requestedOperationTargetEntityIdInfo;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public LoadSummaryError() {
		super(COREServiceMethod.named("loadSummary"));
	}
	public LoadSummaryError(final Class<M> entityType,
							final Throwable th) {
		super(COREServiceMethod.named("loadSummary"),
			  th);	
		_objectType = entityType;
	}
	public LoadSummaryError(final Class<M> entityType,
							final COREServiceErrorType errType,
			  		 		final Throwable th) {
		super(COREServiceMethod.named("loadSummary"),
			  errType,
			  th);
		_objectType = entityType;
	}
	public LoadSummaryError(final Class<M> entityType,
							final String errMsg) {
		super(COREServiceMethod.named("loadSummary"),
			  errMsg);
		_objectType = entityType;
	}
	public LoadSummaryError(final Class<M> entityType,
							final COREServiceErrorType errType,
			  		 		final String errMsg) {
		super(COREServiceMethod.named("loadSummary"),
			  errType,
			  errMsg);
		_objectType = entityType;
	}
	public LoadSummaryError(final Class<M> entityType,
							final COREServiceMethodExecError<?> otherError) {
		super(COREServiceMethod.named("loadSummary"),
			  otherError);
		_objectType = entityType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean wasBecauseClientRequestedEntityWasNOTFound() {
		return _errorType.is(PersistenceServiceErrorTypes.ENTITY_NOT_FOUND);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public void addTargetEntityIdInfo(final String field,final String value) {
		if (_requestedOperationTargetEntityIdInfo == null) _requestedOperationTargetEntityIdInfo = Maps.newHashMap();
		_requestedOperationTargetEntityIdInfo.put(field,value);
	}
	/**
	 * @return any info about the target entity such as it's oid 
	 */
	public String getTargetEntityIdInfo() {
		String outIdInfo = null;
		if (CollectionUtils.hasData(_requestedOperationTargetEntityIdInfo)) {
			StringBuilder sb = new StringBuilder();
			for (Iterator<Map.Entry<String,String>> meIt =_requestedOperationTargetEntityIdInfo.entrySet().iterator(); meIt.hasNext(); ) {
				Map.Entry<String,String> me = meIt.next();
				sb.append(me.getKey())
				  .append("=")
				  .append(me.getValue());
				if (meIt.hasNext()) sb.append(", ");
			}
			outIdInfo = sb.toString();
		}
		return outIdInfo != null ? outIdInfo : "unknown";
	}
}
