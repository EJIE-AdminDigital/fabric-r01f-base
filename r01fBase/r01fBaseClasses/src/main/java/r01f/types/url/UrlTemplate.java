package r01f.types.url;

import java.util.Collection;
import java.util.Map;

import r01f.types.KeyAndValue;
import r01f.util.types.StringCustomizeUtils;

/**
 * An URL template is a string that represents a URL with placeholders for dynamic values.
 * Example:
 * <code>
 * 		var urlTemplate = UrlTemplate.from("https://api.example.com/users/{userId}/posts/{postId}");
 * 		var url = urlTemplate.toUrlUsing(Map.of("userId","123",
 * 												"postId","456"));
 * </code>
 * It can be used to generate actual URLs by replacing the placeholders with specific values.
 */
public record UrlTemplate(String template) {
	/**
	 * Creates a new URL template from the given string.
	 * @param template the URL template string
	 * @return a new UrlTemplate instance
	 */
	public static UrlTemplate from(final String template) {
		return new UrlTemplate(template);
	}
	public Url toUrlUsing(final Map<String,String> values) {
		String interpolatedUrl = StringCustomizeUtils.replaceVariableValues(this.template,'{','}',values);
		return Url.from(interpolatedUrl);
	}
	public Url toUrlUsing(final KeyAndValue<String,String>... keyAndValues) {
		String interpolatedUrl = StringCustomizeUtils.replaceVariableValues(this.template,'{','}',KeyAndValue.toMap(keyAndValues));
		return Url.from(interpolatedUrl);
	}
	public Url toUrlUsing(final Collection<KeyAndValue<String,String>> keyAndValues) {
		String interpolatedUrl = StringCustomizeUtils.replaceVariableValues(this.template,'{','}',KeyAndValue.toMap(keyAndValues));
		return Url.from(interpolatedUrl);
	}
}
