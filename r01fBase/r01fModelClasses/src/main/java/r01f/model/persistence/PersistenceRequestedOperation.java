package r01f.model.persistence;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.model.services.COREServiceMethod;

/**
 * Persistence-related operations
 * Note that the performed operation is NOT always the same as the requested one
 * (ie: an update could be requested by the client BUT the record didn't exist so a creation is performed)
 */
@Accessors(prefix="_")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PersistenceRequestedOperation 
 implements EnumExtended<PersistenceRequestedOperation> {
	EXISTS(PersistencePerformedOperation.EXISTS),
	LOAD(PersistencePerformedOperation.LOADED),
	SAVE(PersistencePerformedOperation.SAVED),	// can be used for both create and update operations
	CREATE(PersistencePerformedOperation.CREATED),
	UPDATE(PersistencePerformedOperation.UPDATED),
	DELETE(PersistencePerformedOperation.DELETED),
	FIND(PersistencePerformedOperation.FOUND),
	COUNT(PersistencePerformedOperation.COUNTED),
	LASTUPDATEDATE(null);
	
	@Getter private final PersistencePerformedOperation _performedOperation;
	
	public COREServiceMethod getCOREServiceMethod() {
		return COREServiceMethod.named(this.name());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final COREServiceMethod coreServiceMethod) {
		return this.asCOREServiceMethod().is(coreServiceMethod);
	}
	public boolean isNOT(final COREServiceMethod coreServieMethod) {
		return this.asCOREServiceMethod().isNOT(coreServieMethod);
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
	public static PersistenceRequestedOperation fromName(final String name) {
		return EnumExtended.fromNameIgnoringCase(name,PersistenceRequestedOperation.class)
//						   .orElseThrow(() -> new IllegalArgumentException("no " + PersistenceRequestedOperation.class + " element with name=" + name.toUpperCase()));
						   .orElse(null);
	}
	public static PersistenceRequestedOperation named(final String name) {
		return PersistenceRequestedOperation.fromName(name);
	}
	public static PersistenceRequestedOperation from(final COREServiceMethod method) {
		return PersistenceRequestedOperation.fromName(method.asString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public COREServiceMethod asCOREServiceMethod() {
		return COREServiceMethod.named(this.name());
	}
}