package r01f.model.builders;

import r01f.model.PersistableModelObject;
import r01f.oids.OID;

public class PersistableModelObjectBuilderBuildStep<M extends PersistableModelObject<? extends OID>> 
	 extends BuilderBuildStep<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public PersistableModelObjectBuilderBuildStep(final M modelObject) {
		super(modelObject);
	}
}
