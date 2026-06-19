package r01f.aspects.interfaces.lazyload;

/**
 * Lazily-loaded type supplier
 * @param <T> lazily loaded type
 */
public interface LazyTypeSupplier<T> 
         extends LazyLoadedTypeSupplier<T> {
	/**
	 * Gets an instance of a lazily-loaded field of a given container object instance
	 * (ie if the field is a Collection, this method lazily loads the Collection and returns it)
	 * see LazyLoadUtils#loadTypeFieldLazily
	 * @param containerObj container object
	 * @return
	 */
	public <C> T instanceFor(final C containerObj);

}
