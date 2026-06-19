package r01f.enums;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Predicate;

import r01f.util.types.Strings;

/**
 * Basic operations with an {@link Enum}
 * @see EnumExtendedWrapper
 * @param <E>
 */
public interface EnumExtended<E extends Enum<E>> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the {@link Enum}'s element name
	 * (it uses java's type erasure to emulate the name() method of an enum)
	 * @return
	 */
	public String name();
	/**
	 * Returns the enum names quouted
	 * @param vals
	 * @return
	 */
	public static <E extends Enum<E> & EnumExtended<E>> Collection<String> namesQuoted(final Class<E> enumType) {
		return EnumSet.allOf(enumType)
					  .stream()
			 		  .map(val -> Strings.customized("'{}'",
											 	  	 val.name()))
			 		  .toList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	is / isNOT
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Checks if this element is the same as the provided one
	 * @param el
	 * @return
	 */
	public default boolean is(final E el) {
		return this == el;
	}
	/**
	 * Checks if this element is NOT the same as the provided one
	 * @param el
	 * @return
	 */
	public default boolean isNOT(final E el) {
		return !this.is(el);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	isIn / isNOTIn
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if the element is within a provided list of elements
	 * @param els
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public default boolean isIn(final E... els) {
		boolean isIn = false;
		for (E currE : els) {
			if (this.is(currE)) {
				isIn = true;
				break;
			}
		}		
		return isIn;
	}
	/**
	 * Checks if the element is within a provided list of elements
	 * @param els
	 * @return
	 */
	public default boolean isIn(final Collection<E> els) {
		boolean isIn = false;
		for (E currE : els) {
			if (this.is(currE)) {
				isIn = true;
				break;
			}
		}		
		return isIn;
	}
	/**
	 * Checks if the element is NOT within a provided list of elements
	 * @param els
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public default boolean isNOTIn(final E... els) {
		boolean isNOTIn = true;
		for (E currE : els) {
			if (this.is(currE)) {
				isNOTIn = false;
				break;
			}
		}		
		return isNOTIn;
	}
	/**
	 * Checks if the element is NOT within a provided list of elements
	 * @param els
	 * @return
	 */
	public default boolean isNOTIn(final Collection<E> els) {
		boolean isNOTIn = true;
		for (E currE : els) {
			if (this.is(currE)) {
				isNOTIn = false;
				break;
			}
		}		
		return isNOTIn;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	STATIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the enum element matching the given predicate
	 * @param pred 
	 * @param enumType
	 * @return 
	 */	
	public static <E extends Enum<E> & EnumExtended<E>> Optional<E> matching(final Predicate<E> pred,final Class<E> enumType) {
		return EnumSet.allOf(enumType)
					  .stream()
					  .filter(pred)
					  .findFirst();
	}
	/**
	 * Checks if the given name can be an enum element
	 * @param name 
	 * @param enumType
	 * @return 
	 */	
	public static <E extends Enum<E> & EnumExtended<E>> boolean canBeFromName(final String name,final Class<E> enumType) {
		Optional<E> e = EnumExtended.fromName(name,
											  enumType);
		return e.isPresent();
	}
	/**
	 * Gets the enum element from it's name
	 * @param name 
	 * @param enumType
	 * @return 
	 */	
	public static <E extends Enum<E> & EnumExtended<E>> Optional<E> fromName(final String name,final Class<E> enumType) {
		return EnumExtended.matching(e -> e.name().equals(name),
									 enumType);
	}
	/**
	 * Gets the enum element from it's name ignoring the case
	 * @param name 
	 * @param enumType
	 * @return 
	 */	
	public static <E extends Enum<E> & EnumExtended<E>> boolean canBeFromNameIgnoringCase(final String name,final Class<E> enumType) {
		Optional<E> e = EnumExtended.fromNameIgnoringCase(name,enumType);
		return e.isPresent();
	}
	/**
	 * Gets the enum element from it's name ignoring the case
	 * @param name 
	 * @param enumType
	 * @return 
	 */	
	public static <E extends Enum<E> & EnumExtended<E>> Optional<E> fromNameIgnoringCase(final String name,final Class<E> enumType) {
		return EnumExtended.matching(e -> e.name().equalsIgnoreCase(name),
									 enumType);
	}
}
