package r01f.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.NotDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.securitycontext.SecurityContext;

/**
 * Base type for model object extension objects
 * A model object is a simple POJO that when "extended" is provided with server-side (or api-dependent) behavior
 * For example:
 * 		If a model object has a dependent object (aggregation / composition), usually, the model object POJO only
 * 		has the dependent object oid reference;
 * 		<pre class='brush:java'>	
 * 			ModelObject modelObj = api.load(modelObjOid);
 * 			Collection<DependentModelObjOID> depModelObjOids = modelObj.getDependentOids();		// this do NOT need an api-call
 * 			// In order to load the dependent object (another POJO), another api call is needed
 * 			Collection<DependentModelObj> depModelObjs = Lists.newArrayList(depModelObjOids.size());
 * 			for (DependentModelObjOID depOid : depModelObjOids) {
 * 				DependentModelObj depModelObj = api.loadDependentModelObj(depOid);
 * 				depModelObjs.add(depModelOjb);
 * 			}
 * 		</pre> 
 * 		A more "transparent" (or easy) way to access the dependent model object can be provided by "extending" the "parent"
 * 		model object and making api calls invisible to the client user:
 * 		<pre class='brush:java'>
 * 			ModelObject modelObj = api.load(modelObjOid);
 * 			DependentModelObj depModelObj = modelObj.getDependent(depModelObjOid);	// this need a api-call to load the dependent object
 * 		</pre>
 * @param <M> the R01MExtensibleModelObject
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public abstract class ModelObjectExtensionBase<M extends ExtendedModelObject<?>> 
		   implements ModelObjectExtension<M>,
		   			  Serializable {

	private static final long serialVersionUID = 6438667712437043285L;
/////////////////////////////////////////////////////////////////////////////////////////
//	SECURITY                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	protected final SecurityContext _securityContext;
/////////////////////////////////////////////////////////////////////////////////////////
//  INTERFACE R01MModelObjectExtension 
//	(used at R01MModelObjectExtensionAspect to inejct the model object to this extension)
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Extended object
	 */
	@NotDirtyStateTrackable
	@MarshallIgnoredField
	@Getter protected transient M _extendedModelObj;
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTEND
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void extend(final M extensible) {
		_extendedModelObj = extensible;
	}
	@SuppressWarnings("unchecked")
	public <E extends ExtendedModelObject<?>> void unsafeExtend(final E extensible) {
		_extendedModelObj = (M)extensible;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <E extends ModelObjectExtension<M>> E as(final Class<E> type) {
		return (E)this;
	}
}
