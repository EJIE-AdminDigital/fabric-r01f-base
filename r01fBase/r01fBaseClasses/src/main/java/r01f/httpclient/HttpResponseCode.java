package r01f.httpclient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;

/**
 * see http://www.iana.org/assignments/http-status-codes/http-status-codes.xml
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public enum HttpResponseCode 
 implements EnumWithCode<Integer,HttpResponseCode> {
	CONTINUE(100),
	PROCESSING(102),
	OK(200),
	CREATED(201),
	ACCEPTED(202),
	NO_CONTENT(204),
	MULTIPLE_CHOICES(300),
	MOVED_PERMANENTLY(301),
	MOVED_TEMPORARILY(302),
	SEE_OTHER(303),
	NOT_MODIFIED(304),
	USE_PROXY(305),
	TEMPORARTY_REDIRECT(307),
	PERMANENT_REDIRECT(308),
	BAD_REQUEST(400),
	UNAUTHORIZED(401),
	FORBIDEN(403),
	NOT_ACEPTABLE(406),
	FOUND(302),
	NOT_FOUND(404),
	METHOD_NOT_ALLOWED(405),
	PROXY_AUTHENTICATION_REQUIRED(407),
	REQUEST_TIMEOUT(408),
	INTERNAL_SERVER_ERROR(500),
	BAD_GATEWAY(502),
	SERVICE_UNAVAILABLE(503),
	GATEWAY_TIMEOUT(504),
	HTTP_VERSION_NOT_SUPPORTED(505),
	LOOP_DETECTED(508),
	NETWORK_AUTHENTICATION_REQUIRED(511),
	UNKNOWN(-1);
	
	@Getter private final Integer _code;
	@Getter private final Class<Integer> _codeType = Integer.class;
	
	public static HttpResponseCode of(final int code) {
		return EnumWithCode.fromCode(code,HttpResponseCode.class)
						   .orElse(UNKNOWN);
	}
	public boolean is(final int responseCode) {
		return _code.equals(responseCode);
	}
	public boolean isNOT(final int responseCode) {
		return !_code.equals(responseCode);
	}
	public boolean is100() {
		return this.isIn(CONTINUE,
						 PROCESSING);
	}
	public boolean is200() {
		return this.isIn(OK,
						 CREATED,
						 ACCEPTED,
						 NO_CONTENT);
	}
	public boolean is300() {
		return this.isIn(MULTIPLE_CHOICES,
						 MOVED_PERMANENTLY,
						 MOVED_TEMPORARILY,
						 SEE_OTHER,
						 NOT_MODIFIED,
						 USE_PROXY,
						 TEMPORARTY_REDIRECT,
						 PERMANENT_REDIRECT);
	}
	public boolean is400() {
		return this.isIn(BAD_REQUEST,
						 UNAUTHORIZED,
						 FORBIDEN,
						 FOUND,
						 NOT_FOUND,
						 METHOD_NOT_ALLOWED,
						 PROXY_AUTHENTICATION_REQUIRED,
						 REQUEST_TIMEOUT);
	}
	public boolean is500() {
		return this.isIn(INTERNAL_SERVER_ERROR,
						 BAD_GATEWAY,
						 SERVICE_UNAVAILABLE,
						 GATEWAY_TIMEOUT,
						 HTTP_VERSION_NOT_SUPPORTED,
						 LOOP_DETECTED,
						 NETWORK_AUTHENTICATION_REQUIRED);
	}
}
