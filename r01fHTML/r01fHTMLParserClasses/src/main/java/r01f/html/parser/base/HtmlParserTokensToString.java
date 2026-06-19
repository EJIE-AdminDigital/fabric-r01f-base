package r01f.html.parser.base;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.Function;

import org.reactivestreams.Subscriber;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subscribers.ResourceSubscriber;
import lombok.extern.slf4j.Slf4j;

/**
 * An utility type that converts an stream of tokens into an string
 * <pre class='brush:java'>
 *		Flowable<HtmlParserToken> flowable = HtmlTokenizerFlowable.createFrom(charReader);
 * 		String str = HtmlParserTokensToString.from(flowable)
 * 											 .using(new Function<HtmlParserToken,String>() {
 * 															public String apply(final HtmlParserToken token) {
 * 																return token.toString();
 * 															}
 * 													});
 * </pre>
 */
@Slf4j
public class HtmlParserTokensToString<TK extends HtmlParserTokenBase<?>> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Flowable<TK> _flowable;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	private HtmlParserTokensToString(final Flowable<TK> flowable) {
		_flowable = flowable;
	}
	public static <TK extends HtmlParserTokenBase<?>> HtmlParserTokensToString<TK> from(final Flowable<TK> flowable) {
		return new HtmlParserTokensToString<>(flowable);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public String using(final Function<TK,String> tokenToString) {
		StringWriter sw = new StringWriter();
		_flowable.blockingSubscribe(_createSubscriber(sw,
													  tokenToString));
		return sw.toString();		
	}
	private Subscriber<TK> _createSubscriber(final Writer w,
											 final Function<TK,String> tokenToString) {
		return new ResourceSubscriber<>() {
							@Override
							public void onNext(final TK t) {
								String tokenText = tokenToString.apply(t);
								try {
									w.write(tokenText);
								} catch (IOException ioEx) {
									// ignored
								}
							}
							@Override
							public void onError(final Throwable th) {
								th.printStackTrace();
							}
							@Override
							public void onComplete() {
								log.info("Completed!");
								this.dispose();
							}
			   };
	}
}
