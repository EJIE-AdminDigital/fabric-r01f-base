package r01f.model.metadata;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.OIDTyped;
import r01f.types.summary.Summary;

/**
 * Indexable field unique identifier
 * Beware that the {@link FieldID} might NOT be the same as the "real" field id at the index
 * in the case of fields with multiple "dimensions" like multi-language {@link Summary} fields where each
 * language {@link Summary} is stored in a separate field with an id like: metaDataId.{dimensionId}
 * For example, a multi-language {@link Summary} field with {@link FieldMetaDataID}=r01.summary
 * is stored in multiple lucene fields, one for each document: r01.summary.es, r01.summary.eu, etc
 */
@Immutable
@MarshallType(as="indexableFieldId")
public record FieldID(@Getter String id)
   implements OIDTyped<String> {

	public static FieldID from(final FieldIDToken... tokens) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i < tokens.length; i++) {
			sb.append(tokens[i].getToken());
			if (i < tokens.length - 1) sb.append(".");
		}
		return FieldID.forId(sb.toString());
	}
	public static FieldID from(final String id) {
		return new FieldID(id);
	}
	public static FieldID forId(final String id) {
		return new FieldID(id);
	}
	public static FieldID valueOf(final String s) {
		return FieldID.forId(s);
	}
	public static FieldID fromString(final String s) {
		return FieldID.forId(s);
	}
	
	@Override
	public String asString() {
		return this.id;
	}
	@Override
	public String toString() {
		return this.id;
	}
}
