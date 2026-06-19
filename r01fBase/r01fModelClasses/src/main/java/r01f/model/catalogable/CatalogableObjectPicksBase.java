package r01f.model.catalogable;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.HasOID;
import r01f.facets.Summarizable;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.facets.builders.SummarizableBuilder;
import r01f.model.PersistableModelObjectBase;
import r01f.model.catalogable.CatalogableIDs.CatalogableObjectTypeID;
import r01f.model.catalogable.CatalogableOIDs.CatalogableObjectOID;
import r01f.model.catalogable.CatalogableOIDs.CatalogablePickOID;
import r01f.model.catalogable.CatalogableOIDs.CatalogablePicksOID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.summary.SummaryBuilder;
import r01f.util.types.collections.CollectionUtils;
import r01f.validation.ObjectValidationResult;
import r01f.validation.SelfValidates;

/**
 * Models the label in structure picks for a catalogable object
 */
@Accessors(prefix="_")
public abstract class CatalogableObjectPicksBase<O extends CatalogablePicksOID,P extends HasOID<? extends CatalogablePickOID>,
											     SELF_TYPE extends CatalogableObjectPicksBase<O,P,SELF_TYPE>> 
              extends PersistableModelObjectBase<O,SELF_TYPE>		
           implements ContainsPicksForCatalogableObject<P>,
           			  HasSummaryFacet,
  			   		  SelfValidates<SELF_TYPE> {

	private static final long serialVersionUID = -5164832134430402658L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The catalogable object oid
	 */
	@MarshallField(as="targetObjectOid")	// DO NOT map as attribute since it's an abstract / interface type
	@Getter @Setter protected CatalogableObjectOID _catalogableObjectOid;
	/**
	 * The catalogable object type
	 */
	@MarshallField(as="onTargetObjectOfType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected CatalogableObjectTypeID _catalogableObjectType;
	/**
	 * The label in structure picks for the catalogable object 
	 */
	@MarshallField(as="picks",
				   whenXml=@MarshallFieldAsXml(collectionElementName="pick"))
	@Getter @Setter protected Set<P> _picks;
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected CatalogableObjectPicksBase() {
		// default no-args constructor
	}
	protected CatalogableObjectPicksBase(final CatalogableObjectPicksBase<O,P,?> other) {
		_oid = other.getOid();
		_catalogableObjectOid = other.getCatalogableObjectOid();
		_catalogableObjectType = other.getCatalogableObjectType();
		_picks = other.getPicks();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SIZE
/////////////////////////////////////////////////////////////////////////////////////////
	public int size() {
		return _picks != null ? _picks.size() : 0;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	ADD PICKS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void addPick(final P pick) {
		if (_picks == null) _picks = Sets.newLinkedHashSet();
		_picks.add(pick);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	SUMMARY
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Summarizable asSummarizable() {
		// if no picks return just the oid
		if (CollectionUtils.isNullOrEmpty(this.getPicks())) {
			return SummarizableBuilder.summarizableFrom(SummaryBuilder.languageInDependent()
																 .create("{} of {}",
																		 this.getOid(),
																		 this.getCatalogableObjectOid()));
		}
		// create a picks summary concatenating each pick memo code
		String picksSummary = this.picksMemoCodesSeparatedWith('/');
		// return the summary as oid > picksSummary
		return SummarizableBuilder.summarizableFrom(SummaryBuilder.languageInDependent()
														 .create("{} of {} > {}",
																 this.getOid(),
																 this.getCatalogableObjectOid(),picksSummary));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	public abstract String getPickMemoCode(final P pick);
	
	public Collection<String> picksMemoCodes() {
		if (CollectionUtils.isNullOrEmpty(_picks)) return Lists.newArrayListWithExpectedSize(0);
		return this.getPicks()
				   .stream()
				   .map(this::getPickMemoCode)
				   .collect(Collectors.toList());
	}
	public String picksMemoCodesSeparatedWith(final char character) {
		// create a picks summary concatenating each pick memo code
		Collection<String> picksMemos = this.getPicks()
											.stream()
											.map(this::getPickMemoCode)
											.collect(Collectors.toList());
		return CollectionUtils.toStringSeparatedWith(picksMemos,character);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	VALIDATE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override @SuppressWarnings("unchecked")
	public ObjectValidationResult<SELF_TYPE> validate() {
		return CatalogableObjectsValidators.<O,P,SELF_TYPE>createCatalogableObjectPicksBaseValidator()
										   .validate((SELF_TYPE)this);
	}
}
