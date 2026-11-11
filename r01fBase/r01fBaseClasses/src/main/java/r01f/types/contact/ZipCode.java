package r01f.types.contact;

import r01f.oids.OID;

public record ZipCode(String id) 
   implements OID {	
	public static ZipCode forId(final String id) {
		return new ZipCode(id);
	}
	public static ZipCode valueOf(final String str) {
		return new ZipCode(str);
	}
	@Override
	public String asString() {
		return this.id;
	}
	@Override
	public String toString() {
		return this.asString();
	}
}
