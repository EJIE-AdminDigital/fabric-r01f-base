package r01f.enums;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Collectors;

import r01f.util.types.Strings;

/**
 * An {@link Enum} that assigns codes to every enum element
 * @see EnumWithCodeWrapper
 * @param <C> the code type
 * @param <E> the enum type
 */
public interface EnumWithCode<C,E extends Enum<E>>
         extends EnumExtended<E> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the code
	 * @return
	 */
	public C getCode();
	/**
	 * Returns the type of the code
	 * @return
	 */
	public Class<C> getCodeType();
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a list of the codes
	 */
	public static <C,E extends Enum<E> & EnumWithCode<C,E>> Collection<C> codes(final Class<E> enumType) {
		return EnumSet.allOf(enumType)
					  .stream()
					  .map(EnumWithCode::getCode)
					  .toList();
	}
	/**
	 * Returns the enum codes quouted
	 * @return
	 */
	public static <C,E extends Enum<E> & EnumWithCode<C,E>> Collection<String> codesQuoted(final Class<E> enumType) {
		return EnumSet.allOf(enumType)
					  .stream()
					  .map(val -> Strings.customized("'{}'",
									 	  			 val.getCode()))
	 			      .collect(Collectors.toList());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the enum element from the code
	 * @param code 
	 * @return 
	 */
	public static <C,E extends Enum<E> & EnumWithCode<C,E>> boolean canBeFromCode(final C code,
									   						 					  final Class<E> enumType) {
		Optional<E> e = EnumWithCode.fromCode(code,
											  enumType);
		return e.isPresent();
	}
	/**
	 * Gets the enum element from the code
	 * @param code 
	 * @return 
	 */
	public static <C,E extends Enum<E> & EnumWithCode<C,E>> Optional<E> fromCode(final C code,
									   						 					 final Class<E> enumType) {
		return EnumSet.allOf(enumType)
					  .stream()
					  .filter(e -> e.getCode().equals(code))
					  .findFirst();
	}
}
