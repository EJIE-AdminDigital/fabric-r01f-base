package r01f.servlet;

import com.google.common.collect.Iterators;

import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class HttpServletSessionUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//	COMPOSES A SESSION DEBUG INFO
/////////////////////////////////////////////////////////////////////////////////////////	
	public static String composeSessionDebugInfo(final HttpSession session) {
		if (session == null) return "";
		StringBuilder dbg = new StringBuilder();
		dbg.append("[web session] id=").append(session.getId()).append("\n");
		Iterable<String> sessAttrsIt = () -> Iterators.forEnumeration(session.getAttributeNames());
		for (String sesAttr : sessAttrsIt) {
			dbg.append("\t-").append(sesAttr).append(" = ").append(session.getAttribute(sesAttr));
		}
		return dbg.toString();
	}
}
