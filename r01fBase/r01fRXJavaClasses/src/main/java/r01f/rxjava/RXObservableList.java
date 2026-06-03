package r01f.rxjava;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * Class for getting an Observable over a dynamic list. 
 * The observable emits a new element when it is added to the list
 * Usage:
 * <pre class='brush:java'>
 * 		// Create the observable list
 * 		RXObservableList<String> observableListOfStrings = new RXObservableList();
 * 		observableListOfStrings.subscribe(new Observer<String>() {
 * 												@Override
 *												public void onNext(final String item) {
 *													System.out.println(">>Item added: " + item);
 *												}
 *												...
 * 										  });
 * 		// emit
 * 		observableListOfStrings.addAll(Lists.newArrayList("one","two","three"));
 * </pre>
 * @param <T>
 */
public class RXObservableList<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  Fields
/////////////////////////////////////////////////////////////////////////////////////////	 
	protected final List<T> _list;
	protected final PublishSubject<T> _onAdd;	// it's both an Observable (emits onNext events) and an Observer (listen for onNext events)	
	 
/////////////////////////////////////////////////////////////////////////////////////////
//  Constructor
/////////////////////////////////////////////////////////////////////////////////////////
	 public RXObservableList() {
		 _list = new LinkedList<>();
		 _onAdd = PublishSubject.create();
	 } 
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	 
	 public void add(final T value) {
		 _list.add(value);
		 _onAdd.onNext(value);
	 }
	 public void addAll(final Collection<T> collection) {
		 // just call to R01MObservableList.add on every given collection item
		 Observable.fromIterable(collection)
		 		   .subscribe(value -> RXObservableList.this.add(value));		// action
	 }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	 public Observable<T> getObservable() {
		 return _onAdd;
	 }
	 public void subscribe(final Observer<T> observer) {
		 _onAdd.subscribe(observer);
	 }
}
