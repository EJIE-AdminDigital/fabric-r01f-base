package r01f.model;

import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;

/**
 * A model object summary used when returning persistence find results
 * @param <M>
 */
@MarshallPolymorphicTypeInfo
public interface SummarizedModelObject<M extends ModelObject>
		 extends SummarizedObject {
	/**
	 * Return the model object type
	 * @return
	 */
	public Class<M> getModelObjectType();
}
