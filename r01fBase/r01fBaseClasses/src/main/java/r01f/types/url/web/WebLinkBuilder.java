package r01f.types.url.web;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.locale.Language;
import r01f.patterns.IsBuilder;
import r01f.types.url.Url;

/**
 * Builds {@link WebLink}s
 * <pre class='brush:java'>
 * 		WebLink link = WebLinkBuilder.of(Url.from("www.google.com")
 * 									 .withText("Google")
 * 									 .in(Language.ENGLISH)
 * 									 .withPresentationData(... use presentation data builder ...)
 * 									 .build();
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class WebLinkBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final WebLinkBuilderTextsStep of(final String url) {
		return WebLinkBuilder.of(Url.from(url));
	}
	public static final WebLinkBuilderTextsStep of(final Url url) {
		return new WebLinkBuilderTextsStep(url);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public record WebLinkBuilderTextsStep(Url url) {
		public WebLinkBuilderPresentationDataStep with(final WebLinkText text) {
			return new WebLinkBuilderPresentationDataStep(this.url,
														  text);
		}
		public WebLinkBuilderTextsLangStep withTextAndTitle(final String text,final String title) {
			WebLinkText linkText = new WebLinkText();
			linkText.setText(text);
			linkText.setTitle(title);
			return new WebLinkBuilderTextsLangStep(url,
												   linkText);
		}
		public WebLinkBuilderTextsLangStep withText(final String text) {
			WebLinkText linkText = new WebLinkText();
			linkText.setText(text);
			return new WebLinkBuilderTextsLangStep(url,
												   linkText);
		}
		public WebLinkBuilderTextsLangStep withText(final WebLinkText text) {
			return new WebLinkBuilderTextsLangStep(url,
												   text);
		}
	}
	public record WebLinkBuilderTextsLangStep(Url url, WebLinkText texts) {

		public WebLinkBuilderPresentationDataStep undefinedLanguage() {
			return new WebLinkBuilderPresentationDataStep(this.url,
														  this.texts);
		}
		public WebLinkBuilderPresentationDataStep in(final Language lang) {
			this.texts.setLang(lang);
			return new WebLinkBuilderPresentationDataStep(this.url,
														  this.texts);
		}
		public WebLink build() {
			return new WebLinkBuilderBuildStep(this.url,
											   this.texts,
											   null)
					  		.build();
		}
	}
	public record WebLinkBuilderPresentationDataStep(Url url,
													 WebLinkText text) {

		public WebLinkBuilderBuildStep withoutPresentationData() {
			return new WebLinkBuilderBuildStep(this.url,
											   this.text,
											   null);
		}
		public WebLinkBuilderBuildStep usingPresentationData(final WebLinkPresentationData presentation) {
			return new WebLinkBuilderBuildStep(this.url,
											   this.text,
											   presentation);
		}
		public WebLink build() {
			return new WebLinkBuilderBuildStep(this.url,
											   this.text,
											   null)
							.build();
		}
	}
	public record WebLinkBuilderBuildStep(Url url,
										  WebLinkText text,
										  WebLinkPresentationData presentation) {
		public WebLink build() {
			return new WebLink(this.url,
							   this.text,
							   this.presentation);
		}
	}
}
