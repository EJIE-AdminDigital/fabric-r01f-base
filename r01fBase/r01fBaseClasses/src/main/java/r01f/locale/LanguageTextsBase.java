package r01f.locale;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Text in different languages collection
 */
@Accessors(prefix="_")
public abstract class LanguageTextsBase<SELF_TYPE extends LanguageTextsBase<SELF_TYPE>>
           implements LanguageTexts {

	private static final long serialVersionUID = -34749639584791088L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Behavior when a required language text is NOT found
	 */
	@MarshallIgnoredField
	@Getter @Setter protected transient LangTextNotFoundBehabior _langTextNotFoundBehabior = LangTextNotFoundBehabior.RETURN_NULL;
	/**
	 * Default value
	 */
	@MarshallIgnoredField
	@Getter @Setter protected transient String _defaultValue = "*** No text for {} ***";
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LanguageTextsBase(final LangTextNotFoundBehabior langTextNotFoundBehabior,
							 final String defaultValue) {
		_langTextNotFoundBehabior = langTextNotFoundBehabior;
		if (Strings.isNOTNullOrEmpty(defaultValue)) _defaultValue = defaultValue;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ABSTRACT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Retrieves a text for the provided lang
	 * @param lang the lang
	 * @return the text associated with the lang
	 */
	protected abstract String _retrieve(final Language lang);
	/**
	 * Puts a text in a language
	 * @param lang the lang
	 * @param text the text associated with the lang
	 */
	protected abstract void _put(final Language lang,final String text);

/////////////////////////////////////////////////////////////////////////////////////////
//	FLUENT API
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	public class TextInLangFluentAdapter {
		private final Language lang;

		public String get() {
			return _retrieve(lang);
		}
		/*
		 @SuppressWarnings("unchecked")
		public SELF_TYPE set(final String text) {
			if (!Strings.isNullOrEmpty(text)) _put(lang,text);
			return (SELF_TYPE)SELF_TYPE.this;
		}*/

		@SuppressWarnings("unchecked")
		public SELF_TYPE set(final String text) {
			if (!Strings.isNullOrEmpty(text)) _put(lang,text);
			return (SELF_TYPE)LanguageTextsBase.this;
		}

	}
	/**
	 * Returns a fluent api adapter to get/set the text in a language
	 * This allows for fluent code like:
	 * <pre class='brush:java'>
	 * 		textByLang.in(Language.SPANISH).set("Hola")
	 * 				  .in(Langugae.BASQUE).set("Kaixo");
	 * 		String textInBasque = textByLang.in(Language.BASQUE).get();
	 * </pre>
	 * @param lang the lang
	 * @return the adapter
	 */
	public TextInLangFluentAdapter in(final Language lang) {
		return new TextInLangFluentAdapter(lang);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ADD
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public LanguageTexts add(final Language lang,final String text) {
		_put(lang,text);
		return this;
	}
	public LanguageTextsBatchAdd addBatchFirst(final Language lang,final String text) {
		this.add(lang,text);
		return new LanguageTextsBatchAdd();
	}
	public class LanguageTextsBatchAdd {
		public LanguageTextsBatchAdd addBatchNext(final Language lang,final String text) {
			LanguageTextsBase.this.add(lang,text);
			return this;
		}
		public LanguageTexts addBatchLast(final Language lang,final String text) {
			LanguageTextsBase.this.add(lang,text);
			return LanguageTextsBase.this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String getFor(final Language lang) {
		if (lang == null) throw new IllegalArgumentException("lang cannot be null to get a " + LanguageTexts.class.getSimpleName() + " text");
		
		String outText = _retrieve(lang);
		
		if (outText == null && _langTextNotFoundBehabior == LangTextNotFoundBehabior.RETURN_NULL) {
			/* outText is yet null */
		} else if (outText == null && _langTextNotFoundBehabior == LangTextNotFoundBehabior.RETURN_DEFAULT_VALUE) {
			outText = _defaultValue != null ? Strings.customized(_defaultValue,lang)
											: null;
		} else if (outText == null && _langTextNotFoundBehabior == LangTextNotFoundBehabior.THROW_EXCEPTION) {
			String msg = Strings.customized(_defaultValue,lang);
			throw new IllegalStateException(msg);
		}
		return outText;
	}
	@Override
	public String getForOrNull(final Language lang) {
		return _retrieve(lang);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  override
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		String outStr = null;
		Set<Language> langsWithText = this.getDefinedLanguages();
		if (CollectionUtils.hasData(langsWithText)) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (Iterator<Language> langIt = langsWithText.iterator(); langIt.hasNext(); ) {
				Language lang = langIt.next();
				String text = this.getFor(lang);
				sb.append(lang).append(":").append(text);
				if (langIt.hasNext()) sb.append(", ");
			}
			sb.append("]");
			outStr = sb.toString();
		} else {
			outStr = "NO data";
		}
		return outStr;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASH-CODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != this.getClass()) return false;
		LanguageTexts otherLangTexts = (LanguageTexts)obj;

		// check both have the SAME langs
		Set<Language> thisDefinedLangs = this.getDefinedLanguages();
		Set<Language> otherDefinedLangs = otherLangTexts.getDefinedLanguages();

		if (CollectionUtils.hasData(thisDefinedLangs) && CollectionUtils.isNullOrEmpty(otherDefinedLangs)) return false;
		if (CollectionUtils.isNullOrEmpty(thisDefinedLangs) && CollectionUtils.hasData(otherDefinedLangs)) return false;
		if (CollectionUtils.hasData(thisDefinedLangs) && CollectionUtils.hasData(otherDefinedLangs)
		 && thisDefinedLangs.size() != otherDefinedLangs.size()) return false;

		// check the content by lang
		boolean allContentEq = true;
		for (Language lang : thisDefinedLangs) {
			String thisLangCont = this.getFor(lang);
			String otherLangCont = otherLangTexts.getFor(lang);

			if ((thisLangCont != null && otherLangCont == null)
			  || (thisLangCont == null && otherLangCont != null) ) {
				allContentEq = false;
				break;
			}
			if (thisLangCont != null && otherLangCont != null
					&& !thisLangCont.equals(otherLangCont)) {
				allContentEq = false;
				break;
			}
		}
		return allContentEq;
	}
	@Override
	public int hashCode() {
		Set<Language> langs = this.getDefinedLanguages();
		Collection<String> allTexts = CollectionUtils.hasData(langs)
											? this.getDefinedLanguages()
												  .stream()
												  .map(LanguageTextsBase.this::getFor)
												  .collect(Collectors.toList())
											: null;
		return allTexts != null ? Objects.hashCode(allTexts.toArray(new String[allTexts.size()]))
								: super.hashCode();
	}
}
