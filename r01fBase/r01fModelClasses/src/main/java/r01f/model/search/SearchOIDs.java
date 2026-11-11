package r01f.model.search;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.OIDTyped;

/**
 * Search oids
 */
public class SearchOIDs {
	/**
	 * Search engine data base identifier
	 */
	@Immutable
	@MarshallType(as="searchEngineBDId")
	public record SearchEngineDBID(@Getter String id) 
	   implements OIDTyped<String> { 	// normally this should extend OIDBaseInmutable BUT it MUST have a default no-args constructor to be serializable
		public static SearchEngineDBID from(final String id) {
			return new SearchEngineDBID(id);
		}
		public static SearchEngineDBID forId(final String id) {
			return new SearchEngineDBID(id);
		}
		public static SearchEngineDBID valueOf(final String id) {
			return new SearchEngineDBID(id);
		}
		public static SearchEngineDBID fromString(final String id) {
			return new SearchEngineDBID(id);
		}
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
	/**
	 * Search origin identifier
	 */
	@Immutable
	@MarshallType(as="searchSourceId")
	public record SearchSourceID(@Getter String id) 
	   implements OIDTyped<String> { 	// normally this should extend OIDBaseImmutable BUT it MUST have a default no-args constructor to be serializable

		public static SearchSourceID from(final String id) {
			return new SearchSourceID(id);
		}
		public static SearchSourceID forId(final String id) {
			return new SearchSourceID(id);
		}
		public static SearchSourceID valueOf(final String id) {
			return new SearchSourceID(id);
		}
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
}
