package r01f.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.oids.SecurityIDs.SecurityToken;
import r01f.types.Path;


/**
 * Create keys using:
 * <pre>
 * 		CryptoKeyPairBuilder.createRSA()
 * </pre>
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class CryptoKeys {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Load a PKCS8 key
	 * @param keyFilePath The path of the key
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * */
	public static PrivateKey loadPKCS8PrivateKey(final Path keyFilePath) throws IOException, 
																				InvalidKeySpecException, 
																				NoSuchAlgorithmException {
		try (RandomAccessFile raf = new RandomAccessFile(keyFilePath.asAbsoluteString(),"r")) {
			byte[] buf = new byte[(int)raf.length()];
			raf.readFully(buf);
			
			PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(buf);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePrivate(kspec);
		} 
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LOAD FROM CLASSPATH
/////////////////////////////////////////////////////////////////////////////////////////
	public static <K extends RSAKey> K loadRSAKeyFromClasspath(final Path keyClasspahtLocation,
															   final CryptoKeySpecFactory keySpecFactory,
															   final CryptoKeyFactoryFromSpec<K> keyFactoryFromSpec) {
		KeySpec keySpec = CryptoKeys.loadRSAKeySpecFromClasspath(keyClasspahtLocation,
																 keySpecFactory);
		return CryptoKeys.rsaKeyFromKeySpec(keySpec,
											keyFactoryFromSpec);
	}
	public static KeySpec loadRSAKeySpecFromClasspath(final Path keyClasspahtLocation,
											   		  final CryptoKeySpecFactory keySpecFactory) {
		try {
			// TODO use ResourceLoader
			InputStream inputStream = CryptoKeys.class.getClassLoader()
													  .getResourceAsStream(keyClasspahtLocation.asRelativeString());		// BEWARE! relative

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[1024];
			while ((nRead = inputStream.read(data,0,data.length)) != -1) {
				buffer.write(data,0,nRead);
			}
			buffer.flush();
			byte[] byteArray = buffer.toByteArray();

			KeySpec keySpec = keySpecFactory.from(byteArray);		// private key: PKCS8EncodedKeySpec
																	// public key:  X509EncodedKeySpec
			return keySpec;
		} catch (IOException ioEx) {
			log.error("Error while trying to load the key: {}",
					  ioEx.getMessage(),ioEx);
		}
		return null;	// !!
	}
	public static KeySpec keySpecFromSecurityToken(final SecurityToken securityToken,
												   final CryptoKeySpecFactory keySpecFactory) {
		byte[] byteArray = securityToken.getBytes();
		KeySpec keySpec = keySpecFactory.from(byteArray);		// private key: PKCS8EncodedKeySpec
																// public key:  X509EncodedKeySpec
		return keySpec;
	}
	public static <K extends RSAKey> K rsaKeyFromKeySpec(final KeySpec keySpec,
														 final CryptoKeyFactoryFromSpec<K> keyFactoryFromSpec) {
		K key = keyFactoryFromSpec.from(keySpec);	// private key: (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(keySpec)
													//  public key: (RSAPublicKey)KeyFactory.getInstance("RSA").generatePublic(keySpec)
		return key;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PRIVATE KEY: PKCS8EncodedKeySpec
/////////////////////////////////////////////////////////////////////////////////////////
	public static CryptoKeySpecFactory PRIVATE_KEY_SPEC_FACTORY = byteArray -> new PKCS8EncodedKeySpec(byteArray);
	public static CryptoKeyFactoryFromSpec<RSAPrivateKey> PRIVATE_KEY_FROM_SPEC_FACTORY = keySpec -> {
																								try {
																									return (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(keySpec);
																								} catch (InvalidKeySpecException ikExc) {
																									log.error("Error on creating PRIVATE KEY: {}",
																											  ikExc.getMessage(),ikExc);
																								} catch (NoSuchAlgorithmException nsaExc) {
																									log.error("Error on creating PRIVATE KEY: {}",
																											  nsaExc.getMessage(),nsaExc);
																								}
																								return null;
																						  };
/////////////////////////////////////////////////////////////////////////////////////////
//	PUBLIC KEY: X509EncodedKeySpec
/////////////////////////////////////////////////////////////////////////////////////////
	public static CryptoKeySpecFactory PUBLIC_KEY_SPEC_FACTORY = byteArray -> new X509EncodedKeySpec(byteArray);
	public static CryptoKeyFactoryFromSpec<RSAPublicKey> PUBLIC_KEY_FROM_SPEC_FACTORY = keySpec -> {
																								try {
																									return (RSAPublicKey)KeyFactory.getInstance("RSA").generatePublic(keySpec);
																								} catch (InvalidKeySpecException ikExc) {
																									log.error("Error on creating PRIVATE KEY: {}",
																											  ikExc.getMessage(),ikExc);
																								} catch (NoSuchAlgorithmException nsaExc) {
																									log.error("Error on creating PRIVATE KEY: {}",
																											  nsaExc.getMessage(),nsaExc);
																								}
																								return null;
																						};

}
