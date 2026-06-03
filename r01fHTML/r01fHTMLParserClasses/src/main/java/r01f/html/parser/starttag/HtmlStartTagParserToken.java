package r01f.html.parser.starttag;

import lombok.experimental.Accessors;
import r01f.html.parser.base.HtmlParserTokenBase;

@Accessors(prefix="_")
public class HtmlStartTagParserToken
     extends HtmlParserTokenBase<HtmlStartTagParserTokenType> {
	private static final long serialVersionUID = 5497485262050809478L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HtmlStartTagParserToken(final HtmlStartTagParserTokenType type,final String text) {
		super(type,text);
	}
}
