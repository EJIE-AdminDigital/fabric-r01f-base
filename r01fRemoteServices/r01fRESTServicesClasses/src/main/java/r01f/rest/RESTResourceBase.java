package r01f.rest;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Context;

import com.google.common.reflect.TypeToken;

import lombok.RequiredArgsConstructor;
import r01f.rest.resources.delegates.RESTDelegate;

/**
 * REST Resource base type
 */
@RequiredArgsConstructor
public abstract class RESTResourceBase
           implements RESTResource {
/////////////////////////////////////////////////////////////////////////////////////////
//  INJECTED PER REQUEST
/////////////////////////////////////////////////////////////////////////////////////////
	@Context
	protected HttpServletRequest _req;		// injected per request
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A persistence / search / index / etc delegate
	 */
	protected final RESTDelegate _delegate;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public <T extends RESTDelegate> T getDelegateAs(final Class<T> type) {
		return (T)_delegate;
	}
	@SuppressWarnings("unchecked")
	public <T extends RESTDelegate> T getDelegateAs(final TypeToken<T> type) {
		return (T)_delegate;
	}
}
