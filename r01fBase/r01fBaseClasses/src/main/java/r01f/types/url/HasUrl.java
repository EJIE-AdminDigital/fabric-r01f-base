package r01f.types.url;

public interface HasUrl {
	public Url getUrl();
	public default void setUrl(final Url url) {
		throw new UnsupportedOperationException("This object does not support setting a URL");
	}
}
