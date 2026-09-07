package r01f.crypto;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.patterns.Memoized;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Objects;

@Accessors(prefix="_")
public class Hash 
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = 9127842088713151940L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final String _theHash;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Hash(final String hash) {
		_theHash = hash;
	}
	public static Hash from(final String hashStr) {
		return new Hash(hashStr);
	}
	public static Hash valueOf(final String hashStr) {
		return Hash.from(hashStr);
	}
	public static Hash fromString(final String hashStr) {
		return Hash.from(hashStr);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CREATE HASHES
/////////////////////////////////////////////////////////////////////////////////////////	
	private static final transient Memoized<MessageDigest> _digest = Memoized.using(() -> {
																						MessageDigest digest = null;
																						try {
																							digest = MessageDigest.getInstance("SHA-256");
																						} catch (final Exception ex) {
																							/* ignored */
																						}
																						return digest;
																				    });
	public static Hash createSHA256From(final CharSequence str) {
		MessageDigest digest = _digest.get();
		if (digest == null) throw new IllegalStateException("Cannot create a SHA-256 hash because the MessageDigest instance could not be created");
		
		// create the hash
		Charset utf8Charset = StandardCharsets.UTF_8;
		byte[] hashBytes = digest.digest(str.toString()
											.getBytes(utf8Charset));
		// Convert byte array to a readable Hex string (Java 17+)
		String hexString = HexFormat.of().formatHex(hashBytes);
		return Hash.from(hexString);
	}
	
	public static Hash createSHA256AsBase64From(final CharSequence str) {
	    MessageDigest digest = _digest.get();
	    if (digest == null) {
	        throw new IllegalStateException("Cannot create a SHA-256 hash because the MessageDigest instance could not be created");
	    }
	    
	  // create the hash
	    Charset utf8Charset = StandardCharsets.UTF_8;
	    byte[] hashBytes = digest.digest(str.toString().getBytes(utf8Charset));
	    
	 // Convert byte array to a Base64UrlEncoding
	    String base64UrlString = Base64.getUrlEncoder()
	                                   .withoutPadding()
	                                   .encodeToString(hashBytes);
	    return Hash.from(base64UrlString);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CanBeRepresentedAsString
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return _theHash;
	}
	@Override
	public String toString() {
		return this.asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	IS
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final Hash other) {
		return this.equals(other);
	}
	public boolean isNOT(final Hash other) {
		return !this.is(other);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof Hash)) return false;
		
		Hash other = (Hash)obj;		
		return Objects.areEqual(this.getTheHash(),other.getTheHash(),
								String::equals);
	}
	@Override
	public int hashCode() {
		return _theHash != null ? _theHash.hashCode() : super.hashCode();
	}
}
