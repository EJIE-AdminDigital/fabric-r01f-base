package r01f.patterns;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Wraps a value 
 * This type is handful when returning a value from a functional interface
 * <pre class='brush:java'>
 * 		MyObj myObj = null;
 * 		_presenter.doSomething(new OnSuccessSubscriber() {
 * 									public void onSuccess(OtherObj val) {
 * 										MyObj theObj = _createMyObjFrom(val);
 * 
 * 										// myObj in an outter scope cannot be SET
 * 										myObj = theObj;		<--- THIS DOES NOT COMPILE!! 
 * 									}
 *							   });
 * </pre>
 * This problem can be overcome using this MutableValue object:
 * <pre class='brush:java'>
 * 		MutableValue<MyObj> myObjMutable = new MutableValue<>();
 * 		_presenter.doSomething(new OnSuccessSubscriber() {
 * 									public void onSuccess(OtherObj val) {
 * 										MyObj theObj = _createMyObjFrom(val);
 * 
 * 										myObjMutable.setValue(theObj); 	<-- this DOES COMPILE!
 * 									}
 *							   });
 *		MyObj theObj = myObj.getValue();	// get back the value
 * </pre>
 * 
 * @param <T>
 */
@Accessors(prefix="_")
public class MutableValue<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter @Setter private T _value;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public MutableValue() {
		// default no-args constructor
	}
	public MutableValue(final T val) {
		_value = val;
	}
	public static <V> MutableValue<V> createEmpty() {
		return new MutableValue<>();
	}
	public static <V> MutableValue<V> createFor(final V val) {
		return new MutableValue<>(val);
	}
}
