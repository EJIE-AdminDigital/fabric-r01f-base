package r01f.types.html;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;

@Immutable
@MarshallType(as="cssStyleClassName")
public record CSSStyleClassName(@Getter String id)
   implements OIDTyped<String> {

	public CSSStyleClassName(final CSSStyleClassName other) {
		this(other.asString());
	}
	public static CSSStyleClassName forId(final String id) {
		return new CSSStyleClassName(id);
	}
	public static CSSStyleClassName valueOf(final String id) {
		return new CSSStyleClassName(id);
	}
	public static CSSStyleClassName fromString(final String s) {
		return CSSStyleClassName.forId(s);
	}
	public static CSSStyleClassName named(final String s) {
		return CSSStyleClassName.forId(s);
	}
	
	@Override
	public String asString() {
		return this.id;
	}
	@Override
	public String toString() {
		return this.id;
	}
}
