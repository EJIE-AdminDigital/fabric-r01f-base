package r01f.model.services;

import r01f.guids.OIDBaseMutable;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;

/**
 * An id of the called [core service] method
 */
@MarshallType(as="coreServiceMethod")
public class COREServiceMethod 
     extends OIDBaseMutable<String> {
	
	private static final long serialVersionUID = -7020929167799107328L;

/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public static COREServiceMethod UNKNOWN = COREServiceMethod.named("unknown");
	public COREServiceMethod() {
		super();
	}
	public COREServiceMethod(final String oid) {
		super(oid);
	}
	public static COREServiceMethod valueOf(final String s) {
		return COREServiceMethod.named(s);
	}
	public static COREServiceMethod fromString(final String s) {
		return COREServiceMethod.named(s);
	}
	public static COREServiceMethod named(final String id) {
		return new COREServiceMethod(id);
	}
	public static COREServiceMethod current() {
		return new COREServiceMethod(StackWalker.getInstance()
												.walk(frames -> frames.findFirst()
																	  .map(StackWalker.StackFrame::getMethodName))
												.orElse("unknown"));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final PersistenceRequestedOperation op) {
		return op.is(this);
	}
	public boolean isNOT(final PersistenceRequestedOperation op) {
		return op.isNOT(this);
	}
	public boolean is(final PersistencePerformedOperation op) {
		return op.is(this);
	}
	public boolean isIn(final PersistencePerformedOperation... ops) {
		boolean isIn = false;
		for (PersistencePerformedOperation currOp : ops) {
			if (this.is(currOp)) {
				isIn = true;
				break;
			}
		}		
		return isIn;
	}
	public boolean isNOTIn(final PersistencePerformedOperation... ops) {
		boolean isNOTIn = true;
		for (PersistencePerformedOperation currOp : ops) {
			if (this.is(currOp)) {
				isNOTIn = false;
				break;
			}
		}		
		return isNOTIn;
	}
	public boolean isIn(final COREServiceMethod... methods) {
		if (CollectionUtils.isNullOrEmpty(methods)) return false;
		boolean outIn = false;
		for (COREServiceMethod m : methods) {
			if (m.is(this)) {
				outIn = true;
				break;
			}
		}
		return outIn;
	}
}