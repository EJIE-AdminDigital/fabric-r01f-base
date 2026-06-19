package r01f.types.datetime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Objects;
import r01f.util.types.Strings;

@MarshallType(as="timeHourMinutes")
@Accessors(prefix="_")
public class Time
  implements Comparable<Time>,
  			 CanBeRepresentedAsString {

	private static final long serialVersionUID = -7084816234257337127L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final transient Pattern TIME_PATTERN = Pattern.compile("([0-9]|0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])(?::([0-5][0-9]))?" +	// HH:MM:ss
																		  "(\\s)?(?i)(am|pm)?");											// am|pm

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="hour",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final int _hourOfDay;

	@MarshallField(as="minuteOfHour",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final int _minuteOfHour;

	@MarshallField(as="secondOfMinute",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final int _secondOfMinute;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Time(@MarshallFrom("hour") final int hourOfDay,@MarshallFrom("minuteOfHour") final int minuteOfHour,@MarshallFrom("secondOfMinute") final int secondOfMinute) {
		if (hourOfDay < 0) throw new IllegalArgumentException("Not a valid hour");
		if (minuteOfHour < 0) throw new IllegalArgumentException("Not a valid minute of hour value");
		if (secondOfMinute < 0) throw new IllegalArgumentException("Not a valid second of minute value");
		_hourOfDay = hourOfDay;
		_minuteOfHour = minuteOfHour;
		_secondOfMinute = secondOfMinute;
	}
	public Time(final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour,final SecondOfMinute secondOfMinute) {
		this(hourOfDay.asInteger(),minuteOfHour.asInteger(),secondOfMinute.asInteger());
	}
	public Time(final HourOfDay hour,final MinuteOfHour minuteOfHour) {
		this(hour.asInteger(),minuteOfHour.asInteger(),0);
	}
	public Time(final String timeStr) {
		Matcher m = TIME_PATTERN.matcher(timeStr);
		if (!m.find()) throw new IllegalStateException(timeStr + " is not a valid time: MUST match " + TIME_PATTERN);
		int hour = Integer.parseInt(m.group(1));
		int minutes = Integer.parseInt(m.group(2));
		int seconds = Strings.isNOTNullOrEmpty(m.group(3)) ? Integer.parseInt(m.group(3)) : 0;
		String ampm = m.group(4);

		if (Strings.isNOTNullOrEmpty(ampm)
		 && ampm.equalsIgnoreCase("PM")
		 && hour < 12) {
			hour = 12 + hour;
		}
		_hourOfDay = hour;
		_minuteOfHour = minutes;
		_secondOfMinute = seconds;
	}
	////////// Builders
	public static Time of(final String time) {
		return new Time(time);
	}
	@Deprecated
	public static Time of(final Date date) {
		return Time.of(date,ZoneId.systemDefault());
	}
	public static Time of(final Date date,final ZoneId zone) {
		return Time.of(date.toInstant(),zone);
	}
	public static Time of(final Instant instant,final ZoneId zone) {
		LocalTime time = instant.atZone(zone)
								.toLocalTime();
		return Time.of(time);
	}
	public static Time of(final LocalTime time) {
		// Get individual components
		int hour = time.getHour();
		int minute = time.getMinute();
		int second = time.getSecond();

		return new Time(hour,minute,second);
	}
	public static Time of(final LocalDateTime time) {
		return new Time(time.getHour(),time.getMinute(),time.getSecond());
	}
	public static Time of(final OffsetDateTime time) {
		return new Time(time.getHour(),time.getMinute(),time.getSecond());
	}
	public static Time of(final ZonedDateTime time) {
		return new Time(time.getHour(),time.getMinute(),time.getSecond());
	}
	public static Time of(final int hour,final int minutes,final int seconds) {
		return new Time(hour,minutes,seconds);
	}
	public static Time valueOf(final String time) {
		return new Time(time);
	}
	public static Time fromString(final String time) {
		return new Time(time);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONVERSION
/////////////////////////////////////////////////////////////////////////////////////////
	public LocalTime asLocalTime() {
		return LocalTime.of(_hourOfDay,_minuteOfHour,_secondOfMinute,0);	// 0 nanoseconds
	}
	@Override
	public String toString() {
		return Strings.customized("{}:{}:{}",
								  String.format("%02d",_hourOfDay),
								  String.format("%02d",_minuteOfHour),
								  String.format("%02d",_secondOfMinute));
	}
	@Override
	public String asString() {
		return this.toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isBefore(final Time other) {
		return this.asLocalTime().isBefore(other.asLocalTime());
	}
	public boolean isAfter(final Time other) {
		return this.asLocalTime().isAfter(other.asLocalTime());
	}
	public boolean isBeforeOrEqual(final Time other) {
		LocalTime thisLT = this.asLocalTime();
		LocalTime otherLT = other.asLocalTime();
		return thisLT.isBefore(otherLT)
			|| thisLT.equals(otherLT);
	}
	public boolean isAfterOrEqual(final Time other) {
		LocalTime thisLT = this.asLocalTime();
		LocalTime otherLT = other.asLocalTime();
		return thisLT.isBefore(otherLT)
			|| thisLT.equals(otherLT);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		return obj instanceof Time other ? this.getHourOfDay() == other.getHourOfDay()
										&& this.getMinuteOfHour() == other.getMinuteOfHour()
										&& this.getSecondOfMinute() == other.getSecondOfMinute()
										 : false;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_hourOfDay,_minuteOfHour,_secondOfMinute);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COMPARABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int compareTo(final Time other) {
		LocalTime thisLT = this.asLocalTime();
		LocalTime otherLT = other.asLocalTime();
		return thisLT.compareTo(otherLT);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	UTIL
/////////////////////////////////////////////////////////////////////////////////////////
	public static long hoursBetween(final Time t1,final Time t2) {
		Duration duration = Duration.between(t1.asLocalTime(),t2.asLocalTime());
		return duration.toHours();
	}
	public static long minutesBetween(final Time t1,final Time t2) {
		Duration duration = Duration.between(t1.asLocalTime(),t2.asLocalTime());
		return duration.toMinutes();
	}
	public static long secondsBetween(final Time t1,final Time t2) {
		Duration duration = Duration.between(t1.asLocalTime(),t2.asLocalTime());
		return duration.getSeconds();
	}
}
