package r01f.model.catalogable.geo.metadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.model.metadata.FieldID;
import r01f.model.metadata.FieldIDToken;
import r01f.model.metadata.HasMetaDataForModelObject;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.Storage;
import r01f.types.geo.GeoPositionFilter;

public interface MetaDataForGeoCatalogableObject
	     extends HasMetaDataForModelObject {
/////////////////////////////////////////////////////////////////////////////////////////
//  SEARCHABLE METADATAS
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public enum SEARCHABLE_METADATA 
	 implements FieldIDToken {
		GEO_POSITION ("geoPosition");

		@Getter private final String _token;
		
		public FieldID asFieldId() {
			return FieldID.from(this);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GEO
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
						@DescInLang(language=Language.SPANISH,value="Posición geográfica"),
						@DescInLang(language=Language.BASQUE,value="[eu] Posición geográfica"),
						@DescInLang(language=Language.ENGLISH,value="Geo Position")
				  },
				  storage = @Storage(indexed=true,stored=true,tokenized=false))
	public GeoPositionFilter getGeoPosition();
}
