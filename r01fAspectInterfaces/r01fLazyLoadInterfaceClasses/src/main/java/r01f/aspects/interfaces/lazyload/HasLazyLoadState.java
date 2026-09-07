package r01f.aspects.interfaces.lazyload;

/**
 * Interface implemented by objects that can be loaded with minimum info
 * ie:
 * <pre>
 * 		An object can be loaded with MINIMUM info: only part of it's state is loaded (id, name)
 * 		When loaded with minimum info, the info stored as XML that needs costly parsings is NOT loaded so the
 * 		load operation is FASTER
 * </pre>
 * An object with minimum info CANNOT be persisted!!
 */
public interface HasLazyLoadState {
	/**
	 * @return true if the object is loaded with minimum info
	 */
	public boolean isLoadedWithMinimumInfo();
	/**
	 * Sets the flag that states that the object is loaded with minimum info
	 * @param loaded 
	 */
	public void setLoadedWithMinimumInfo(boolean loaded);
}
