package r01f.cache;

import java.io.Serializable;

import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.oids.OID;
import r01f.util.types.Strings;

@Accessors(prefix="_")
public record CacheKeyForLangDependentObject<O extends OID>(O oid,
															Language lang)
  implements Serializable {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public O getOid() {
		return this.oid;
	}
	public Language getLang() {
		return this.lang;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TO STRING / EQUALS / HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Strings.customized("{}:{}",this.lang,this.oid);
	}
}
