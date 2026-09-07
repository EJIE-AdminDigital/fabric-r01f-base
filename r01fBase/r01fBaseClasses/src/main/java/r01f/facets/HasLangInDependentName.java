package r01f.facets;

import r01f.facets.delegates.LangInDependentNamedDelegate;

public interface HasLangInDependentName 
		 extends HasName {	// If an object has a name... it has a summary
	public String getName();
	public void setName(final String name);
	
	public default LangInDependentNamed asLangInDependentNamed() {
		return new LangInDependentNamedDelegate<>(this);
	}
}