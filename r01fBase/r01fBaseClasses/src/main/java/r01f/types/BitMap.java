package r01f.types;


import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

/**
 * Encapsulates a bitmap into a java long (longs in java are 32 bits). 
 * For example if we want to encode some security rights:
 * <pre>
 *	Total control ---------------------------------------------+
 *	Security admin ------------------------------------------+ |
 *	Delete ------------------------------------------------+ | |
 *	Read ------------------------------------------------+ | | | 
 *	Write ---------------------------------------------+ | | | |
 *	Create ------------------------------------------+ | | | | |
 *													 | | | | | |
 *							   0 0 0 0 0 0 0 0 | 0 0 0 0 0 0 0 0 
 * ie:
 *   X X X 00011010: Can admin security, read or write
 *				     BUT cannot create nor have total control
 * </pre>
 * The normal use of this base class is:
 * <pre class='brush:java'>
 * 		public class MyMask 
 * 			 extends BitMap<MyMask> {
 * 
 * 			private static transient int ELEMENT_A = 0;
 * 			private static transient int ELEMENT_B = 1;
 * 
 *			protected MyMask(final long bitMap) {
 *				super(bitMap);
 *			}
 * 
 * 			public MyMask setElementA() {
 * 				return this.setBit(ELEMENT_A);
 * 			}
 * 			public MyMask unsetElementA() {
 * 				return this.clearBit(ELEMENT_A);
 * 			}
 * 			public boolean isElementASetted() {
 * 				return this.bitAt(ELEMENT_A);
 * 			}
 * 		}
 * </pre>
 */
@Accessors(prefix="_")
public abstract class BitMap<SELF_TYPE extends BitMap<SELF_TYPE>> 
  		   implements CanBeRepresentedAsString,
		   	 		  Debuggable {
	
	private static final long serialVersionUID = -7795853546674157713L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="bitMap",
				   whenXml=@MarshallFieldAsXml(asParentElementValue = true))
	@Getter protected long _bitMap;	  // long containing the data
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected BitMap(final long bitMap) {
		_bitMap = bitMap;
	}
	protected BitMap(final Long bitMap) {
		_bitMap = bitMap;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Sets the bitMap info from other given bitmap
	 * (it computes an logical OR with the two bit maps)
	 * @param otherFlags
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE mergedWith(final SELF_TYPE otherFlags) {
		_bitMap = _bitMap | otherFlags.getBitMap();	// logical OR
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE copyFrom(final SELF_TYPE otherFlags) {
		for (int i=0; i < Long.SIZE; i++) {
			if (otherFlags.bitAt(i)) {
				this.setBitAt(i);
			} else {
				this.clearBitAt(i);
			}
		}
		return (SELF_TYPE)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INDIVIDUAL BIT SET/CLEAR METHODS 
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Gets a bit value
	 * @return the bit value
	 */
	public boolean bitAt(final int bitIndex) {
		return this.getBitValueAt(bitIndex);
	}
	/**
	 * Gets a bit value
	 * @return the bit value
	 */
	public boolean getBitValueAt(final int bitIndex) {
		return _getBit(_bitMap,
					   (Numbers.LONG_WIDTH*8-1)-bitIndex-1);
	}
	/**
	 * Sets a bit value
	 * @param bitIndex 
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE setBitAt(final int bitIndex) {
		_bitMap = _setBit(_bitMap,
						  (Numbers.LONG_WIDTH*8-1)-bitIndex-1);
		return (SELF_TYPE)this;
	}
	/**
	 * Resets a bit value
	 * @param bitIndex 
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE clearBitAt(final int bitIndex) {
		_bitMap = _clearBit(_bitMap,
				  			(Numbers.LONG_WIDTH*8-1)-bitIndex-1);
		return (SELF_TYPE)this;
	}
	/**
	 * Sets a bit value
	 * @param bitIndex
	 * @param val
	 */
	public SELF_TYPE setBitValueAt(final int bitIndex,final boolean val) {
		return val ? this.setBitAt(bitIndex)
				   : this.clearBitAt(bitIndex);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GLOBAL SET/CLEAR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets all bit values
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE setAllBits() {
		String str = Strings.repeat("1",Numbers.LONG_WIDTH*8-1);
		_bitMap = Long.parseLong(str,2);	// integer from binary string
		return (SELF_TYPE)this;
		// for (int i=0; i <= (Numbers.INTEGER_WIDTH*8-2); i++) {
		//	 this.setBitAt(i);
		// }		
	}
	/**
	 * Resets all bit values
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE clearAllBits() {
		String str = Strings.repeat("0",Numbers.LONG_WIDTH*8-2);
		_bitMap = Long.parseLong(str,2);	// integer from binary string
		return (SELF_TYPE)this;
		// for (int i=0; i <= (Numbers.INTEGER_WIDTH*8-2); i++) {
		//	  this.clearBitAt(i);
		// }		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	STRING CONVERSION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return this.toString();
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i <= (Numbers.LONG_WIDTH*8-2); i++) {
			sb.append( (this.bitAt(i) ? "1" : "0") );
		}
		return sb.toString();		
	}
	public String asBinaryString() {
		return Long.toBinaryString(_bitMap);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BIT MASKING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a mask to get an int's bit value
	 * @param bitIndex 
	 * @return the mask
	 */
	private static long _getMask(final long bitIndex) {
		return (1L << bitIndex);
	}
	/**
	 * Sets an int's bit and returns the new int
	 * @param originalLong
	 * @param bitIndex
	 * @return the new int
	 */
	public static long _setBit(final long originalLong,final long bitIndex) {
		return originalLong | _getMask(bitIndex);
	}
	/**
	 * Resets an int's bit and returns the new int
	 * @param originalLong
	 * @param bitIndex
	 * @return the new int
	 */
	public static long _clearBit(final long originalLong,final long bitIndex) {
		return originalLong & ~_getMask(bitIndex);
	}
	/**
	 * Returns an int's bit value
	 * @param theLong 
	 * @param bitIndex 
	 * @return the bit value
	 */
	public static boolean _getBit(final long theLong,final long bitIndex) {
		return ((theLong & _getMask(bitIndex)) != 0);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof BitMap)) return false;
		
		BitMap<?> other = (BitMap<?>)obj;
		return this.getBitMap() == other.getBitMap();
	}
	@Override
	public int hashCode() {
		return Long.valueOf(_bitMap).hashCode();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG-INFO
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append("Mapa de bits: " + _bitMap + " : " + Long.toBinaryString(_bitMap) + "\r\n");
		String currBitStr = "";
		for (int i=0; i <= (Numbers.INTEGER_WIDTH*8-2); i++) {
			currBitStr = "  " + i;
			sb.append(currBitStr.substring(currBitStr.length()-2) + "|");
		}
		sb.append("\r\n");
		for (int i=0; i <= (Numbers.INTEGER_WIDTH*8-2); i++) {
			currBitStr = (this.bitAt(i) ? " 1":" 0");
			sb.append(currBitStr.substring(currBitStr.length()-2) + "|");
		}
		sb.append("\r\n");
		return sb.toString();
	}
}
