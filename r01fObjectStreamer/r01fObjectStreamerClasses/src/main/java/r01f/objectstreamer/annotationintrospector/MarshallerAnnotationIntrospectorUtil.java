package r01f.objectstreamer.annotationintrospector;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallDateFormat;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.JavaPackage;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.datetime.DateTimeFormatUtils;
import r01f.util.types.datetime.DateTimeUtils;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.introspect.AnnotatedMethod;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
abstract class MarshallerAnnotationIntrospectorUtil {
/////////////////////////////////////////////////////////////////////////////////////////
//	NAMIMG
/////////////////////////////////////////////////////////////////////////////////////////
	enum MarshallerNameFor {
		PROPERTY_SERIALIZATION,
		PROPERTY_DESERIALIZATION,
		TYPE_ROOT;
	}
	public static PropertyName findName(final MapperConfig<?> config,final Annotated ann,
								   		final MarshallerNameFor whatFor,
								   		final JacksonAnnotationIntrospector delegatedJacksonAnnotationIntrospector) {
		PropertyName outName = null;

		// [0] - Do NOT mind annotated getters
		if (ann instanceof AnnotatedMethod) return null;

		// [1] - @MarshallFrom annotated constructor parameter
		if (ann instanceof AnnotatedParameter
		 && ann.hasAnnotation(MarshallFrom.class)) {
			MarshallFrom m = ann.getAnnotation(MarshallFrom.class);
			if (!MarshallFrom.MARKER_FOR_DEFAULT.equals(m.value())) {
				outName = PropertyName.construct(m.value());
			} else {
				outName = PropertyName.construct(ann.getName());
			}
		}
		// [2] - @MarshallType annotated type
		else if (ann instanceof AnnotatedClass
			  && ann.hasAnnotation(MarshallType.class)) {
			MarshallType m = ann.getAnnotation(MarshallType.class);
			if (!MarshallType.MARKER_FOR_DEFAULT.equals(m.as())) {
				outName = PropertyName.construct(m.as());
			} else {
				outName = PropertyName.construct(_normalizeField(ann.getName()));
			}
		}
		// [3] - @MarshallField annotated field
		else if (ann instanceof AnnotatedField
			  && ann.hasAnnotation(MarshallField.class)) {
			MarshallField m = ann.getAnnotation(MarshallField.class);
			if (!MarshallField.MARKER_FOR_DEFAULT.equals(m.as())) {
				outName = PropertyName.construct(m.as());
			} else {
				outName = PropertyName.construct(_normalizeField(ann.getName()));
			}
		}

		// [5] - if name could not be found delegate to native jackson annotations
		if (outName == null
		 || outName == PropertyName.USE_DEFAULT) {
			outName = switch (whatFor) {
						case PROPERTY_SERIALIZATION -> delegatedJacksonAnnotationIntrospector.findNameForSerialization(config,ann);
						case PROPERTY_DESERIALIZATION -> delegatedJacksonAnnotationIntrospector.findNameForDeserialization(config,ann);
						case TYPE_ROOT -> delegatedJacksonAnnotationIntrospector.findRootName(config,(AnnotatedClass)ann);
						default -> throw new IllegalStateException(delegatedJacksonAnnotationIntrospector + " does NOT support " + whatFor);
					 };
		}
		// return
		return outName;
	}
	private static String _normalizeField(final String name) {
		if (name.startsWith("_")) return name.substring(1);
		return name;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static JsonFormat.Value dateFormatOf(final AnnotatedField annF,
												final MarshallDateFormat mdf) {
		DateFormat dateFormat = mdf.use();
		Class<? extends Temporal> dateTimeType = _dateTimeType(annF);	// can be null if the field is a collection/array

		// [1] - Get the annotation values
		// 1.1 - pattern
		String pattern = mdf.format();

		// 1.2 - timezone
		String zoneId = mdf.timezone();
		if (Strings.isNOTNullOrEmpty(zoneId)
		 &&	!DateTimeUtils.isSupportedZoneId(zoneId)) throw new AnnotationFormatError(String.format("Field %s of %s is a date/time field with a ZoneId=%s BUT this %s is NOT valid!",
																								    annF.getName(),annF.getDeclaringClass(),zoneId,ZoneId.class.getName()));
		ZoneId zone = Strings.isNOTNullOrEmpty(mdf.timezone()) ? ZoneId.of(mdf.timezone())
															   : null;
		// 1.3 - locale
		String localeStr = mdf.locale();
		Locale locale = null;
		if (Strings.isNOTNullOrEmpty(localeStr)) {
			locale = Locale.forLanguageTag(localeStr.replace('_', '-'));	// normalize: always use '-' in language tags
																			// if Locale is NOT supported Locale.forLanguageTag returns a default locale: Locale.ROOT
			if (locale == Locale.ROOT
			 || !Arrays.asList(Locale.getAvailableLocales()).contains(locale)) throw new AnnotationFormatError(String.format("Field %s of %s is a date/time field with a Locale=%s BUT this %s is NOT valid!",
															  																 annF.getName(),annF.getDeclaringClass(),localeStr,Locale.class.getName()));
		}

		// [2] - build format
		JsonFormat.Value outFormat = null;

		switch(dateFormat) {
		case CUSTOM:
			if (MarshallField.MARKER_FOR_DEFAULT.equals(pattern)) throw new AnnotationFormatError(String.format("Field %s of %s is a date/time field with CUSTOM format: the pattern is mandatory",
																												annF.getName(),annF.getDeclaringClass().getName()));
			if (zone != null) {
				outFormat = JsonFormat.Value.forShape(Shape.STRING)
											.withTimeZone(TimeZone.getTimeZone(zoneId))
											.withPattern(pattern);
			} else {
				outFormat = JsonFormat.Value.forShape(Shape.STRING)
											.withPattern(pattern);
			}
			break;
		case EPOCH:	// BEWARE!! EPOCH is always UTC!!!
			if (dateTimeType == Instant.class) {
				outFormat = JsonFormat.Value.forShape(Shape.STRING)
											.withPattern("epochMillis");
			} else {
				throw new IllegalArgumentException(String.format("Field %s of %s is a date/time field with EPOCH format: only %s type is supported for EPOCH format",
																  annF.getName(),annF.getDeclaringClass().getName(),
																  Instant.class.getName()));
			}
			break;
		case ISO8601:
		default:
			String isoPattern = null;
			if (dateTimeType == Instant.class) {
				isoPattern = DateTimeFormatUtils.FORMAT_PATTERN_FOR_ISO_INSTANT;
				zone = ZoneId.of("UTC");	// ISO INSTANT is always in UTC
			} else if (dateTimeType == LocalDate.class) {
				isoPattern = DateTimeFormatUtils.FORMAT_PATTERN_FOR_ISO_LOCAL_DATE;
			} else if (dateTimeType == LocalDateTime.class) {
				isoPattern = DateTimeFormatUtils.FORMAT_PATTERN_FOR_ISO_LOCAL_DATE_TIME;
			} else if (dateTimeType == LocalTime.class) {
				isoPattern = DateTimeFormatUtils.FORMAT_PATTERN_FOR_ISO_LOCAL_TIME;
			} else if (dateTimeType == OffsetDateTime.class) {
				isoPattern = DateTimeFormatUtils.FORMAT_PATTERN_FOR_ISO_OFFSET_DATE_TIME;
			} else if (dateTimeType == ZonedDateTime.class) {
				isoPattern = DateTimeFormatUtils.FORMAT_PATTERN_FOR_ISO_ZONED_DATE_TIME;
			} else {
				isoPattern = DateTimeFormatUtils.FORMAT_PATTERN_FOR_ISO_INSTANT;	// default
			}

			outFormat = JsonFormat.Value.forShape(Shape.STRING)
								  .withPattern(isoPattern);
			if (zone != null) outFormat = outFormat.withTimeZone(TimeZone.getTimeZone(zoneId));
			if (locale != null) outFormat = outFormat.withLocale(locale);
		}
		return outFormat;
	}
	public static final boolean isDateType(final Class<?> type) {
		return type == Date.class || type == java.sql.Date.class
			|| type == java.time.Instant.class
			|| type == java.time.LocalDate.class
			|| type == java.time.LocalDateTime.class
			|| type == java.time.LocalTime.class
			|| type == java.time.OffsetDateTime.class
			|| type == java.time.ZonedDateTime.class;
	}
	private static final Class<? extends Temporal> _dateTimeType(final AnnotatedField annF) {
		if (MarshallerAnnotationIntrospectorUtil.isCollectionOrArrayType(annF.getRawType())) return null;

		Class<? extends Temporal> outType = null;
		if (annF.getRawType() == Instant.class
		 || annF.getRawType() == Date.class || annF.getRawType() == java.sql.Date.class) {
			outType = Instant.class;
		} else if (annF.getRawType() == LocalDate.class) {
			outType = LocalDate.class;
		} else if (annF.getRawType() == LocalDateTime.class) {
			outType = LocalDateTime.class;
		} else if (annF.getRawType() == LocalTime.class) {
			outType = LocalTime.class;
		} else if (annF.getRawType() == OffsetDateTime.class) {
			outType = OffsetDateTime.class;
		} else if (annF.getRawType() == ZonedDateTime.class) {
			outType = ZonedDateTime.class;
		}
		return outType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean isInstanciable(final Class<?> type) {
		return !MarshallerAnnotationIntrospectorUtil.isNotInstanciable(type);
	}
	public static boolean isNotInstanciable(final Class<?> type) {
		return type.isAnnotation()
			|| Modifier.isAbstract(type.getModifiers())		// is abstract
			|| Modifier.isInterface(type.getModifiers());	// is interface
	}
	public static boolean isCollectionOrArrayType(final Class<?> type) {
		return Map.class.isAssignableFrom(type)
			|| Collection.class.isAssignableFrom(type)
			|| type.isArray();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean isInstanciable(final JavaType type) {
		return !MarshallerAnnotationIntrospectorUtil.isNotInstanciable(type);
	}
	public static boolean isNotInstanciable(final JavaType javaType) {
		return javaType.isAbstract()
			|| javaType.isInterface();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if the given type is within the accepted packages
	 * @param type
	 * @param acceptedPackages
	 * @return
	 */
	public static boolean isWithinAcceptedPackages(final JavaPackage pckg,
												   final Collection<JavaPackage> acceptedPackages) {
		if (pckg == null || CollectionUtils.isNullOrEmpty(acceptedPackages)) return true;

		return acceptedPackages.stream()
							   .anyMatch(acceptedPkg -> pckg.startsWith(acceptedPkg));
	}
	/**
	 * Checks if the given type is NOT within the accepted packages
	 * @param pckg
	 * @param acceptedPackages
	 * @return
	 */
	public static boolean isNOTWithinAcceptedPackages(final JavaPackage pckg,
											   		  final Collection<JavaPackage> acceptedPackages) {
		boolean accepted = MarshallerAnnotationIntrospectorUtil.isWithinAcceptedPackages(pckg,
																			  			 acceptedPackages);
		return !accepted;
	}
}
