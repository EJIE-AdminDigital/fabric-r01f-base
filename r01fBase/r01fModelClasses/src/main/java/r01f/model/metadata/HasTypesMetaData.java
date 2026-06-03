package r01f.model.metadata;

/**
 * Holds info about model object types metadata
 */
public interface HasTypesMetaData {
	/**
	 * Tells if there is {@link TypeMetaData} for the given model object type
	 * @param <M>
	 * @param hasMetaData
	 * @return
	 */
	public <M extends MetaDataDescribable> boolean existsTypeMetaDataFor(final Class<M> hasMetaData);
	/**
	 * Finds the {@link TypeMetaData} for the given model object type
	 * @param hasMetaData
	 * @return
	 */
	public <M extends MetaDataDescribable> TypeMetaData<M> getTypeMetaDataFor(final Class<M> hasMetaData);
	/**
	 * Finds the {@link TypeMetaData} for the given model objet type code
	 * @param typeCode
	 * @return
	 */
	public <M extends MetaDataDescribable> TypeMetaData<M> getTypeMetaDataFor(final long typeCode);
}