package r01f.types.url.web;

import lombok.Getter;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.OIDTyped;

/**
 * The window open target: https://www.w3schools.com/tags/att_a_target.asp
 * Pre-defined targets:
 * <pre class='brush:java'>
 * 		WebLinkOpenTarget tgt = WebLinkOpenTarget.SELF;
 * </pre>
 * For a frame target:
 * <pre class='brush:java'>
 * 		WebLinkOpenTarget frmTgt = WebLinkOpenTarget.frameNamed("my-frame");
 * </pre>
 */
@MarshallType(as="openTaget")
public record WebLinkOpenTarget(@Getter String id)
   implements OIDTyped<String> {

	private static final long serialVersionUID = 5416033585280880913L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	public static WebLinkOpenTarget BLANK = WebLinkOpenTarget.forId("_blank");
	public static WebLinkOpenTarget SELF = WebLinkOpenTarget.forId("_self");
	public static WebLinkOpenTarget TOP = WebLinkOpenTarget.forId("_top");
	public static WebLinkOpenTarget PARENT = WebLinkOpenTarget.forId("_parent");
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static WebLinkOpenTarget from(final String id) {
		return new WebLinkOpenTarget(id);
	}
	public static WebLinkOpenTarget forId(final String id) {
		return new WebLinkOpenTarget(id);
	}
	public static WebLinkOpenTarget valueOf(final String id) {
		return WebLinkOpenTarget.forId(id);
	}
	public static WebLinkOpenTarget fromString(final String s) {
		return WebLinkOpenTarget.forId(s);
	}
	public static WebLinkOpenTarget forIdOrNull(final String id) {
		if (id == null) return null;
		return new WebLinkOpenTarget(id);
	}
	public static WebLinkOpenTarget frameNamed(final String frameName) {
		return new WebLinkOpenTarget(frameName);
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
