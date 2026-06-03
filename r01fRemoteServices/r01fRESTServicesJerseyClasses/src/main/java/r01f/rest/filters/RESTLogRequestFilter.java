package r01f.rest.filters;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import r01f.rest.RESTFilter;
import r01f.util.types.Strings;



/**
 * A REST endpoint request filter that logs
 * To configure the filter put it at the web.xml:
 * <pre class='brush:xml'>
 * 		<init-param>
 *	       	<param-name>com.sun.jersey.spi.container.ContainerRequestFilters</param-name>
 *	       	<param-value>r01f.rest.RESTLogRequestFilter</param-value>
 *	   	</init-param>
 * </pre>
 */
@Slf4j
@Provider
@Priority(1) // To set priority execution of filter - Log filter always first
@RESTLogFilter
public class RESTLogRequestFilter
  implements ContainerRequestFilter,
  			 RESTFilter {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {
		String baseUri = requestContext.getUriInfo().getBaseUri().toString();
		String method = requestContext.getMethod();
		String path = requestContext.getUriInfo().getAbsolutePath().toString();
		MediaType mediaType = requestContext.getMediaType();
		String dbg = Strings.customized("\r\n" +
										"\r\nLog FILTER RESTEndPoint: {}"  +
				  						"\r\n---------------------------------------------------------------------"  +
				  						"\r\n      Method: {}"  +
				  						"\r\n        Path: {}" +
				  						"\r\nContent-Type: {}" +
				  						"\r\n---------------------------------------------------------------------",
						  				baseUri,
						  				method,
						  				path,
						  				mediaType != null ? mediaType
						  					   			  : "not specified");
		log.info(dbg);
	}
}
