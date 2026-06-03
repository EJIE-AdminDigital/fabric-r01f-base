package r01f.model.services;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
public abstract class COREServiceMethodExecResultBase<T>
           implements COREServiceMethodExecResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The requested operation
	 */
	@MarshallField(as="calledMethod",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected COREServiceMethod _calledMethod;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public COREServiceMethodExecResultBase(final COREServiceMethod reqOp) {
		_calledMethod = reqOp;
	}
}
