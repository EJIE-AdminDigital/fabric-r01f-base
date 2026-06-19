package r01f.comparable;

/**
 * Interface to be implemented by the comparable objects
 */
public interface ComparableObject<SELF_TYPE extends ComparableObject<SELF_TYPE>> {
	
	/**
	 * Returns if is the same object
	 * @return the debug info
	 */
	public boolean isSameObject(final SELF_TYPE other);
}
