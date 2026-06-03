package r01f.enums;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface EnumWithRegEx<E extends Enum<E>> 
         extends EnumExtended<E> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the pattern
	 * @return
	 */
	public Pattern getPattern(); 
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public default boolean matches(final String testString) {
		return this.getPattern()
				   .matcher(testString)
				   .matches();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static <E extends Enum<E> & EnumWithRegEx<E>> Set<E> enumElementsMatching(final String testString,
																					 final Class<E> enumType) {
		return EnumSet.allOf(enumType)
					  .stream()
					  .filter(e -> e.matches(testString))
					  .collect(Collectors.toUnmodifiableSet());
	}
	public static <E extends Enum<E> & EnumWithRegEx<E>> Optional<E> firstEnumElementMatching(final String testString,
																					 		  final Class<E> enumType) {
		return EnumSet.allOf(enumType)
					  .stream()
					  .filter(e -> e.matches(testString))
					  .findFirst();
	}
}
