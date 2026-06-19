package r01f.model.persistence;

import java.util.Collection;

import com.google.common.reflect.TypeToken;

import r01f.exceptions.Throwables;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.util.types.collections.CollectionUtils;

@MarshallPolymorphicTypeInfo
public interface FindResult<T>
   		 extends PersistenceOperationOnObjectResult<Collection<T>> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override @SuppressWarnings({ "unchecked" })
	public default Class<Collection<T>> getObjectType() {
		return (Class<Collection<T>>)new TypeToken<Class<Collection<T>>>() { /* nothing */ }
											.getComponentType()
											.getRawType();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a {@link FindOK}
	 */
	public default FindOK<T> asFindOK() {
		if (this instanceof FindError<T>) throw new ClassCastException(this.getClass() + " is not an instance of " + FindOK.class);
		return (FindOK<T>)this;
	}
	/**
	 * @return a {@link FindError}
	 */
	public default FindError<T> asFindError() {
		if (this instanceof FindOK<T>) throw new ClassCastException(this.getClass() + " is not an instance of " + FindError.class);
		return (FindError<T>)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * When a single result is expected, this method returns this entity
	 * @return
	 */
	public default T getSingleExpectedOrThrow() {
		// error
		if (this instanceof FindError<T> err) {
			throw err.getCOREServiceException();	
		}
		// OK
		else if (this instanceof FindOK<T> ok) {
			T outEntity = null;
			Collection<T> entities = this.getOrThrow();
			if (CollectionUtils.hasData(entities)) {
				outEntity = CollectionUtils.of(entities).pickOneAndOnlyElement("A single instance of {} was expected to be found BUT {} were found",
																			   ok.getFoundObjectType(),entities.size());
			} else {
				throw new IllegalStateException(Throwables.message("A single instance of {} was expected to be found BUT NONE were found",
																   ok.getFoundObjectType()));
			}
			return outEntity;
		} else {
			throw new IllegalStateException("Unknown state");
		}
	}
}
