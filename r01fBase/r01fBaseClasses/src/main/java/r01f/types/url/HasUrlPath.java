package r01f.types.url;

public interface HasUrlPath {
	public UrlPath getUrlPath();
	public default void setUrlPath(final UrlPath urlPath) {
		throw new UnsupportedOperationException("This object does not support setting a URL path");
	}
}
