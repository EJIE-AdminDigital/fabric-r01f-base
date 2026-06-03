package r01f.rest.exceptions;

import jakarta.ws.rs.WebApplicationException;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
public class RESTTooManyRequestsException extends WebApplicationException {
	
	private static final long serialVersionUID = -4180188877682855051L;
	
	@Getter private final long _secondsToRetry;

    public RESTTooManyRequestsException(final String message,final long secondsToRetry) {
        super(message);
        _secondsToRetry = secondsToRetry;
    }
}
