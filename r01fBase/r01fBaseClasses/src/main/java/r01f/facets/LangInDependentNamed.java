package r01f.facets;

import java.util.Comparator;

import r01f.facets.delegates.LangInDependentNamedDelegate;
import r01f.locale.Language;
import r01f.util.types.Strings;

/**
 * Interface for objects with lang-independent name
 */
public interface LangInDependentNamed {
/////////////////////////////////////////////////////////////////////////////////////////
//  HaLangInDependentNamedFacet
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface HasLangInDependentNamedFacet 
					extends HasName {	// If an object has a name... it has a summary
		public String getName();
		public void setName(final String name);
		
		public default LangInDependentNamed asLangInDependentNamed() {
			return new LangInDependentNamedDelegate<>(this);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets a name 
	 */
	public void setName(final String name);
	/**
	 * Gets the name
	 * @return
	 */
	public String getName();
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static <S extends HasLangInDependentNamedFacet> Comparator<S> createComparatorIn(final Language lang) {
		return (s1,s2) -> { 
					Comparator<String> comp = Strings.comparatorIn(lang);
					return comp.compare(s1.getName(),s2.getName());
		     	};
	}
}
