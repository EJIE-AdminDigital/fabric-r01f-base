package r01f.types.url;

import java.util.Collection;
import java.util.Map;

import r01f.types.KeyAndValue;
import r01f.util.types.StringCustomizeUtils;

/**
 * An UrlPath template is a string that represents a URL with placeholders for dynamic values.
 * Example:
 * <code>
 * 		var urlPathTemplate = UrlPathTemplate.from("/users/{userId}/posts/{postId}");
 * 		var urlPath = urlPathTemplate.toUrlUsing(Map.of("userId","123",
 * 														"postId","456"));
 * </code>
 * It can be used to generate actual URLs by replacing the placeholders with specific values.
 */
public record UrlPathTemplate(String template) {
	/**
	 * Creates a new URL template from the given string.
	 * @param template the URL template string
	 * @return a new UrlTemplate instance
	 */
	public static UrlPathTemplate from(final String template) {
		return new UrlPathTemplate(template);
	}
	public UrlPath toUrlPathUsing(final Map<String,String> values) {
		String interpolatedUrl = StringCustomizeUtils.replaceVariableValues(this.template,'{','}',values);
		return UrlPath.from(interpolatedUrl);
	}
	public UrlPath toUrlUsing(final KeyAndValue<String,String>... keyAndValues) {
		String interpolatedUrl = StringCustomizeUtils.replaceVariableValues(this.template,'{','}',KeyAndValue.toMap(keyAndValues));
		return UrlPath.from(interpolatedUrl);
	}
	public UrlPath toUrlUsing(final Collection<KeyAndValue<String,String>> keyAndValues) {
		String interpolatedUrl = StringCustomizeUtils.replaceVariableValues(this.template,'{','}',KeyAndValue.toMap(keyAndValues));
		return UrlPath.from(interpolatedUrl);
	}
}
