package r01f.exceptions;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;

@RequiredArgsConstructor
public abstract class EnrichedThrowableTypeBuilder<T extends EnrichedThrowableType>
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public EnrichedThrowableTypeBuilderCodesStep withName(final String name) {
		return new EnrichedThrowableTypeBuilderCodesStep(name);
	}
	public EnrichedThrowableTypeBuilderCodesStep of(final Class<? extends Throwable> thType) {
		return this.withName(thType.getSimpleName());
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class EnrichedThrowableTypeBuilderCodesStep {
		private final String _name;
		
		public EnrichedThrowableTypeBuilderSeverityStep coded(final int group,final int code) {
			return new EnrichedThrowableTypeBuilderSeverityStep(_name,
																group,code);
		} 
		public EnrichedThrowableTypeBuilderSeverityStep noCodes() {
			return new EnrichedThrowableTypeBuilderSeverityStep(_name,
																0,0);
		}
		public EnrichedThrowableTypeBuilderBuildStep severity(final ExceptionSeverity severity) {
			return new EnrichedThrowableTypeBuilderBuildStep(_name,
															 0,0,
															 severity);
		}
		public T build() {
			return new EnrichedThrowableTypeBuilderBuildStep(_name,
															 0,0,
															 ExceptionSeverity.DEFAULT)
							.build();
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class EnrichedThrowableTypeBuilderSeverityStep {
		private final String _name;
		private final int _group;
		private final int _code;
		
		public EnrichedThrowableTypeBuilderBuildStep severity(final ExceptionSeverity severity) {
			return new EnrichedThrowableTypeBuilderBuildStep(_name,
															 _group,_code,
															 severity);
		}
		public T build() {
			return this.buildWithSeverity(ExceptionSeverity.DEFAULT);
		}
		public T buildWithSeverity(final ExceptionSeverity severity) {
			return this.severity(severity)
					   .build();
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class EnrichedThrowableTypeBuilderBuildStep {
		private final String _name;
		private final int _group;
		private final int _code;
		private final ExceptionSeverity _severity;
		
		public T build() {
			return _build(_name,
						  _group,_code,
						  _severity);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//																			  
/////////////////////////////////////////////////////////////////////////////////////////
	protected abstract T _build(final String name,
								final int group,final int code,
								final ExceptionSeverity severity);
}
