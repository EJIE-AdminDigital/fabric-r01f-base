package r01f.html.parser.base;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import lombok.experimental.Accessors;

/**
 * Base type for token flowables
 */
@Accessors(prefix="_")
public class HtmlTokenizerFlowableBase<TK extends HtmlParserTokenBase<?>,
									   TN extends HtmlTokenizerBase<?,TK,?,?>>
	 extends HtmlTokenizerEmitter<TK,TN,
	 							  FlowableEmitter<TK>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected HtmlTokenizerFlowableBase(final TN tokenizer) {
		super(tokenizer);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected boolean emitterAvailable(final FlowableEmitter<TK> emitter) {
		return !emitter.isCancelled();
	}
	protected Flowable<TK> createFlowable() {
		return Flowable.create(
					new FlowableOnSubscribe<TK>() {
							@Override
							public void subscribe(final FlowableEmitter<TK> emitter) throws Exception {
								HtmlTokenizerFlowableBase.super.subscribe(emitter);
							}
				},
				BackpressureStrategy.MISSING);	// BackpressureStrategy.BUFFER ???
	}
}
