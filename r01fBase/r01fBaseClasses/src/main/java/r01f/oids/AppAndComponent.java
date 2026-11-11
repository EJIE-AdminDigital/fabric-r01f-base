package r01f.oids;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import lombok.Getter;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.CommonOIDs.AppCode;
import r01f.oids.CommonOIDs.AppComponent;



/**
 * AppCode and component in a single object
 * Normally the id is internally stored as a {@link String} like appCode.appComponent
 * The appCode and appComponent parts can be accessed individually:
 * <pre class='brush:java'>
 * 		AppAndComponent appAndComp = AppAndComponent.composedBy("myApp","myComp");
 * 		AppCode = appAndComp.getAppCode();
 * 		AppComponent = appAndComp.getComponent();
 * </pre>
 */
@MarshallType(as="appAndComponent")
public record AppAndComponent(@Getter String id)
   implements OIDTyped<String> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AppAndComponent(final AppCode appCode,final AppComponent appComponent) {
		this(appCode.asString(),appComponent.asString());
	}
	public AppAndComponent(final String appCode,final String appComponent) {
		this(appCode + "." + appComponent);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static AppAndComponent forId(final String id) {
		return new AppAndComponent(id);
	}
	public static AppAndComponent composedBy(final AppCode appCode,final AppComponent appComponent) {
		return AppAndComponent.composedBy(appCode.asString(),appComponent.asString());
	}
	public static AppAndComponent composedBy(final String appCode,final String appComponent) {
		return new AppAndComponent(appCode,appComponent);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public AppCode getAppCode() {
		return AppCode.forId(Iterables.get(Splitter.on(".")
												   .split(this.getId()),
										   0));	// the first element
	}
	public AppComponent getAppComponent() {
		Iterable<String> parts = Splitter.on(".").split(this.getId());
		int size = Iterables.size(parts);
		
		if (size == 2) {
			return AppComponent.forId(Iterables.get(parts,1));
		} else if (size > 2) {
			StringBuilder comp = new StringBuilder();
			for (int i=1; i < size; i++) {
				comp.append(Iterables.get(parts,i));
				if (i < size - 1) comp.append(".");
			}
			return AppComponent.forId(comp.toString());
		} else {
			throw new IllegalStateException();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return this.id;
	}
	@Override
	public String toString() {
		return this.asString();
	}

}
