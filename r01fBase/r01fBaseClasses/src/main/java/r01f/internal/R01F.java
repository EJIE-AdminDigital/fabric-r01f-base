package r01f.internal;

import java.nio.charset.Charset;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Environment properties
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class R01F {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	public static final String ENCODING_UTF_8 = "UTF-8";
	public static final String ENCODING_ISO_8859_1 = "ISO-8859-1";
	
	public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	public static final Charset ISO8859_1_CHARSET = Charset.forName("ISO-8859-1");
	public static final Charset DEFAULT_CHARSET = UTF8_CHARSET;
	
	public static final int CORE_GROUP = 0;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static void initSystemEnv() {
		log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		log.info("Setting base system properties:");
		log.info("networkaddress.cache.ttl=10");
		log.info("file.encoding={}",DEFAULT_CHARSET.name());
		log.info("mail.mime.charset={}",DEFAULT_CHARSET.name());
		log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		
		// set all encodings to UTF-8 (independent of the underlying OS) to provide a stable environment
		System.setProperty("file.encoding",DEFAULT_CHARSET.name());
		System.setProperty("mail.mime.charset",DEFAULT_CHARSET.name());
		
		// if DNS round robin based services are going to be used (like amazon S3 or a mail cluster),
		// don't forget to configure the DNS cache tiemout of Java (which is also infinite by default)
		java.security.Security.setProperty("networkaddress.cache.ttl","10"); 	// Only cache DNS lookups for 10 seconds
	}
}
