package r01f.types.summary;

import r01f.facets.HasLangDependentName;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.types.summary.SummaryBases.MutableLangDependentSummary;

/**
 * Summary in multiple languages for an object that has multiple-languages names
 */
public class SummaryHasLanguageDependentNameBacked 
     extends MutableLangDependentSummary {

	private static final long serialVersionUID = 8505060057374301693L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Summary in each language
	 */
	private final HasLangDependentName _hasName;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private SummaryHasLanguageDependentNameBacked(final boolean isFullText,
												  final HasLangDependentName hasName) {
		super(isFullText);
		_hasName = hasName;
	}
	public static SummaryHasLanguageDependentNameBacked of(final HasLangDependentName hasNameFacet) {
		SummaryHasLanguageDependentNameBacked outSummary = new SummaryHasLanguageDependentNameBacked(false,		// NOT fullText
																									 hasNameFacet);
		return outSummary;
	}
	public static SummaryHasLanguageDependentNameBacked fullTextOf(final HasLangDependentName hasNameFacet) {
		SummaryHasLanguageDependentNameBacked outSummary = new SummaryHasLanguageDependentNameBacked(true,		// fullText
																									 hasNameFacet);
		return outSummary;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString(final Language lang) {
		return _hasName != null ? _hasName.asLangDependentNamed()
										  .getNameIn(lang)
								: null;
	}
	@Override
	public String asString() {
		return this.asString(Language.DEFAULT);
	}
	@Override
	public void setSummary(final Language lang,final String summary) {
		_hasName.asLangDependentNamed()
				.setNameIn(lang,summary);
	}
	@Override
	public LanguageTexts asLanguageTexts() {
		return _hasName.getNameByLanguage();
	}
}
