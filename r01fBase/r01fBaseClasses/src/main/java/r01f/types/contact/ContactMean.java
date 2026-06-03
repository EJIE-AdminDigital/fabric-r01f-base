package r01f.types.contact;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.collections.CollectionUtils;

public interface ContactMean
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
}