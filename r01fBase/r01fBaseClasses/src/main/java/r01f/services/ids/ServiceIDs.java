package r01f.services.ids;

import java.util.List;

import com.google.common.base.Splitter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import r01f.guids.CommonOIDs.IsAppCode;
import r01f.guids.CommonOIDs.IsAppComponent;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.collections.Lists;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ServiceIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//  CLIENT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * AppCode
	 */
	@MarshallType(as="clientApiAppCode")
	public record ClientApiAppCode(@Getter String id)
	   implements IsAppCode {
		public static ClientApiAppCode of(final IsAppCode appCode) {
			return new ClientApiAppCode(appCode.asString());
		}
		public static ClientApiAppCode from(final String id) {
			return new ClientApiAppCode(id);
		}
		public static ClientApiAppCode forId(final String id) {
			return new ClientApiAppCode(id);
		}
		public static ClientApiAppCode valueOf(final String id) {
			return ClientApiAppCode.forId(id);
		}
		public static ClientApiAppCode fromString(final String id) {
			return ClientApiAppCode.forId(id);
		}
		public static ClientApiAppCode named(final String id) {
			return ClientApiAppCode.forId(id);
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
	 * AppCode
	 */
	@MarshallType(as="clientApiComponent")
	public record ClientApiComponent(@Getter String id)
	   implements IsAppComponent {
		public static ClientApiComponent fo(final IsAppComponent appComponent) {
			return new ClientApiComponent(appComponent.asString());
		}
		public static ClientApiComponent from(final String id) {
			return new ClientApiComponent(id);
		}
		public static ClientApiComponent forId(final String id) {
			return new ClientApiComponent(id);
		}
		public static ClientApiComponent valueOf(final String id) {
			return ClientApiComponent.forId(id);
		}
		public static ClientApiComponent fromString(final String id) {
			return new ClientApiComponent(id);
		}
		public static ClientApiComponent named(final String id) {
			return ClientApiComponent.forId(id);
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
/////////////////////////////////////////////////////////////////////////////////////////
//  CORE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * AppCode
	 */
	@MarshallType(as="coreAppCode")
	public record CoreAppCode(@Getter String id)
	   implements IsAppCode {
		public static CoreAppCode of(final IsAppCode appCode) {
			return new CoreAppCode(appCode.asString());
		}
		public static CoreAppCode from(final String id) {
			return new CoreAppCode(id);
		}
		public static CoreAppCode forId(final String id) {
			return new CoreAppCode(id);
		}
		public static CoreAppCode valueOf(final String id) {
			return CoreAppCode.forId(id);
		}
		public static CoreAppCode fromString(final String id) {
			return new CoreAppCode(id);
		}
		public static CoreAppCode named(final String id) {
			return CoreAppCode.forId(id);
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
	 * Core module
	 */
	@MarshallType(as="coreModule")
	public record CoreModule(@Getter String id)
	   implements IsAppComponent {
		
		public static CoreModule of(final IsAppComponent comp) {
			return new CoreModule(comp.asString());
		}
		public static CoreModule from(final String id) {
			return new CoreModule(id);
		}
		public static CoreModule forId(final String id) {
			return new CoreModule(id);
		}
		public static CoreModule valueOf(final String id) {
			return CoreModule.forId(id);
		}
		public static CoreModule fromString(final String id) {
			return new CoreModule(id);
		}
		public static CoreModule named(final String id) {
			return CoreModule.forId(id);
		}
		
		public static final CoreModule SERVICES = CoreModule.forId("services");
		public static final CoreModule PUBLISHER = CoreModule.forId("publisher");
		public static final CoreModule OTP = CoreModule.forId("otp");
	
		public static final CoreModule SECURITY = CoreModule.forId("security");
		public static final CoreModule HISTORY = CoreModule.forId("history");
		public static final CoreModule CMS = CoreModule.forId("cms");
		public static final CoreModule GEO = CoreModule.forId("geo");
		public static final CoreModule SCHEDULER = CoreModule.forId("scheduler");
		public static final CoreModule REPORTING = CoreModule.forId("reporting");
		
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
	 * Core sub-module (ie db, search)
	 */
	@MarshallType(as="coreSubModule")
	public record CoreSubModule(@Getter String id)
	   implements IsAppComponent {
		
		public static CoreSubModule of(final IsAppComponent comp) {
			return new CoreSubModule(comp.asString());
		}
		public static CoreSubModule from(final String id) {
			return new CoreSubModule(id);
		}
		public static CoreSubModule forId(final String id) {
			return new CoreSubModule(id);
		}
		public static CoreSubModule valueOf(final String id) {
			return CoreSubModule.forId(id);
		}
		public static CoreSubModule fromString(final String id) {
			return new CoreSubModule(id);
		}
		public static CoreSubModule named(final String id) {
			return CoreSubModule.forId(id);
		}
		public static final CoreSubModule DBPERSISTENCE = CoreSubModule.forId("dbpersistence");
		public static final CoreSubModule FSPERSISTENCE = CoreSubModule.forId("fspersistence");
		public static final CoreSubModule SEARCHPERSISTENCE = CoreSubModule.forId("searchpersistence");
		public static final CoreSubModule NOTIFIER = CoreSubModule.forId("notifier");
		public static final CoreSubModule RENDER = CoreSubModule.forId("render");
		public static final CoreSubModule SYNDICATION = CoreSubModule.forId("syndication");
		public static final CoreSubModule BUSINESS = CoreSubModule.forId("business");

		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallType(as="coreAppAndModule")
	public record CoreAppAndModule(@MarshallFrom("appCode") 
								   @Getter CoreAppCode appCode,
								   @MarshallFrom("module") 
								   @Getter CoreModule module) {
		public static CoreAppAndModule of(final CoreAppCode appCode,final CoreModule module) {
			return new CoreAppAndModule(appCode,module);
		}
		public static CoreAppAndModule of(final String appCodeAndModule) {
			List<String> parts = Lists.newArrayList(Splitter.on(".")
									 						.split(appCodeAndModule));
			if (parts.size() == 2) {
				return CoreAppAndModule.of(CoreAppCode.forId(parts.get(0)),
										   CoreModule.forId(parts.get(1)));
			} else if (parts.size() > 2) {
				StringBuilder comp = new StringBuilder();
				for (int i=1; i < parts.size(); i++) {
					comp.append(parts.get(i));
					if (i < parts.size()-1) comp.append(".");
				}
				return CoreAppAndModule.of(CoreAppCode.forId(parts.get(0)),CoreModule.forId(comp.toString()));
			} else {
				throw new IllegalStateException();
			}
		}
		public String asString() {
			return this.toString();
		}
		@Override
		public String toString() {
			return Strings.customized("{}.{}",this.appCode,this.module);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UI
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * AppCode
	 */
	@MarshallType(as="uiAppCode")
	public record UIAppCode(@Getter String id)
	   implements IsAppCode {
		
		public static UIAppCode of(final IsAppCode appCode) {
			return new UIAppCode(appCode.asString());
		}
		public static UIAppCode from(final String id) {
			return new UIAppCode(id);
		}
		public static UIAppCode forId(final String id) {
			return new UIAppCode(id);
		}
		public static UIAppCode valueOf(final String id) {
			return UIAppCode.forId(id);
		}
		public static UIAppCode fromString(final String id) {
			return new UIAppCode(id);
		}
		public static UIAppCode named(final String id) {
			return UIAppCode.forId(id);
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
