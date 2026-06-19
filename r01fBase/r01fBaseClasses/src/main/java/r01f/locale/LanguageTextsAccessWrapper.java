package r01f.locale;


import java.util.Optional;

import lombok.RequiredArgsConstructor;

/**
 * A wrapper to access a {@link LanguageTexts} data
 */
@RequiredArgsConstructor
public final class LanguageTextsAccessWrapper {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient HasLanguageTexts _hasLangTexts;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static LanguageTextsAccessWrapper wrap(final HasLanguageTexts hasLangTexts) {
		return new LanguageTextsAccessWrapper(hasLangTexts);
	}
	public static LanguageTextsAccessWrapper wrap(final LanguageTexts langTexts) {
		return new LanguageTextsAccessWrapper(new HasLanguageTexts() {
													@Override
													public LanguageTexts get() {
														return langTexts;
													}
													@Override
													public void set(final LanguageTexts langText) {
														throw new UnsupportedOperationException();
													}
											  });
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public Optional<String> getIn(final Language lang) {
		if (_hasLangTexts == null) return Optional.ofNullable(null);
		String outText = null;
		try {
			outText = _hasLangTexts != null ? _hasLangTexts.get() != null
													? _hasLangTexts.get()
											  			   		   .get(lang)
											  		: null
										 : null;
		} catch (IllegalStateException illEx) {
			// no text for for lang			
		}
		return Optional.ofNullable(outText);
	}
	public String getInOrNull(final Language lang) {
		return this.getIn(lang)
				   .orElse(null);
	}
	public String getInOrDefault(final Language lang,
								 final String def) {
		return def != null
				? this.getIn(lang)
					  .orElse(def)
				: this.getIn(lang)
					  .orElse(null);
	}
	public Optional<String> getInAnyLanguage() {
		if (_hasLangTexts == null) return Optional.ofNullable(null);
		return Optional.ofNullable(_hasLangTexts != null ? _hasLangTexts.get() != null
																	? _hasLangTexts.get()
																				   .getAny()
																	: null
														 : null);
	}
	public String getInAnyLanguageOrDefault(final String def) {
		return this.getInAnyLanguage()
				  .orElse(def);
	}
	public String getInAnyLanguageOrNull() {
		return this.getInAnyLanguage()
				  .orElse(null);
	}
}
