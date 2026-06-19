package r01f.model.persistence;

import r01f.enums.EnumExtended;
import r01f.httpclient.HttpResponseCode;
import r01f.model.services.COREServiceMethod;

/**
 * A performed persistence-related operation
 * Note that the performed operation is NOT always the same as the requested one
 * (ie: an update could be requested by the client BUT the record didn't exist so a creation is performed)
 */
public enum PersistencePerformedOperation
 implements EnumExtended<PersistencePerformedOperation> {
	EXISTS,
	LOADED,
	CREATED,
	UPDATED,
	DELETED,
	NOT_MODIFIED,
	COUNTED,
	FOUND;

	public COREServiceMethod getCOREServiceMethod() {
		return COREServiceMethod.named(this.name());
	}
	public COREServiceMethod asCOREServiceMethod() {
		return this.getCOREServiceMethod();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final COREServiceMethod method) {
		return this.getCOREServiceMethod().is(method);
	}
	public boolean isIn(final COREServiceMethod... coreServiceMethods) {
		boolean isIn = false;
		for (COREServiceMethod currM : coreServiceMethods) {
			if (this.is(currM)) {
				isIn = true;
				break;
			}
		}		
		return isIn;
	}
	public boolean isNOTIn(final COREServiceMethod... coreServiceMethods) {
		boolean isNOTIn = true;
		for (COREServiceMethod currM : coreServiceMethods) {
			if (this.is(currM)) {
				isNOTIn = false;
				break;
			}
		}		
		return isNOTIn;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static PersistencePerformedOperation fromName(final String name) {
		return EnumExtended.fromName(name,PersistencePerformedOperation.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + PersistencePerformedOperation.class + " element with name=" + name));
	}
	public static PersistencePerformedOperation named(final String name) {
		return PersistencePerformedOperation.fromName(name);
	}
	public static PersistencePerformedOperation from(final COREServiceMethod method) {
		return PersistencePerformedOperation.fromName(method.asString());
	}
	/**
	 * Guess the supposed performed operation based on the requested one
	 * Usually the performed operation depends on the requested one, BUT some times, it
	 * depends on the server data, ie, if the client request a CREATION but the entity
	 * already exists at the server, the performed operation can be UPDATED instead of the
	 * supposed one (CREATED)
	 * @param requestedOp
	 * @return
	 */
	public static PersistencePerformedOperation from(final PersistenceRequestedOperation requestedOp) {
		PersistencePerformedOperation outPerformedOp = null;
		if (requestedOp == PersistenceRequestedOperation.LOAD) {
			outPerformedOp = LOADED;
		}
		else if (requestedOp == PersistenceRequestedOperation.CREATE) {
			outPerformedOp = CREATED;
		}
		else if (requestedOp == PersistenceRequestedOperation.UPDATE) {
			outPerformedOp = UPDATED;
		}
		else if (requestedOp == PersistenceRequestedOperation.DELETE) {
			outPerformedOp = DELETED;
		}
		else if (requestedOp == PersistenceRequestedOperation.FIND) {
			outPerformedOp = FOUND;
		}
		else if (requestedOp == PersistenceRequestedOperation.COUNT) {
			outPerformedOp = COUNTED;
		}
		else {
			throw new IllegalArgumentException("Illegal combination of PersistenceRequestedOperation and HttpResponseCode. This is a DEVELOPER mistake!");
		}
		return outPerformedOp;

	}
	/**
	 * Guess the performed operation based on the requested operation and the HTTP response code
	 * @param requestedOp
	 * @param httpResponseCode
	 * @return
	 */
	public static PersistencePerformedOperation from(final PersistenceRequestedOperation requestedOp,
													 final HttpResponseCode httpResponseCode) {
		PersistencePerformedOperation outPerformedOp = null;
		if (requestedOp == PersistenceRequestedOperation.LOAD) {
			outPerformedOp = LOADED;
		}
		else if (requestedOp == PersistenceRequestedOperation.CREATE) {
			outPerformedOp = CREATED;

		}
		else if (requestedOp == PersistenceRequestedOperation.UPDATE) {
			if (httpResponseCode == HttpResponseCode.NOT_MODIFIED) {
				outPerformedOp = NOT_MODIFIED;
			} else {
				outPerformedOp = UPDATED;
			}
		}
		else if (requestedOp == PersistenceRequestedOperation.DELETE) {
			outPerformedOp = DELETED;
		}
		else if (requestedOp == PersistenceRequestedOperation.FIND) {
			outPerformedOp = FOUND;
		}
		else if (requestedOp == PersistenceRequestedOperation.COUNT) {
			outPerformedOp = COUNTED;
		}
		else {
			throw new IllegalArgumentException("Illegal combination of PersistenceRequestedOperation and HttpResponseCode. This is a DEVELOPER mistake!");
		}
		return outPerformedOp;
	}
}
