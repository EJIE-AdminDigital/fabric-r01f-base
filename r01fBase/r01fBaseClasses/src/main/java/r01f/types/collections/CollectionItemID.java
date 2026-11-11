package r01f.types.collections;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.JavaOIDDispenser;
import r01f.oids.OID;

@MarshallType(as="metaDatedValuesCollectionItemID")
@Accessors(prefix="_")
public record CollectionItemID(String id)
   implements OID  {
	
	public static final CollectionItemID DEFAULT = new CollectionItemID("default");

	public static CollectionItemID forId(final String id) {
		return new CollectionItemID(id);
	}
	public static CollectionItemID fromString(final String id) {
		return new CollectionItemID(id);
	}
    @JsonCreator
    public static CollectionItemID valueOf(final String id) {
		return new CollectionItemID(id);
	}
	public static CollectionItemID supply() {
		return new CollectionItemID(JavaOIDDispenser.supplyShortOid());
	}
	public static CollectionItemID named(final String name) {
		return new CollectionItemID(name);
	}
	public static CollectionItemID forLang(final Language lang) {
		return new CollectionItemID(lang.getIso639_1());
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
