package r01f.exceptions;

import r01f.debug.Debuggable;

/**
 * Interface for ids representing an {@link EnrichedThrowable} types
 * Usage:
 * <pre class='brush:java'>
 *      @Accessors(prefix="_")
 *      public final class MyErrorType 
 *                 extends EnrichedThrowableTypeBase {	
 *      	private MyErrorType(final String name,
 *      						final int group,final int code,								
 *      						final ExceptionSeverity severity) {
 *      		super(name,
 *      			  group,code,
 *      			  severity);
 *      	}
 *      	private static EnrichedThrowableTypeBuilder<MyErrorType>.EnrichedThrowableTypeBuilderCodesStep<MyErrorType> withName(final String name) {		
 *      		return new EnrichedThrowableTypeBuilder<MyErrorType>() {
 *      						@Override
 *      						protected MyErrorType _build(final String name, 
 *      													 final int group,final int code,
 *      													 final ExceptionSeverity severity) {
 *      							return new MyErrorType(name,
 *      												   group,code,
 *      												   severity);
 *      						}
 *      			   }.withName(name);
 *      	}
 *      	public static final MyErrorType MY_ERROR_1 = MyErrorType.withName("MY_ERROR_1")
 *      															.coded(2,1)
 *      															.severity(ExceptionSeverity.FATAL)
 *      															.build();
 *      	public static final MyErrorType MY_ERROR_2 = MyErrorType.withName("COMPONENTDEF_XML_MALFORMED")
 *      															.coded(2,2)
 *      															.severity(ExceptionSeverity.FATAL)
 *      															.build();
 * </pre>
 * Just use it like an enum: <code>MyErrorType.MY_ERROR_1</code>
 * @param <E>
 */
public interface EnrichedThrowableType 
		 extends Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the group (usually is the same for all enum elements)
	 */
	public int getGroup();
	/**
	 * @return the code
	 */
	public int getCode();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the name
	 * (it uses java's type erasure to emulate the name() method of an enum)
	 * @return
	 */
	public String getName();
	/**
	 * Checks if the element is within a provided list of elements
	 * @param els
	 * @return
	 */
	public boolean isIn(EnrichedThrowableType... els);	
	/**
	 * Checks if this element is the same as the provided one
	 * @param el
	 * @return
	 */
	public boolean is(EnrichedThrowableType el);
	/**
	 * Returns true if the enum element has the same group and code as the provided ones 
	 * @param group
	 * @param code
	 * @return
	 */
	public boolean is(final int group,final int code);
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return the exception severity from the exception type
	 */
	public ExceptionSeverity getSeverity();
	/**
	 * Checks if this type has the same severity as the given one
	 * @param other
	 * @return
	 */
	public boolean hasSameSeverityAs(final EnrichedThrowableType other);
	/**
	 * Checks if this type is more serious than the given one
	 * @param other
	 * @return
	 */
	public boolean isMoreSeriousThan(final EnrichedThrowableType other);
}
