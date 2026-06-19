package r01f.services.ids;

import java.util.List;

import com.google.common.base.Splitter;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppCodeBase;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.AppComponentBase;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.collections.Lists;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ServiceIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//  BASE
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private static abstract class AppCodeAndModuleBase<A extends AppCodeBase,M extends AppComponentBase> {
		@MarshallField(as="appCode",whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final A _appCode;

		@MarshallField(as="module",whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final M _module;

		public String asString() {
			return this.toString();
		}
		@Override
		public String toString() {
			return Strings.customized("{}.{}",_appCode,_module);
		}
		@Override
		public int hashCode() {
			return this.toString().hashCode();
		}
		@Override
		public boolean equals(final Object obj) {
			if (obj == null) return false;
			if (obj == this) return true;
			if (obj instanceof AppCodeAndModuleBase) {
				AppCodeAndModuleBase<?,?> otherAppCodeAndModule = (AppCodeAndModuleBase<?,?>)obj;
				AppCodeBase appCode = otherAppCodeAndModule.getAppCode();
				AppComponentBase module = otherAppCodeAndModule.getModule();
				return appCode.equals(_appCode)
					&& module.equals(_module);
			} else if (obj instanceof String) {
				return obj.toString().equals(this.toString());
			}
			return super.equals(obj);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CLIENT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * AppCode
	 */
	@MarshallType(as="clientApiAppCode")
	@NoArgsConstructor
	public static final class ClientApiAppCode
	                  extends AppCode {
		private static final long serialVersionUID = 7093516452073951301L;
		public ClientApiAppCode(final String oid) {
			super(oid);
		}
		public static ClientApiAppCode forId(final String id) {
			return new ClientApiAppCode(id);
		}
		public static ClientApiAppCode valueOf(final String id) {
			return ClientApiAppCode.forId(id);
		}
		public static ClientApiAppCode named(final String id) {
			return ClientApiAppCode.forId(id);
		}
		public AppCode asAppCode() {
			return AppCode.forId(this.getId());
		}
		@Override
		public int hashCode() {
			return super.hashCode();
		}
		@Override
		public boolean equals(final Object obj) {
			return super.equals(obj);
		}
	}
	/**
	 * AppCode
	 */
	@MarshallType(as="clientApiComponent")
	@NoArgsConstructor
	public static final class ClientApiComponent
	                  extends AppComponent {
		private static final long serialVersionUID = 7093516452073951301L;
		public ClientApiComponent(final String oid) {
			super(oid);
		}
		public static ClientApiComponent forId(final String id) {
			return new ClientApiComponent(id);
		}
		public static ClientApiComponent valueOf(final String id) {
			return ClientApiComponent.forId(id);
		}
		public static ClientApiComponent named(final String id) {
			return ClientApiComponent.forId(id);
		}
		public AppCode asAppCode() {
			return AppCode.forId(this.getId());
		}
		@Override
		public int hashCode() {
			return super.hashCode();
		}
		@Override
		public boolean equals(final Object obj) {
			return super.equals(obj);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CORE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * AppCode
	 */
	@MarshallType(as="coreAppCode")
	@NoArgsConstructor
	public static final class CoreAppCode
	                  extends AppCode {
		private static final long serialVersionUID = 7498970290219115981L;
		public CoreAppCode(final String oid) {
			super(oid);
		}
		public static CoreAppCode of(final AppCode appCode) {
			return new CoreAppCode(appCode.asString());
		}
		public static CoreAppCode forId(final String id) {
			return new CoreAppCode(id);
		}
		public static CoreAppCode valueOf(final String id) {
			return CoreAppCode.forId(id);
		}
		public static CoreAppCode named(final String id) {
			return CoreAppCode.forId(id);
		}
		public AppCode asAppCode() {
			return AppCode.forId(this.getId());
		}
		@Override
		public int hashCode() {
			return super.hashCode();
		}
		@Override
		public boolean equals(final Object obj) {
			return super.equals(obj);
		}
	}
	/**
	 * Core module
	 */
	@MarshallType(as="coreModule")
	@NoArgsConstructor
	public static final class CoreModule
	                  extends AppComponent {
		private static final long serialVersionUID = -8935025566081906908L;
		public CoreModule(final String oid) {
			super(oid);
		}
		public static CoreModule of(final AppComponent comp) {
			return new CoreModule(comp.asString());
		}
		public static CoreModule forId(final String id) {
			return new CoreModule(id);
		}
		public static CoreModule valueOf(final String id) {
			return CoreModule.forId(id);
		}
		public static CoreModule named(final String id) {
			return CoreModule.forId(id);
		}
		@Override
		public AppComponent asAppComponent() {
			return AppComponent.forId(this.getId());
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
		public int hashCode() {
			return super.hashCode();
		}
		@Override
		public boolean equals(final Object obj) {
			return super.equals(obj);
		}
	}
	/**
	 * Core sub-module (ie db, search)
	 */
	@MarshallType(as="coreSubModule")
	@NoArgsConstructor
	public static final class CoreSubModule
	                  extends AppComponent {
		private static final long serialVersionUID = 7619551925207677429L;

		public CoreSubModule(final String oid) {
			super(oid);
		}
		public static CoreSubModule of(final AppComponent comp) {
			return new CoreSubModule(comp.asString());
		}
		public static CoreSubModule forId(final String id) {
			return new CoreSubModule(id);
		}
		public static CoreSubModule valueOf(final String id) {
			return CoreSubModule.forId(id);
		}
		public static CoreSubModule named(final String id) {
			return CoreSubModule.forId(id);
		}
		@Override
		public AppComponent asAppComponent() {
			return AppComponent.forId(this.getId());
		}
		public static final CoreSubModule DBPERSISTENCE = CoreSubModule.forId("dbpersistence");
		public static final CoreSubModule FSPERSISTENCE = CoreSubModule.forId("fspersistence");
		public static final CoreSubModule SEARCHPERSISTENCE = CoreSubModule.forId("searchpersistence");
		public static final CoreSubModule NOTIFIER = CoreSubModule.forId("notifier");
		public static final CoreSubModule RENDER = CoreSubModule.forId("render");
		public static final CoreSubModule SYNDICATION = CoreSubModule.forId("syndication");
		public static final CoreSubModule BUSINESS = CoreSubModule.forId("business");

		@Override
		public int hashCode() {
			return super.hashCode();
		}
		@Override
		public boolean equals(final Object obj) {
			return super.equals(obj);
		}
	}
	@MarshallType(as="coreAppAndModule")
	@EqualsAndHashCode(callSuper=true)
	@Accessors(prefix="_")
	public static final class CoreAppAndModule
				      extends AppCodeAndModuleBase<CoreAppCode,CoreModule> {
		public CoreAppAndModule(@MarshallFrom("appCode") final CoreAppCode api,@MarshallFrom("module") final CoreModule module) {
			super(api,module);
		}
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
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CORE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * AppCode
	 */
	@MarshallType(as="uiAppCode")
	@NoArgsConstructor
	public static final class UIAppCode
	                  extends AppCode {
		private static final long serialVersionUID = -2688898956072883546L;
		public UIAppCode(final String oid) {
			super(oid);
		}
		public static UIAppCode of(final AppCode appCode) {
			return new UIAppCode(appCode.asString());
		}
		public static UIAppCode forId(final String id) {
			return new UIAppCode(id);
		}
		public static UIAppCode valueOf(final String id) {
			return UIAppCode.forId(id);
		}
		public static UIAppCode named(final String id) {
			return UIAppCode.forId(id);
		}
		public AppCode asAppCode() {
			return AppCode.forId(this.getId());
		}
		@Override
		public int hashCode() {
			return super.hashCode();
		}
		@Override
		public boolean equals(final Object obj) {
			return super.equals(obj);
		}
	}
}
