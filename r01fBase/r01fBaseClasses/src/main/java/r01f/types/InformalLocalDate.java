package r01f.types;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * A simple type used to hold info about a {@link LocalDate} when sometimes the day, month or even the year
 * are NOT relevant
 * Sample usages:
 * <pre class='brush:java'>
 * 		InformalDate date1 = new InformalDate("2015");
 * 		InformalDate date2 = new InformalDate("2015","March");
 * 		InformalDate date1 = new InformalDate("2015","March","25 monday");
 * </pre>
 */
@MarshallType(as="date")
@Accessors(prefix="_")
public class InformalLocalDate
  implements Serializable {
	private static final long serialVersionUID = 1110792232316017013L;
/////////////////////////////////////////////////////////////////////////////////////////
//  DATE
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="year",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _year;

	@MarshallField(as="month",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _month;

	@MarshallField(as="day",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _day;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public InformalLocalDate() {
		// default no args constructor
	}
	public InformalLocalDate(final String year,final String month,final String day) {
		_year = year;
		_month = month;
		_day = day;
	}
	public InformalLocalDate(final String year,final String month) {
		_year = year;
		_month = month;
	}
	public InformalLocalDate(final String year) {
		_year = year;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static InformalLocalDate create() {
		return new InformalLocalDate();
	}
	public static InformalLocalDate createFor(final String year,final String month,final String day) {
		return new InformalLocalDate(year,month,day);
	}
	public static InformalLocalDate createFor(final String year,final String month) {
		return new InformalLocalDate(year,month);
	}
	public static InformalLocalDate createFor(final String year) {
		return new InformalLocalDate(year);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public InformalLocalDate year(final String year) {
		_year = year;
		return this;
	}
	public InformalLocalDate month(final String month) {
		_month = month;
		return this;
	}
	public InformalLocalDate day(final String day) {
		_day = day;
		return this;
	}
}
