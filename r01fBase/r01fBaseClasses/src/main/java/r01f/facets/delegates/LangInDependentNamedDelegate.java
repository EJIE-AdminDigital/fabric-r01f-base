package r01f.facets.delegates;

import r01f.facets.HasLangInDependentName;
import r01f.facets.LangInDependentNamed;

/**
 * Encapsulates the {@link LangInDependentNamed} behavior
 * @param <L>
 */
public class LangInDependentNamedDelegate<L extends HasLangInDependentName>
	 extends FacetDelegateBase<L>
  implements LangInDependentNamed {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LangInDependentNamedDelegate(final L hasLangIndependentNameFacet) {
		super(hasLangIndependentNameFacet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getName() {
		return _modelObject.getName();
	}
	@Override
	public void setName(final String name) {
		_modelObject.setName(name);
	}
}
