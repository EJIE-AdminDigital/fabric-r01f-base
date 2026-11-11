package r01f.types.collections;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.HasID;
import r01f.facets.Tagged;
import r01f.facets.Tagged.HasTaggeableFacet;
import r01f.facets.delegates.TaggeableDelegate;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.oids.JavaOIDDispenser;
import r01f.oids.OID;
import r01f.types.tag.TagList;
import r01f.util.types.Objects;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
public abstract class MetaDatedValuesCollectionItemBase<T,
														SELF_TYPE extends MetaDatedValuesCollectionItemBase<T,SELF_TYPE>>
  		   implements HasID<CollectionItemID>,
  	   	     		  HasTaggeableFacet<String> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter	protected CollectionItemID _id;

	@MarshallField(as="tags",
				   whenXml=@MarshallFieldAsXml(collectionElementName="tag"))
	@Getter @Setter protected TagList<String> _tags;

	@MarshallField(as="value")
	@Getter @Setter protected T _value;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	protected MetaDatedValuesCollectionItemBase() {
		// default no-args constructor
	}
	protected MetaDatedValuesCollectionItemBase(final T value) {
		this(value,null);
	}
	protected MetaDatedValuesCollectionItemBase(final T value,
										        final CollectionItemID id) {
		this(value,
			 id,null);
	}
	protected MetaDatedValuesCollectionItemBase(final T value,
										        final CollectionItemID id,final TagList<String> tags) {
		_value = value;
		_id = id != null ? id : CollectionItemID.named(JavaOIDDispenser.supplyShortOid());	// ensure id is never null
		_tags = tags;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings("unchecked")
	public SELF_TYPE withId(final CollectionItemID id) {
		this.setId(id);
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE withTags(final TagList<String> tags) {
		this.setTags(tags);
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE withTags(final String... tags) {
		if (CollectionUtils.hasData(tags)) this.setTags(new TagList<>(tags));
		return (SELF_TYPE)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Tagged<String> asTaggeable() {
		return new TaggeableDelegate<>(this);
	}
	public boolean hasTag(final String tag) {
		return this.asTaggeable()
				   .hasTag(tag);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void unsafeSetId(final OID id) {
		_id = (CollectionItemID)id;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASH CODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object o) {
		if (o == null) return false;
		if (this == o) return true;
		if (!(o instanceof MetaDatedValuesCollectionItemBase other)) return false;

		boolean idEqs = Objects.areEqual(this.getId(),other.getId(),
										 (i1,i2) -> i1.is(i2));
		boolean itemEqs = Objects.areEqual(this.getValue(),other.getValue(),
										   (i1,i2) -> i1.equals(i2));
		return idEqs && itemEqs;
	}
	@Override
	public int hashCode() {
		return Objects.hash(this.getId(),this.getValue());
	}
}
