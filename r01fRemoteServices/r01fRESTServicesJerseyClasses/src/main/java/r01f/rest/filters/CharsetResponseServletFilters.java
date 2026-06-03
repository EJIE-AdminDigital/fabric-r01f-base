package r01f.rest.filters;

import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;
import lombok.experimental.Accessors;
import r01f.rest.RESTFilter;
import r01f.util.types.Strings;

/**
 * To use  CharsetResponseServletFilters:
 * 	 1. Anotate  at REST Resource: @CharsetResponseFilter
 *   2. Register at REST Bootstraping :
 */
public abstract class CharsetResponseServletFilters {

/////////////////////////////////////////////////////////////////////////////////////////
//	CharsetResponseServletFilterBase
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static abstract class CharsetResponseServletFilterBase
					  implements ContainerResponseFilter,
						   		 RESTFilter {
		private final String _charset;

		public CharsetResponseServletFilterBase(final String charset) {
			_charset = charset;
		}
		@Override
		public void filter(final ContainerRequestContext request, final ContainerResponseContext response) {
			MediaType type = response.getMediaType();
			if (type != null) {
				String contentType = type.toString();
				if (!contentType.contains("charset")) {
					String contentTypeAsString = Strings.customized("{};charset={}", contentType,_charset);
					response.getHeaders().putSingle("Content-Type", contentTypeAsString);
				}
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ISOCharsetResponseServletFilter
/////////////////////////////////////////////////////////////////////////////////////////
	@Provider
	@CharsetResponseFilter
	public static class ISOCharsetResponseServletFilter
				extends CharsetResponseServletFilterBase {
		public ISOCharsetResponseServletFilter() {
			super(StandardCharsets.ISO_8859_1.name());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	UTF8CharsetResponseServletFilter
/////////////////////////////////////////////////////////////////////////////////////////
	@Provider
	@CharsetResponseFilter
	public static class UTF8CharsetResponseServletFilter
				extends CharsetResponseServletFilterBase {
		public UTF8CharsetResponseServletFilter() {
			super(StandardCharsets.UTF_8.name());
		}
	}
}
