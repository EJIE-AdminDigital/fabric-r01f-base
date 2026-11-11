package r01f.util.types;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.enums.EnumWithCode;
import r01f.locale.Language;
import r01f.oids.OID;
import r01f.patterns.FactoryFrom;
import r01f.types.Path;
import r01f.types.StringBase64;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;
import r01f.util.types.StringConverter.StringConverterFilter;
import r01f.util.types.collections.CollectionUtils;

public class StringConverterWrapper {
/////////////////////////////////////////////////////////////////////////////////////////
//  FINAL
/////////////////////////////////////////////////////////////////////////////////////////
	private final CharSequence _theString;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public StringConverterWrapper(final CharSequence str) {
		_theString = str;
	}
	public static StringConverterWrapper wrap(final CharSequence str) {
		return new StringConverterWrapper(str);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class StringConverterDefaultOrThrowStep<T> {
		private final T _value;
		private final T _def;

		public T orDefault() {
			return _value != null ? _value : _def;
		}
		public T orDefault(final T def) {
			return _value != null ? _value
								  : def != null ? def : _def;
		}
		public T orNull() {
			return _value != null ? _value
								  : null;
		}
		public T orThrow() {
			return this.orThrow(null);
		}
		public T orThrow(final String msg,final Object... params) {
			if (_value == null) {
				throw new IllegalArgumentException(Strings.isNOTNullOrEmpty(msg) ? Strings.customized(msg,params)
																									 : "Null or empty string");
			}
			return _value;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link String}
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<String> asString() {
		return new StringConverterDefaultOrThrowStep<>(_theString != null ? _theString.toString() : null,
													   null);		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link String} applying the filter
	 * @param filter
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<String> asString(final StringConverterFilter filter) {
		return new StringConverterDefaultOrThrowStep<>(_theString != null ? filter.filter(_theString.toString()) : null,
													   null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STRINGBASE64
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link String}
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<StringBase64> asStringBase64() {
		return new StringConverterDefaultOrThrowStep<>(_theString != null ? StringBase64.fromBase64EncodedString(_theString.toString()) : null,
													   null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Url
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link Url}
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<Url> asUrl() {
		return new StringConverterDefaultOrThrowStep<>(_theString != null ? Url.from(_theString.toString()) : null,
													   null);		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link UrlPath}
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<UrlPath> asUrlPath() {
		return new StringConverterDefaultOrThrowStep<>(_theString != null ? UrlPath.from(_theString.toString()) : null,
													   null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BOOLEAN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a boolean
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<Boolean> asBoolean() {
		return new StringConverterDefaultOrThrowStep<>(StringConverter.asBoolean(_theString,null),
													   false);	// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BYTE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a byte
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<Byte> asByte() {
		return new StringConverterDefaultOrThrowStep<>(StringConverter.asByte(_theString,null),
												 	   Byte.valueOf((byte)'\u0000'));	// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHAR
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a char
     * @return
     */
    public StringConverterDefaultOrThrowStep<Character> asChar() {
		return new StringConverterDefaultOrThrowStep<>(StringConverter.asChar(_theString,null),
												 	   Character.valueOf('\u0000'));	// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  DOUBLE
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a double
     * @return
     */
    public StringConverterDefaultOrThrowStep<Double> asDouble() {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asDouble(_theString,null),
												 	   0D);		// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  FLOAT
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a float
     * @return
     */
    public StringConverterDefaultOrThrowStep<Float> asFloat() {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asFloat(_theString,null),
												 	   0F);		// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  INTEGER
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as an integer
     * @return
     */
    public StringConverterDefaultOrThrowStep<Integer> asInteger() {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asInteger(_theString,null),
												 	   0);		// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  LONG
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a long
     * @return
     */
    public StringConverterDefaultOrThrowStep<Long> asLong() {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asLong(_theString,null),
												 	   0L);			// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  SHORT
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a short
     * @return
     */
    public StringConverterDefaultOrThrowStep<Short> asShort() {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asShort(_theString,null),
												 	   (short)0);		// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  LEGACY DATE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link Date} using the given format
	 * as a pattern to get the date
	 * @param format
	 * @return
	 */
    @Deprecated
	public StringConverterDefaultOrThrowStep<Date> asDate(final String format) {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asDate(_theString,format,
    																		  null),
    												   new Date());		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link Date} using the given format
	 * as a pattern to get the date
	 * @param format
	 * @return
	 */
    @Deprecated
	public StringConverterDefaultOrThrowStep<LocalDate> asLocalDate(final String format) {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asLocalDate(_theString,format,
    																			   null),
    												   LocalDate.now());		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DATE / TIME
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets an Instant (Instant is ALWAYS at UTC!!!) from an String
	 * First it detects if the String is in the ISO8601 string representation at UTC
	 * or in the EPOCH Millis representation at UTC
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<Instant> asInstantFromISO8601OrEPOCHMillis() {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asInstantFromISO8601OrEPOCHMillis(_theString,
    																			 						null),	// default value
    												   Instant.now());		// default value
	}
	/**
	 * Gets an Instant (Instant is ALWAYS at UTC!!!) from it's ISO8601 string representation at UTC
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<Instant> asInstantFromISO8061() {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asInstantFromISO8601(_theString,
    																			 			null),	// default value
    												   Instant.now());		// default value
	}
	/**
	 * Gets an Instant (Instant is ALWAYS at UTC!!!) from EPOCH millis
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<Instant> asInstantFromEPOCHMillis() {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asInstantFromEPOCHMillis(Strings.isNOTNullOrEmpty(_theString) ? Long.parseLong(_theString.toString()) : null,
    																			 				null),	// default value
    												   Instant.now());		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link Instant} using the given format
	 * as a pattern to get the date
	 * @param pattern
	 * @param locale only meaningful if the pattern contains a localizeable text
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<Instant> asInstant(final String pattern,final Locale locale) {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asInstant(_theString,pattern,
    																			 locale,
    																			 null),	// default value
    												   Instant.now());		// default value
	}
	/**
	 * Gets a ZonedDateTime from it's ISO8601 string representation (2011-12-03T10:15:30+01:00[Europe/Paris])
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<ZonedDateTime> asZonedDateTimeFromISO8061() {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asZonedDateTimeFromISO8601(_theString,
    																			 				 null),	// default value
    												   ZonedDateTime.now());		// default value
	}
	/**
	 * Gets a OffsetDateTime from it's ISO8601 string representation (2011-12-03T10:15:30+01:00[Europe/Paris])
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<OffsetDateTime> asOffsetDateTimeFromISO8061() {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asOffsetDateTimeFromISO8601(_theString,
    																			 				   null),	// default value
    												   OffsetDateTime.now());		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link LocalDate} from ISO format
	 * as a pattern to get the date
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<LocalDate> asLocalDateFromISO8601() {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asLocalDateFromISO8601(_theString,
    																			   			  null),
    												   LocalDate.now());		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link LocalDate} using the given format
	 * as a pattern to get the date
	 * @param pattern
	 * @param locale
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<LocalDate> asLocalDate(final String pattern,
																	final Locale locale) {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asLocalDate(_theString,pattern,
    																			   locale,
    																			   null),
    												   LocalDate.now());		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link LocalDate} using the given style
	 * as a pattern to get the date
	 * @param style
	 * @param locale
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<LocalDate> asLocalDate(final FormatStyle style,
																	final Locale locale) {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asLocalDate(_theString,style,
    																			   locale,
    																			   null),
    												   LocalDate.now());		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link LocalDateTime} from ISO format
	 * as a pattern to get the date
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<LocalDateTime> asLocalDateTimeFromISO8601() {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asLocalDateTimeFromISO8601(_theString,
    																			   			  null),
    												   LocalDateTime.now());		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link LocalDateTime} using the given format
	 * as a pattern to get the date
	 * @param pattern
	 * @param locale
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<LocalDateTime> asLocalDateTime(final String pattern,
																			final Locale locale) {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asLocalDateTime(_theString,pattern,
    																			   	   locale,
    																			   	   null),
    												   LocalDateTime.now());		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link LocalDateTime} using the given style
	 * as a pattern to get the date
	 * @param style
	 * @param locale
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<LocalDateTime> asLocalDateTime(final FormatStyle style,
																			final Locale locale) {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asLocalDateTime(_theString,style,
    																			   	   locale,
    																			   	   null),
    												   	LocalDateTime.now());		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link LocalTime} from ISO format
	 * as a pattern to get the date
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<LocalTime> asLocalTimeFromISO8601() {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asLocalTimeFromISO8601(_theString,
    																			   			  null),
    												   LocalTime.now());		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link LocalTime} using the given format
	 * as a pattern to get the date
	 * @param pattern
	 * @param locale
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<LocalTime> asLocalTime(final String pattern,
																	final Locale locale) {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asLocalTime(_theString,pattern,
    																			   locale,
    																			   null),
    												   LocalTime.now());		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link LocalTime} using the given style
	 * as a pattern to get the date
	 * @param style
	 * @param locale
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<LocalTime> asLocalTime(final FormatStyle style,
																	final Locale locale) {
    	return new StringConverterDefaultOrThrowStep<>(StringConverter.asLocalTime(_theString,style,
    																			   locale,
    																			   null),
    												   	LocalTime.now());		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PATH
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link Path}
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<Path> asPath() {
		return new StringConverterDefaultOrThrowStep<>(StringConverter.asPath(_theString,null),
    												   null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Language
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link Language}
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<Language> asLanguageFromCountryCode() {
		return new StringConverterDefaultOrThrowStep<>(StringConverter.asLanguageFromCountryCode(_theString,null),
    												   Language.ENGLISH);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ENUM
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as an {@link Enum} of the given type
	 * @param enumType
	 * @return
	 */
	public <E extends Enum<E>> StringConverterDefaultOrThrowStep<E> asEnumElement(final Class<E> enumType) {
		return new StringConverterDefaultOrThrowStep<>(StringConverter.asEnumElement(_theString,enumType,
																				  	 null),
    												   null);		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as an {@link Enum} of the given type
	 * @param enumType
	 * @return
	 */
	public <E extends Enum<E> & EnumWithCode<String,E>> StringConverterDefaultOrThrowStep<E> asEnumElementFromStringCode(final Class<E> enumType) {
		return new StringConverterDefaultOrThrowStep<>(StringConverter.asEnumElementFromStringCode(_theString,enumType,
																					  			   null),
    												   null);		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as an {@link Enum} of the given type
	 * @param enumType
	 * @return
	 */
	public <E extends Enum<E> & EnumWithCode<Integer,E>> StringConverterDefaultOrThrowStep<E> asEnumElementFromIntCode(final Class<E> enumType) {
		return new StringConverterDefaultOrThrowStep<>(StringConverter.asEnumElementFromIntCode(_theString,enumType,
																					  			null),
    												   null);		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as an {@link Enum} of the given type
	 * @param enumType
	 * @return
	 */
	public <E extends Enum<E> & EnumWithCode<Long,E>> StringConverterDefaultOrThrowStep<E> asEnumElementFromLongCode(final Class<E> enumType) {
		return new StringConverterDefaultOrThrowStep<>(StringConverter.asEnumElementFromLongCode(_theString,enumType,
																					  			 null),
    												   null);		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as an {@link Enum} of the given type
	 * @param enumType
	 * @return
	 */
	public <E> StringConverterDefaultOrThrowStep<E> asEnumElementFromString(final Function<CharSequence,E> transformer,
																			final Class<E> enumType) {
		return new StringConverterDefaultOrThrowStep<>(StringConverter.asEnumElementFromString(_theString,transformer,
																							   enumType,null),
    												   null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link StringBuilder}
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<StringBuilder> asStringBuilder() {
		return new StringConverterDefaultOrThrowStep<>(StringConverter.asStringBuilder(_theString,null),
    												  new StringBuilder());		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING BUFFER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link StringBuffer}
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<StringBuffer> asStringBuffer() {
		return new StringConverterDefaultOrThrowStep<>(StringConverter.asStringBuffer(_theString,null),
    												   new StringBuffer());		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHAR ARRAY
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a char array
	 * @return
	 */
	public char[] asCharArray() {
		return StringConverter.asCharArray(_theString,null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  InputStream
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns an {@link InputStream} to the buffer, that's the {@link String} as an {@link InputStream}
	 * (the stream's byte charset is the System's default charset)
	 */
	public InputStream asInputStream() {
		return StringConverter.asInputStream(_theString,Charset.defaultCharset());
	}
	/**
	 * Returns an {@link InputStream} to the buffer, that's the {@link String} as an {@link InputStream}
	 * the stream's byte charset is provided
	 * @param charset
	 */
	public InputStream asInputStream(final Charset charset) {
		return StringConverter.asInputStream(_theString,charset);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  READER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link Reader} to the buffer, that's the {@link String} as a {@link Reader}
	 */
	public Reader asReader() {
		return StringConverter.asReader(_theString);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TYPE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as an instance of the given type
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * <pre class="brush:java">
	 * 		MyType myType = wrapper.asType()
	 * 							   .using(new Function<CharSequence,MyType>() {
	 * 											public MyType apply(final CharSequence str) {
	 * 												return ReflectionUtils.<MyType>createInstanceFromString(MyType.class,
	 *														 												value.toString());
	 *											}
	 *									   })
	 *							   .orDefault(new MyType("default"));
	 * </pre>
	 * @param fromStringTransform a function that transforms the string to an instance of the given type
	 * @param defValue
	 * @return
	 */
	public <T> StringConverterWrapperAsTypeFromStringTransformStep<T> asType(final Class<T> type) {
		return new StringConverterWrapperAsTypeFromStringTransformStep<>(type);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class StringConverterWrapperAsTypeFromStringTransformStep<T> {
		private final Class<T> _type;

		public StringConverterWrapperAsTypeDefaultOrThrowStep<T> using(final Function<CharSequence,T> fromStringTransform) {
			return new StringConverterWrapperAsTypeDefaultOrThrowStep<>(_type,fromStringTransform);
		}
		public StringConverterWrapperAsTypeDefaultOrThrowStep<T> usingFactory(final FactoryFrom<CharSequence,T> fromStringTransform) {
			return new StringConverterWrapperAsTypeDefaultOrThrowStep<>(_type,str -> fromStringTransform.from(str));	// create a Function from the FactoryFrom
		}
	}
	@SuppressWarnings("unused")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class StringConverterWrapperAsTypeDefaultOrThrowStep<T> {
		private final Class<T> _type;
		private final Function<CharSequence,T> _fromStringTransform;

		public T orDefault(final T def) {
			return _asType(_theString,_fromStringTransform,
					  	   def);
		}
		public T orNull() {
			return _asType(_theString,_fromStringTransform,
						   null);		// default value
		}
		public T orThrow() {
			return this.orThrow(null);
		}
		public T orThrow(final String msg,final Object... params) {
			if (_theString == null) {
				throw new IllegalArgumentException(Strings.isNOTNullOrEmpty(msg) ? Strings.customized(msg,params)
																										 : "Null or empty string");
			}
			return this.orDefault(null);
		}
	}
	private static <T> T _asType(final CharSequence value,
							   	 final Function<CharSequence,T> fromStringTransform,
							   	 final T defValue) {
		if (value == null) return defValue;
		T outValue = fromStringTransform.apply(value);
		return outValue;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COLLECTION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a {@link Collection} of instances of the given type
	 * (the string is tokenized by " ", "," or ";"
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * <pre class="brush:java">
	 * 		MyType myType = wrapper.asCollection("...string representation...",
	 * 									   		 new Function<CharSequence,MyType>() {
	 * 													public MyType apply(final CharSequence str) {
	 * 														return ReflectionUtils.<MyType>createInstanceFromString(MyType.class,
	 *														 														value.toString());
	 *													}
	 *											  },
	 *											  Lists.newArrayList(new MyType("default")));
	 * </pre>
	 * @param value
	 * @param fromStringTransform a function that transforms the string to an instance of the given type
	 * @param defValue
	 * @return
	 */
	public <T> StringConverterWrapperAsCollectionStringTokenizerStep<T> asCollection(final Class<T> type) {
		return new StringConverterWrapperAsCollectionStringTokenizerStep<>(type);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class StringConverterWrapperAsCollectionStringTokenizerStep<T> {
		private final Class<T> _type;

		public StringConverterWrapperAsCollectionTypeFromStringTransformStep<T> useTokenizer(final Function<CharSequence,Collection<String>> tokenizer) {
			return new StringConverterWrapperAsCollectionTypeFromStringTransformStep<>(_type,
																					   tokenizer.apply(_theString));
		}
		public StringConverterWrapperAsCollectionTypeFromStringTransformStep<T> useDefaultTokenizer() {
			return this.useTokenizer(str -> Strings.isNOTNullOrEmpty(str)
									   			? StringSplitter.using(Splitter.on(';'))
									    						.at(str.toString()
									    							   .trim()
									    							   .replaceAll("\\s",";")
									    							   .replaceAll(",",";"))
									    						.toList()
									    						.stream()
									    						.filter(tokenStr -> Strings.isNOTNullOrEmpty(tokenStr))
									    						.collect(Collectors.toList())
									    		: Collections.emptyList());
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class StringConverterWrapperAsCollectionTypeFromStringTransformStep<T> {
		private final Class<T> _type;
		private final Collection<String> _tokens;

		public StringConverterWrapperAsCollectionDefaultOrThrowStep<T> transformTokenUsing(final Function<CharSequence,T> fromStringTransform) {
			return new StringConverterWrapperAsCollectionDefaultOrThrowStep<>(_type,_tokens,fromStringTransform);
		}
		public StringConverterWrapperAsCollectionDefaultOrThrowStep<T> transformTokenUsingFactory(final FactoryFrom<CharSequence,T> fromStringTransform) {
			return new StringConverterWrapperAsCollectionDefaultOrThrowStep<>(_type,_tokens,str -> fromStringTransform.from(str));	// create a Function from the FactoryFrom
		}
	}
	@SuppressWarnings("unused")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class StringConverterWrapperAsCollectionDefaultOrThrowStep<T> {
		private final Class<T> _type;
		private final Collection<String> _tokens;
		private final Function<CharSequence,T> _fromStringTransform;

		public Collection<T> orDefault(final Collection<T> def) {
			return _asCollection(def);
		}
		public Collection<T> orNull() {
			return _asCollection(null);		// default value
		}
		public Collection<T> orThrow() {
			return this.orThrow(null);
		}
		public Collection<T> orThrow(final String msg,final Object... params) {
			if (_theString == null) throw new IllegalArgumentException(Strings.isNOTNullOrEmpty(msg) ? Strings.customized(msg,params)
																									 : "Null or empty string");
			return this.orDefault(null);
		}
		private Collection<T> _asCollection(final Collection<T> defValue) {
			return CollectionUtils.hasData(_tokens) ? _tokens.stream()
		    	  											 .map(str -> _fromStringTransform.apply(str))
		    	  											 .collect(Collectors.toList())
				    	  						    : defValue;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OID
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as an instance of the given oid type
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * <pre class="brush:java">
	 * 		MyOID myType = wrapper.asType()
	 * 							   .using(new Function<CharSequence,MyOID>() {
	 * 											public MyOID apply(final CharSequence str) {
	 * 												return MyOID.forId(str);
	 *											}
	 *									   })
	 *							   .orDefault(MyOID.forId("default"));
	 * </pre>
	 * @param oidType
	 * @return
	 */
	public <O extends OID> StringConverterWrapperAsTypeFromStringTransformStep<O> asOid(final Class<O> oidType) {
		return new StringConverterWrapperAsTypeFromStringTransformStep<>(oidType);
	}
}
