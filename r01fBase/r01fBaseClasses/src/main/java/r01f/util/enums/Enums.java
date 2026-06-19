package r01f.util.enums;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

/**
 * Enum utilities
 * Usage:
 * Provided an enum:
 * <pre class='brush:java'>
 * 		public enum MyEnum {
 * 			A,B,C;
 * 		}
 * </pre>
 * The util methods can be accessed:
 * <pre class='brush:java'>
 * 		boolean isIn = Enums.of(MyEnum.class).isIn(MyEnum.A,
 *											   	   new MyEnum[] {MyEnum.A,MyEnum.B,MyEnum.C});
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class Enums {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static <E extends Enum<E>> EnumWrapper<E> of(final Class<E> enumType) {
		return new EnumWrapper<>(enumType.getEnumConstants());
	}
	public static <E extends Enum<E>> EnumWrapper<E> wrap(final Class<E> enumType) {
		return new EnumWrapper<>(enumType.getEnumConstants());
	}
	public static <E extends Enum<E>> EnumWrapper<E> of(final E[] enumEls) {
		return new EnumWrapper<>(enumEls);
	}
	public static <E extends Enum<E>> EnumWrapper<E> wrap(final E[] enumEls) {
		return new EnumWrapper<>(enumEls);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  WRAPPER
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static record EnumWrapper<E extends Enum<E>>(E[] values)	{		// values
		
		public EnumWrapper {
			if (CollectionUtils.isNullOrEmpty(values)) throw new IllegalArgumentException("The enum values is mandatory!");
		}
		public static <T extends Enum<T>> EnumWrapper<T> create(final Class<T> enumType) {
			return new EnumWrapper<>(enumType.getEnumConstants());
		}
		////////// Enum type
		@SuppressWarnings("unchecked")
		public Class<E> getEnumType() {
			return (Class<E>)this.values.getClass().getComponentType();
		}
		////////// Factory methods
		/**
		 * Returns the enum element matching the given predicate
		 * @param pred 
		 * @return the enum's element
		 */
		public Optional<E> matching(final Predicate<E> pred) {
			if (pred == null) throw new IllegalArgumentException();

			return Stream.of(this.values)
						 .filter(pred)
						 .findFirst();
		}
		/**
		 * Returns the enum element from it's name
		 * @param name element's name
		 * @return the enum's element
		 */
		public Optional<E> fromName(final String name) {
			if (name == null) return Optional.ofNullable(null);
			return this.matching(e -> e.name().equals(name));
		}
		/**
		 * Returns the enum element from it's name
		 * @param name element's name
		 * @return the enum's element
		 */
		public Optional<E> fromNameIgnoringCase(final String name) {
			if (name == null) return Optional.ofNullable(null);
			return this.matching(e -> e.name().equalsIgnoreCase(name));
		}
		/**
		 * Returns the enum element from it's code
		 * WARNING: the enum MUST be an instance of {@link EnumWithCode}
		 * @param code
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public <C> Optional<E> fromCode(final C code) {
			if (code == null) return Optional.ofNullable(null);
			return this.matching(e -> {
									EnumWithCode<C,E> enumWithCode = (EnumWithCode<C,E>)e;
									return enumWithCode.getCode().toString().equals(code.toString());	// toString() is used because equals() may return false when using primitives
								 });
		}
		////////// can be methods
		/**
		 * Returns true if the provided param can be one of the enum elements
		 * @param code the code to check
		 * @return true if one enum element's code matches the param
		 */
		public <C> boolean canBeCode(final C code) {
			Optional<E> e = this.fromCode(code);
			return e.isPresent();
		}
		/**
		 * Returns true if the provided param can be one of the enum elements
		 * @param name the name to check
		 * @return true if one enum element's name matches the param
		 */
		public boolean canBe(final String name) {
			Optional<E> e = this.fromName(name);
			return e.isPresent();
		}
		////////// is / notIs
		/**
		 * Returns true if the element is the other element
		 * @param el one element of the enum
		 * @param other other element of the enum
		 * @return true if both elements are the same
		 */
		public boolean is(final E el,final E other) {
			return el == other;
		}
		/**
		 * Returns if the element provided in the first parameter is NOT the provided one in the second parameter
		 * @param el
		 * @param other
		 * @return
		 */
		public boolean isNOT(final E el,final E other) {
			return !this.is(el,other);
		}
		////////// isIn / isNOTIn
		/**
		 * Returns true if the element is among the provided other elements
		 * @param el the element to check if is included
		 * @param els the set of elements
		 * @return true if el is included in els
		 */
		@SuppressWarnings("unchecked")
		public boolean isIn(final E el,final E... els) {
			boolean isIn = false;
			for (E currE : els) {
				if (currE == el) {
					isIn = true;
					break;
				}
			}
			return isIn;
		}
		/**
		 * Returns true if the element provided in the first parameter is within the provided ones in the second parameter 
		 * @param el 
		 * @param els 
		 * @return true 
		 */
		public boolean isIn(final E el,final Collection<E> els) {
			boolean isIn = false;
			for (E currE : els) {
				if (currE == el) {
					isIn = true;
					break;
				}
			}		
			return isIn;
		}
		/**
		 * Returns true if the element provided in the first parameter is NOT within the provided ones in the second parameter 
		 * @param el 
		 * @param els 
		 * @return true 
		 */
		@SuppressWarnings("unchecked")
		public boolean isNOTIn(final E el,final E... els) {
			boolean isNOTIn = true;
			for (E currE : els) {
				if (currE == el) {
					isNOTIn = false;
					break;
				}
			}		
			return isNOTIn;
		}
		/**
		 * Returns true if the element provided in the first parameter is within the provided ones in the second parameter 
		 * @param el 
		 * @param els 
		 * @return true 
		 */
		public boolean isNOTIn(final E el,final Collection<E> els) {
			boolean isNOTIn = true;
			for (E currE : els) {
				if (currE == el) {
					isNOTIn = false;
					break;
				}
			}		
			return isNOTIn;
		}
		////////// names
		public Collection<String> namesQuoted() {
			return Enums.namesQuoted(this.values);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	NAMES METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the enum names quouted
	 * @param vals
	 * @return
	 */
	public static <E extends Enum<E>> Collection<String> namesQuoted(@SuppressWarnings("unchecked") final E... vals) {
		return Enums.namesQuoted(Lists.newArrayList(vals));
	}
	/**
	 * Returns the enum names quouted
	 * @param vals
	 * @return
	 */
	public static <E extends Enum<E>> Collection<String> namesQuoted(final Collection<E> vals) {
		return CollectionUtils.hasData(vals)
					? vals.stream()
			 			  .map(val -> Strings.customized("'{}'",
											 	  		 val.name()))
			 			  .collect(Collectors.toList())
			 		: new ArrayList<>();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CODE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Tries to guess the code type of a {@link EnumWithCode} implementing type
	 * @param enumType
	 * @return
	 */
	public static Class<?> guessEnumWithCodeCodeType(final Class<? extends Enum<?>> enumType) {
		// trick: get an enum element and call it's getCodeType() method
		return ((EnumWithCode<?,?>)enumType.getEnumConstants()[0]).getCodeType();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SET METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns elements in s1 NOT present in s2
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static <E extends Enum<E>> Set<E> difference(final Set<E> s1,final Set<E> s2) {
		Set<E> clone = new HashSet<>(s1);
		clone.removeAll(s2);
		return clone;
	}
	/**
	 * Returns elements present both in s1 and s2
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static <E extends Enum<E>> Set<E> intersection(final Set<E> s1,final Set<E> s2) {
		Set<E> clone = new HashSet<>(s1);
		clone.retainAll(s2);	// retain in l1 elements that are in l2
		return clone;
	}
	/**
	 * Returns the union of both sets
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static <E extends Enum<E>> Set<E> union(final Set<E> s1,final Set<E> s2) {
		Set<E> clone = new HashSet<>(s1);
		clone.addAll(s2);	// add all elements of l2
		return clone;
	}
}
