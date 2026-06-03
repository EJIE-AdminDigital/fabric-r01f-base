package r01f.types.url.web;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.locale.Language;
import r01f.patterns.IsBuilder;
import r01f.types.tag.StringTagList;
import r01f.util.types.collections.CollectionUtils;

/**
 * Builder for {@link WebLinkText} objects
 * Usage:
 * <pre class="brush:java'>
 * WebLinkText linkText = WebLinkTextBuilder.in(Language.ENGLISH)
 *								  .text("Google")
 * 								  .taggedAs("myLink","this_is_a_link")
 *								  .build();
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class WebLinkTextBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static UrlLinkBuilderTextStep in(final Language lang) {
		WebLinkText link = new WebLinkText(lang);
		return new UrlLinkBuilderTextStep(link);
	}
	public static UrlLinkBuilderTextStep langNotSpecified() {
		return WebLinkTextBuilder.in(null);
	}
	public static UrlLinkBuilderTextStep noLang() {
		return WebLinkTextBuilder.in(null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  LINK
/////////////////////////////////////////////////////////////////////////////////////////
	public record UrlLinkBuilderTextStep(WebLinkText link) {

		public UrlLinkBuilderTextStep text(final String text) {
			this.link.setText(text);
			return this;
		}
		public UrlLinkBuilderTextStep title(final String title) {
			this.link.setTitle(title);
			return this;
		}
		public UrlLinkBuilderTextStep description(final String description) {
			this.link.setDescription(description);
			return this;
		}
		public UrlLinkBuilderTextStep taggedAs(final String... tags) {
			if (CollectionUtils.hasData(tags)) this.link.setTags(new StringTagList(tags));
			return this;
		}
		public UrlLinkBuilderTextStep taggedAs(final StringTagList tags) {
			if (CollectionUtils.hasData(tags)) this.link.setTags(tags);
			return this;
		}
		public WebLinkText build() {
			return this.link;
		}
	}
}
