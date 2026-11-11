package r01f.types.contact;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.collections.CollectionUtils;

@MarshallPolymorphicTypeInfo
public interface IsContactMean
		 extends CanBeRepresentedAsString,
    		   	 Serializable  {
/////////////////////////////////////////////////////////////////////////////////////////
//	UTIL
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public static <M extends ContactMeanDataBase<?>> Collection<M> findMatching(final Collection<? extends ContactMeanDataBase<?>> col,
															   					final Predicate<M> predicate) {
		return CollectionUtils.hasData(col) ? col.stream()
										   		 .filter(el -> predicate.test((M)el))
										   		 .map(el -> (M)el)
										 		 .collect(Collectors.toList())
										 	: null;
	}
	@SuppressWarnings("unchecked")
	public static <M extends ContactMeanDataBase<?>> M find(final Collection<? extends ContactMeanDataBase<?>> col,
													 		final ContactInfoUsage usage) {
		M out = null;
		if (CollectionUtils.hasData(col)) out = (M)CollectionUtils.of(col)
														     	  .findFirstElementMatching(el -> el.getUsage() == usage);
		return out;
	}
	@SuppressWarnings("unchecked")
	public static <M extends ContactPhone> M find(final Collection<? extends ContactPhone> col,
												  final ContactPhoneType type) {
		M out = null;
		if (CollectionUtils.hasData(col)) out = (M)CollectionUtils.of(col)
														     	  .findFirstElementMatching(el -> el.getType() == type);
		return out;
	}
	@SuppressWarnings("unchecked")
	public static <M extends ContactMeanDataBase<?>> M findDefault(final Collection<? extends ContactMeanDataBase<?>> col) {
		M out = null;
		if (CollectionUtils.hasData(col)) out = (M)CollectionUtils.of(col)
														     	  .findFirstElementMatching(ContactMeanDataBase::isDefault);
		return out;
	}
	@SuppressWarnings("unchecked")
	public static <M extends ContactPhone> M findDefaultForType(final Collection<? extends ContactPhone> col,
																final ContactPhoneType type) {
		M out = null;
		if (CollectionUtils.hasData(col)) out = (M)CollectionUtils.of(col)
																  .findFirstElementMatching(el -> el.isDefault() && el.getType() == type);
		return out;
	}
	@SuppressWarnings("unchecked")
	public static <M extends ContactPhone> M findOtherThanDefaultForType(final Collection<? extends ContactPhone> col,
															  			 final ContactPhoneType type) {
		M out = null;
		if (CollectionUtils.hasData(col)) out = (M)CollectionUtils.of(col)
														     	  .findFirstElementMatching(el -> !el.isDefault() && el.getType() == type);
		return out;
	}
	@SuppressWarnings("unchecked")
	public static <M extends ContactMeanDataBase<?>> M findOne(final Collection<? extends ContactMeanDataBase<?>> col) {
		M out = null;
		if (CollectionUtils.hasData(col)) out = (M)CollectionUtils.of(col)
																  .pickOneElement();
		return out;
	}
	@SuppressWarnings("unchecked")
	public static <M extends ContactMeanDataBase<?>> M findOtherThanDefault(final Collection<? extends ContactMeanDataBase<?>> col) {
		M out = null;
		if (CollectionUtils.hasData(col)) out = (M)CollectionUtils.of(col)
														     	  .findFirstElementMatching(el -> !el.isDefault());
		return out;
	}
	public static <M extends ContactMeanDataBase<?>> Collection<M> orderDefaultFirst(final Collection<M> col) {
		if (CollectionUtils.isNullOrEmpty(col)) return col;
		return col.stream()
				  .sorted((m1,m2) -> {
							  if (m1.isDefault() && !m2.isDefault()) return -1;
							  if (!m1.isDefault() && m2.isDefault()) return 1;
							  return 0;
				  		  })
				  .collect(Collectors.toList());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public default ContactMeanType getType() {
		return ContactMeanType.from(this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public default boolean isEMail() {
		return this.getType().is(ContactMeanType.EMAIL);
	}
	public default boolean isPhone() {
		return this.getType().is(ContactMeanType.PHONE);
	}
	public default boolean isOwnedContactMean() {
		return this instanceof OwnedContactMean;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public default EMail asEMail() {
		return this.as(EMail.class);
	}
	public default Phone asPhone() {
		return this.as(Phone.class);
	}
	@SuppressWarnings("unchecked")
	public default <C extends IsContactMean> OwnedContactMean<C> asOwnedContactMean(final Class<C> meanType) {
		return (OwnedContactMean<C>)this;
	}
	@SuppressWarnings("unchecked")
	public default <C extends IsContactMean> C as(final NotificationMedium medium) {
		return switch(medium) {
					case EMAIL: yield (C)this.as(EMail.class);
					case SMS: yield (C)this.as(Phone.class);
					default: throw new IllegalArgumentException("Unknown notification medium " + medium);
				};
		
	}
	public default <C extends IsContactMean> C as(final Class<C> type) {
		return type.cast(this);
	}
}