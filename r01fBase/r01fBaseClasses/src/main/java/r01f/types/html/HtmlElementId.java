package r01f.types.html;

import java.util.UUID;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;

@Immutable
@MarshallType(as="htmlElementId")
public record HtmlElementId(@Getter String id)
   implements OIDTyped<String> {
	
	public static HtmlElementId from(final String id) {
		return new HtmlElementId(id);
	}
	public static HtmlElementId forId(final String id) {
		return new HtmlElementId(id);
	}
	public static HtmlElementId valueOf(final String id) {
		return new HtmlElementId(id);
	}
	public static HtmlElementId fromString(final String s) {
		return HtmlElementId.forId(s);
	}
	public static HtmlElementId supply() {
		return HtmlElementId.forId(UUID.randomUUID().toString());
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
