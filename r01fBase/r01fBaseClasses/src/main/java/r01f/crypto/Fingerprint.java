package r01f.crypto;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Objects; 

/**
 * Identity Fingerprint is a compact, probabilistic, and irreversible representation of a text field (such as a person's full name).

	Unlike a traditional cryptographic hash (like SHA-256), which is rigid and designed to change completely if a single character varies, a Fingerprint is elastic. It is engineered to preserve structural and typographical similarities, allowing systems to detect matching records even when they contain typos, spacing issues, or minor spelling variations.
	
	How It Works Under the Hood
	----------------------------------------
		[1] The Fingerprint class utilizes a Guava Bloom Filter combined with text fragmentation (bigrams) to map the structure of a name into a hexadecimal string payload.
	
		[2] Tokenization (Bigrams): The input text is cleaned and broken down into overlapping pairs of characters (e.g., "inigo" becomes ["in", "ni", "ig", "go"]).
	
		[3]  Bit Mapping: Each pair is processed by a fast, uniform hashing function (Murmur32) that flips specific bits to 1 inside a fixed-size bit array (the Bloom Filter).
	
	Hex Serialization: This array of bits is converted into a compact Hexadecimal String—this is your final Fingerprint payload.
 */
@Accessors(prefix="_")
public class Fingerprint 
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = -6704690821326019085L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter  private final String _theFingerprint;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Fingerprint(final String fingerprintStr) {
		_theFingerprint = fingerprintStr;
	}
	public static Fingerprint from(final String fingerprintStr) {
		return new Fingerprint(fingerprintStr);
	}
	public static Fingerprint valueOf(final String fingerprintStr) {
		return Fingerprint.from(fingerprintStr);
	}
	public static Fingerprint fromString(final String fingerprintStr) {
		return Fingerprint.from(fingerprintStr);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CREATE FINGERPRINTS (BloomFilter Guava)
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Crea una firma elástica (Fingerprint) en formato HEX basado en un Filtro de Bloom.
	 * Configurado de forma óptima para nombres (20 elementos esperados, 5% de error para optimizar espacio).
	 */
	public static Fingerprint createBloomFilterFrom(final CharSequence str) {
		if (str == null) throw new IllegalArgumentException("Cannot create a fingerprint from a null string");
		
		// [1] Frangment Bigramas
		List<String> bigramas = _generarBigramas(str.toString());
		
		// 2. Creación del Filtro de Bloom (20 insertions, 5% fpp para un tamaño Hex más compacto)
		BloomFilter<CharSequence> filter = BloomFilter.create(	Funnels.stringFunnel(StandardCharsets.UTF_8), 20, 0.05
		);
		
		for (String bigrama : bigramas) {
			filter.put(bigrama);
		}
		
		// 3. Serialización del filtro a bytes y conversión a String HEX
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			filter.writeTo(out);
			byte[] bytesFiltro = out.toByteArray();
			
			String hexString = HexFormat.of().formatHex(bytesFiltro);
			return Fingerprint.from(hexString);
			
		} catch (final IOException ex) {
			throw new IllegalStateException("Error serializing the BloomFilter instance into the Fingerprint payload", ex);
		}
	}
	
	
	public double calculateSimilarityWith(final CharSequence localName) {
		if (localName == null || this.asString() == null) return 0.0;
		
		try {
			// [1]. Reconstruct  BloomFilter from input name
			byte[] bytesFiltro = HexFormat.of().parseHex(this.asString());
			java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(bytesFiltro);
			
			// [2] Deserialze using Funnel
			BloomFilter<CharSequence> reconstructedFilter = BloomFilter.readFrom(in, 
																				Funnels.stringFunnel(StandardCharsets.UTF_8)
			);
			
			//[3] Frangment into bigrams
			List<String> bigramasLocales = _generarBigramas(localName.toString());
			if (bigramasLocales.isEmpty()) return 0.0;
			
			// 3. Contar cuántos bigramas locales "viven" dentro del filtro recibido
			long coincidencias = 0;
			for (String bigrama : bigramasLocales) {
				if (reconstructedFilter.mightContain(bigrama)) {
					coincidencias++;
				}
			}
			
			// 4. Calcular el ratio de éxito (coincidencias / total de fragmentos)
			return (double) coincidencias / bigramasLocales.size();
			
		} catch (final Throwable ex) {
			throw new IllegalStateException("Could not reconstruct BloomFilter from HEX string", ex);
		}
	}

	/**
	 * Fragmentador interno utilitario
	 */
	private static List<String> _generarBigramas(final String texto) {
		String cleaned = texto.strip().toLowerCase();
		List<String> bigramas = new ArrayList<>();
		for (int i = 0; i < cleaned.length() - 1; i++) {
			bigramas.add(cleaned.substring(i, i + 2));
		}
		return bigramas;
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	CanBeRepresentedAsString
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return _theFingerprint;
	}
	@Override
	public String toString() {
		return this.asString();
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	IS
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final Fingerprint other) {
		return this.equals(other);
	}
	public boolean isNOT(final Fingerprint other) {
		return !this.is(other);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof Fingerprint)) return false;
		
		Fingerprint other = (Fingerprint)obj;		
		return Objects.areEqual(this.getTheFingerprint(), other.getTheFingerprint(),
								String::equals);
	}
	@Override
	public int hashCode() {
		return _theFingerprint != null ? _theFingerprint.hashCode() : super.hashCode();
	}
}