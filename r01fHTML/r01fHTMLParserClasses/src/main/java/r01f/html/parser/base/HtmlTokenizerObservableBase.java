package r01f.html.parser.base;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
public abstract class HtmlTokenizerObservableBase<TK extends HtmlParserTokenBase<?>,
											      TN extends HtmlTokenizerBase<?,TK,?,?>>	 
	 		  extends HtmlTokenizerEmitter<TK,TN,ObservableEmitter<TK>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected HtmlTokenizerObservableBase(final TN tokenizer) {
		super(tokenizer);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected boolean emitterAvailable(final ObservableEmitter<TK> emitter) {
		return !emitter.isDisposed();
	}
	protected Observable<TK> createObservable() {
		return Observable.create(
					new ObservableOnSubscribe<TK>() {
							@Override
							public void subscribe(final ObservableEmitter<TK> emitter) throws Exception {
								HtmlTokenizerObservableBase.super.subscribe(emitter);
							}
				});		// do not buffer!	
	}
}
