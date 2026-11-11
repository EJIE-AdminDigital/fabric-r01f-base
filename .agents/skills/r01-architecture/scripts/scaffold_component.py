#!/usr/bin/env python3
"""
Scaffolds the -api / -core Maven projects of a FABRIC (r01f) component, and its test classes and config,
from the samplesys / samplecomp reference implementation (presencia-web/ogov-pocs/ogov-samplesys).

    python scaffold_component.py --system communic --component media-register [--domain-bai e80a177]
                                 [--api-app-code OG10 --core-app-code OG11] [--only api|core|tests]
                                 [--dry-run] [--force]

What it does (see references/task1-scaffold-component.md and task4-tests.md):
- api / core: copies every component-level file of the sample (pom.xml, .gitignore, src/**; no IDE, build
  or git files and nothing of SamplePersistableObj), replacing the identifiers; the component files that
  grow with each persistable object (ClientAPI, OIDs/IDs/Refs, summarizers, bootstrap modules,
  persistence.xml) are created empty, ready for add_persistable_object.py; writes an adapted README.md.
- registers the component's model package (and, when given, the system's app codes) in OG00AppCodes.
- tests: the component's Guice / Spring test base classes (empty), its Spring db yml, the Maven
  dependencies of the test project, the application.yml import and the CORE config in ogov-config.

New Java files are formatted with the r01-java-formatter skill; existing files only on the changed lines.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from r01gen import (SEP, T_API, T_CORE, Ids, Output, app_codes_file, add_imports, detect_app_codes,  # noqa: E402
                    detect_bai, detect_system_pascal, eol_of, exists, find_workspace, lp, pascal_from_id,
                    read_text, walk_files)

SKIP_DIRS = {"target", "bin", ".settings", ".git", ".idea", ".vscode", "vscode-target"}
SKIP_FILES = {".project", ".classpath", ".gitlab-ci.yml", ".flattened-pom.xml", "README.md"}
TEST_MODULES = ("ogovTestCommonClasses", "ogovTestGuiceClasses", "ogovTestSpringClasses")

# component files generated from the templates below (or derived from the sample) instead of copied
OWNED = {
    "OG90ClientAPIForSampleComp.java", "OG90SampleCompOIDs.java", "OG90SampleCompIDs.java", "OG90SampleCompRefs.java",
    "OG91SummarizableSampleCompPersistableObjType.java", "OG91SummarizableSampleCompPersistableObjVisitors.java",
    "OG91SummarizersForSampleCompPersistableObjs.java", "OG91SummarizersInterfacesForSampleCompPersistaleObjects.java",
    "OG91COREPersistenceBootstrapGuiceModuleForSampleComp.java", "OG91SearchGuiceModuleForSampleComp.java",
    "OG91CORESpringBootstrapConfigurationForSampleComp.java", "OG91DBSearcherProvidersForSampleCompPersistableObjs.java",
    "persistence.xml", "og91.samplecomp.persistence.xml",
}

# =========================================================================================
#  TEMPLATES (written with the reference identifiers: they go through Ids.sub like the sample)
# =========================================================================================
T_CLIENT_API = f"""package ogov.samplesys.samplecomp.api;


import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import lombok.experimental.Accessors;
import ogov.common.api.OG00AppCodes;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.security.context.SecurityContext;
import r01f.objectstreamer.Marshaller;
import r01f.services.api.ClientAPIImplBase;
import r01f.services.interfaces.ServiceInterface;

@Singleton
@Accessors(prefix="_")
public class OG90ClientAPIForSampleComp
	 extends ClientAPIImplBase {{
{SEP}
//	DELEGATES
{SEP}
	// add a @Getter private final field for each persistable object's client API delegate
	// (and its ref solver) here
{SEP}
//	CONSTRUCTOR
{SEP}
	@Inject
	@SuppressWarnings("rawtypes")
	public OG90ClientAPIForSampleComp(final Provider<SecurityContext> securityContextProvider,
											      @ModelObjectsMarshaller final Marshaller modelObjectsMarshaller,
							@Named(OG00AppCodes.SAMPLESYS_API_APPCODE_STR) final Map<Class,ServiceInterface> srvcIfaceMappings) {{
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  srvcIfaceMappings);
		// create each persistable object's client API delegates here
	}}
}}
"""

def t_holder(name: str, comment: str) -> str:
    return f"""package ogov.samplesys.samplecomp.api.model.oids;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class OG90SampleComp{name} {{
{SEP}
//	add the {comment} of the component's persistable objects here
{SEP}
}}
"""

T_GUICE_PERSISTENCE = f"""package ogov.samplesys.samplecomp.core.bootstrap.guice;

import com.google.inject.Binder;

import lombok.EqualsAndHashCode;
import ogov.common.api.OG00AppCodes;
import ogov.samplesys.samplecomp.api.OG90AppCodesForSampleComp;
import r01f.inject.HasMoreBindings;
import r01f.persistence.search.config.SearchModuleConfig;
import r01f.services.bootstrap.core.db.guice.ServicesBootstrapCoreDBPersistenceGuiceModuleBase;
import r01f.services.bootstrap.core.guice.CoreServicesBootstrapGuiceModuleBindsEventListeners;
import r01f.services.core.persistence.db.config.DBModuleConfig;

/**
 * Mappings internal to services core implementation
 */
@EqualsAndHashCode(callSuper=true)											// This is important for guice modules
public class OG91COREPersistenceBootstrapGuiceModuleForSampleComp
     extends ServicesBootstrapCoreDBPersistenceGuiceModuleBase
  implements CoreServicesBootstrapGuiceModuleBindsEventListeners,
  			 HasMoreBindings {{
{SEP}
//	CONSTRUCTOR
{SEP}
	public OG91COREPersistenceBootstrapGuiceModuleForSampleComp(final DBModuleConfig dbModuleCfg,final SearchModuleConfig searchModuleCfg) {{
		super(OG00AppCodes.SAMPLESYS_CORE_APPCODE,OG90AppCodesForSampleComp.SAMPLESYS_SAMPLECOMP_MOD,
			  new OG91DBGuiceModuleForSampleComp(dbModuleCfg),
			  new OG91SearchGuiceModuleForSampleComp(searchModuleCfg));
	}}
{SEP}
//	MORE BINDINGS
{SEP}
	@Override
	public void configureMoreBindings(final Binder binder) {{
		// --- summarizers
		// bind each persistable object's summarizer service here, e.g.:
//		binder.bind(OG91SummarizerServiceForXXX.class)
//			  .to(OG91SummarizerServiceImplForXXX.class)
//			  .in(Singleton.class);
	}}
{SEP}
//
{SEP}
	@Override
	public void bindEventListeners(final Binder binder) {{
		// bind the event listeners here, e.g.:
//		binder.bind(OG91CRUDOKEventListenerForXXX.class)
//			  .asEagerSingleton();			// <-- this FORCES event bus subscription (see BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase)
	}}
}}
"""

T_GUICE_SEARCH = f"""package ogov.samplesys.samplecomp.core.bootstrap.guice;


import lombok.EqualsAndHashCode;
import r01f.persistence.search.config.SearchModuleConfig;
import r01f.services.bootstrap.core.db.guice.DBSearchPersistenceGuiceModule;
import r01f.services.bootstrap.core.persistence.search.guice.SearchPersistenceGuiceModuleBase;

@EqualsAndHashCode(callSuper=true)				// This is important for guice modules
     class OG91SearchGuiceModuleForSampleComp
   extends SearchPersistenceGuiceModuleBase
implements DBSearchPersistenceGuiceModule {{
{SEP}
//	CONSTRUCTOR
{SEP}
	public OG91SearchGuiceModuleForSampleComp(final SearchModuleConfig cfg) {{
		super(cfg);
	}}
{SEP}
//	SEARCHER PROVIDERS
{SEP}
	// add a @Provides @Singleton method for each persistable object's searcher provider here
}}
"""

T_SPRING_CORE = f"""package ogov.samplesys.samplecomp.core.bootstrap.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ogov.common.api.OG00AppCodes;
import ogov.samplesys.samplecomp.api.OG90AppCodesForSampleComp;
import ogov.samplesys.samplecomp.core.db.services.OG91IsCOREPersistenceServiceForSampleComp;
import ogov.samplesys.samplecomp.core.db.summarizers.OG91SummarizersInterfacesForSampleCompPersistaleObjects;
import r01f.services.bootstrap.core.spring.CoreSpringComponentScan;
import r01f.services.core.ServicesCoreModuleConfig;
import r01f.services.core.annotations.ServicesCoreConfigFor;
import r01f.spring.IsSpringConfiguration;


@Configuration
@CoreSpringComponentScan(basePackageClasses  = {{
								// Service Layer
								OG91IsCOREPersistenceServiceForSampleComp.class,

								// Summarizers
								OG91SummarizersInterfacesForSampleCompPersistaleObjects.class
						}})

public class OG91CORESpringBootstrapConfigurationForSampleComp
  implements IsSpringConfiguration {{
{SEP}
//
{SEP}
	@Bean @ServicesCoreConfigFor(OG90AppCodesForSampleComp.SAMPLESYS_SAMPLECOMP_CORE_FQN)
	public ServicesCoreModuleConfig sampleCompCoreModuleConfig() {{ //..method name is used as bean name.
		return new ServicesCoreModuleConfig(OG00AppCodes.SAMPLESYS_CORE_APPCODE,OG90AppCodesForSampleComp.SAMPLESYS_SAMPLECOMP_MOD);
	}}
////////////////////////////////////////////////////////////////////////
///  Searcher Provider
////////////////////////////////////////////////////////////////////////
	// add a @Bean method for each persistable object's searcher provider here
}}
"""

T_SUMM_TYPE = f"""package ogov.samplesys.samplecomp.core.db.summarizers;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import ogov.samplesys.samplecomp.api.model.base.OG90IsSampleCompPersistableObject;
import ogov.samplesys.samplecomp.api.model.base.OG90SampleCompBaseOIDs.OG90IsSampleCompPersistableObjectOID;
import ogov.samplesys.samplecomp.api.model.summaries.base.OG90IsSummarizedSampleCompPersistableObject;
import ogov.samplesys.samplecomp.core.db.summarizers.OG91SummarizableSampleCompPersistableObjVisitors.OG91SummarizedCacheExistenceVisitorForSampleCompPersistableObj;
import ogov.samplesys.samplecomp.core.db.summarizers.OG91SummarizableSampleCompPersistableObjVisitors.OG91SummarizedCacheInvalidatorVisitorForSampleCompPersistableObj;
import ogov.samplesys.samplecomp.core.db.summarizers.OG91SummarizableSampleCompPersistableObjVisitors.OG91SummarizerVisitorForSampleCompPersistableObj;
import ogov.samplesys.samplecomp.core.db.summarizers.OG91SummarizableSampleCompPersistableObjVisitors.OG91SummarizerVisitorForSampleCompPersistableObjByObjectOid;
import r01f.model.SummarizableObjectType;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum OG91SummarizableSampleCompPersistableObjType
 implements SummarizableObjectType<OG91SummarizableSampleCompPersistableObjType> {{
	// add a constant for each summarizable persistable object of the component here
	;

	@Getter private final Class<? extends OG90IsSampleCompPersistableObjectOID> _oidType;
	@Getter private final Class<? extends OG90IsSampleCompPersistableObject<?>> _objectType;

	public abstract <S extends OG90IsSummarizedSampleCompPersistableObject<?,?>> S summarize(OG91SummarizerVisitorForSampleCompPersistableObjByObjectOid visitor);
	public abstract <S extends OG90IsSummarizedSampleCompPersistableObject<?,?>> S summarize(OG91SummarizerVisitorForSampleCompPersistableObj visitor);

	public abstract void invalidateCache(OG91SummarizedCacheInvalidatorVisitorForSampleCompPersistableObj visitor);
	public abstract boolean isCached(OG91SummarizedCacheExistenceVisitorForSampleCompPersistableObj visitor);
{SEP}
//
{SEP}
	public static <O extends OG90IsSampleCompPersistableObjectOID> boolean isWithinSummarizableObjectTypes(final Class<O> oidType) {{
		return SummarizableObjectType.isSummarizableObjectType(oidType,
															   OG91SummarizableSampleCompPersistableObjType.class);
	}}
	public static <O extends OG90IsSampleCompPersistableObjectOID> OG91SummarizableSampleCompPersistableObjType fromOidType(final Class<O> oidType) {{
		return SummarizableObjectType.fromOidType(oidType,
										   		  OG91SummarizableSampleCompPersistableObjType.class);
	}}
	public static <M extends OG90IsSampleCompPersistableObject<?>> OG91SummarizableSampleCompPersistableObjType fromObjectType(final Class<M> type) {{
		OG91SummarizableSampleCompPersistableObjType out = Stream.of(OG91SummarizableSampleCompPersistableObjType.values())
																	.filter(o -> o.getObjectType() == type)
																	.findFirst().orElse(null);
		if (out == null) throw new IllegalArgumentException(type + " is NOT a model object type for a summarizable object!");
		return out;
	}}
}}
"""

T_SUMM_VISITORS = f"""package ogov.samplesys.samplecomp.core.db.summarizers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ogov.samplesys.samplecomp.api.model.base.OG90IsSampleCompPersistableObject;
import ogov.samplesys.samplecomp.api.model.base.OG90SampleCompBaseOIDs.OG90IsSampleCompPersistableObjectOID;
import r01f.locale.Language;
import r01f.model.security.context.SecurityContext;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class OG91SummarizableSampleCompPersistableObjVisitors {{
{SEP}
//  VISITOR
{SEP}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	static abstract class OG91SampleCompPersistableObjSummarizerVisitorBase {{
		// summarizers
		protected final OG91SummarizersForSampleCompPersistableObjs _summarizers;
	}}
	private static class OG91SummarizerVisitorForSampleCompPersistableObjBase
		 	     extends OG91SampleCompPersistableObjSummarizerVisitorBase {{
		protected final SecurityContext _securityContext;
		protected final Language _lang;

		public OG91SummarizerVisitorForSampleCompPersistableObjBase(// summarizers
																	   final OG91SummarizersForSampleCompPersistableObjs summarizers,
																	   // security context
																	   final SecurityContext securityContext,
																	   // lang
																	   final Language lang) {{
			super(summarizers);
			_securityContext = securityContext;
			_lang = lang;
		}}
	}}
{SEP}
//
{SEP}
	public static class OG91SummarizerVisitorForSampleCompPersistableObjByObjectOid
		 	    extends OG91SummarizerVisitorForSampleCompPersistableObjBase {{

		@SuppressWarnings("unused")
		private final OG90IsSampleCompPersistableObjectOID _modelObjOid;

		public OG91SummarizerVisitorForSampleCompPersistableObjByObjectOid(// summarizers
																			  final OG91SummarizersForSampleCompPersistableObjs summarizers,
																			  // security context
																			  final SecurityContext securityContext,
																			  // model object oid & lang
																			  final OG90IsSampleCompPersistableObjectOID modelObjOid,final Language lang) {{
			super(summarizers,
				  securityContext,
				  lang);
			_modelObjOid = modelObjOid;
		}}
		// add a summarize{{PersistableObj}}() method for each persistable object here
	}}
	public static class OG91SummarizerVisitorForSampleCompPersistableObj
		 	    extends OG91SummarizerVisitorForSampleCompPersistableObjBase {{

		@SuppressWarnings("unused")
		private final OG90IsSampleCompPersistableObject<?> _modelObj;

		public OG91SummarizerVisitorForSampleCompPersistableObj(// summarizers
																   final OG91SummarizersForSampleCompPersistableObjs summarizers,
																   // security context
																   final SecurityContext securityContext,
																   // model object & lang
																   final OG90IsSampleCompPersistableObject<?> modelObj,final Language lang) {{
			super(summarizers,
				  securityContext,
				  lang);
			_modelObj = modelObj;
		}}
		// add a summarize{{PersistableObj}}() method for each persistable object here
	}}
{SEP}
//
{SEP}
	public static class OG91SummarizedCacheInvalidatorVisitorForSampleCompPersistableObj
		 		extends OG91SampleCompPersistableObjSummarizerVisitorBase {{

		@SuppressWarnings("unused")
		private final OG90IsSampleCompPersistableObject<?> _modelObj;

		public OG91SummarizedCacheInvalidatorVisitorForSampleCompPersistableObj(// summarizers
																				   final OG91SummarizersForSampleCompPersistableObjs summarizers,
																				   // model object
																				   final OG90IsSampleCompPersistableObject<?> modelObj) {{
			super(summarizers);
			_modelObj = modelObj;
		}}
		// add an invalidateCached{{PersistableObj}}() method for each persistable object here
	}}
{SEP}
//
{SEP}
	public static class OG91SummarizedCacheExistenceVisitorForSampleCompPersistableObj
		 		extends OG91SampleCompPersistableObjSummarizerVisitorBase {{

		@SuppressWarnings("unused")
		private final OG90IsSampleCompPersistableObjectOID _modelObjOid;

		public OG91SummarizedCacheExistenceVisitorForSampleCompPersistableObj(// summarizers
																				 final OG91SummarizersForSampleCompPersistableObjs summarizers,
																				 // model object oid
																				 final OG90IsSampleCompPersistableObjectOID modelObjOid) {{
			super(summarizers);
			_modelObjOid = modelObjOid;
		}}
		// add an isCached{{PersistableObj}}() method for each persistable object here
	}}
}}
"""

T_SUMMARIZERS = f"""package ogov.samplesys.samplecomp.core.db.summarizers;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import ogov.samplesys.samplecomp.api.model.base.OG90IsSampleCompPersistableObject;
import ogov.samplesys.samplecomp.api.model.base.OG90SampleCompBaseOIDs.OG90IsSampleCompPersistableObjectOID;
import ogov.samplesys.samplecomp.api.model.summaries.base.OG90IsSummarizedSampleCompPersistableObject;
import ogov.samplesys.samplecomp.core.db.summarizers.OG91SummarizableSampleCompPersistableObjVisitors.OG91SummarizedCacheExistenceVisitorForSampleCompPersistableObj;
import ogov.samplesys.samplecomp.core.db.summarizers.OG91SummarizableSampleCompPersistableObjVisitors.OG91SummarizedCacheInvalidatorVisitorForSampleCompPersistableObj;
import ogov.samplesys.samplecomp.core.db.summarizers.OG91SummarizableSampleCompPersistableObjVisitors.OG91SummarizerVisitorForSampleCompPersistableObj;
import ogov.samplesys.samplecomp.core.db.summarizers.OG91SummarizableSampleCompPersistableObjVisitors.OG91SummarizerVisitorForSampleCompPersistableObjByObjectOid;
import r01f.locale.Language;
import r01f.model.security.context.SecurityContext;

/**
 * Just contains all summarizers
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class OG91SummarizersForSampleCompPersistableObjs {{
{SEP}
//  FIELDS
{SEP}
	// add a @Getter protected final field for each persistable object's summarizer here

{SEP}
//
{SEP}
	public <O extends OG90IsSampleCompPersistableObjectOID,M extends OG90IsSampleCompPersistableObject<O>,
			S extends OG90IsSummarizedSampleCompPersistableObject<O,M>> S summarize(final SecurityContext securityContext,
																					   final O objOid,final Language lang) {{
		return OG91SummarizableSampleCompPersistableObjType.fromOidType(objOid.getClass())
															  .summarize(new OG91SummarizerVisitorForSampleCompPersistableObjByObjectOid(this,
																																			securityContext,
																																			objOid,lang));
	}}
	public <O extends OG90IsSampleCompPersistableObjectOID,M extends OG90IsSampleCompPersistableObject<O>,
			S extends OG90IsSummarizedSampleCompPersistableObject<O,M>> S summarize(final SecurityContext securityContext,
																					   final M obj,final Language lang) {{
		return OG91SummarizableSampleCompPersistableObjType.fromOidType(obj.getOid().getClass())
															  .summarize(new OG91SummarizerVisitorForSampleCompPersistableObj(this,
																																 securityContext,
																																 obj,lang));
	}}
	public <O extends OG90IsSampleCompPersistableObjectOID,M extends OG90IsSampleCompPersistableObject<O>> void invalidateCachedOf(final M obj) {{
		OG91SummarizableSampleCompPersistableObjType.fromOidType(obj.getOid().getClass())
													   .invalidateCache(new OG91SummarizedCacheInvalidatorVisitorForSampleCompPersistableObj(this,
																																				obj));
	}}
	public <O extends OG90IsSampleCompPersistableObjectOID> boolean isCached(final O objOid) {{
		return OG91SummarizableSampleCompPersistableObjType.fromOidType(objOid.getClass())
															  .isCached(new OG91SummarizedCacheExistenceVisitorForSampleCompPersistableObj(this,
																																			  objOid));
	}}
}}
"""

T_GUICE_TEST = f"""package ogov.samplesys.samplecomp.test.guice.persistence;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * JVM arguments:
 * -javaagent:c:/develop/java/local-libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true
 * -javaagent:/develop/java/local-libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true
 */
public class OG99TestGuiceForSampleCompPersistableObjects
	 extends OG99TestGuiceForSampleCompBase {{
{SEP}
//
{SEP}
}}
"""

T_SPRING_TEST_BASE = f"""package ogov.samplesys.samplecomp.test.spring.persistence;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import ogov.samplesys.samplecomp.api.OG90ClientAPIForSampleComp;
import r01f.test.api.spring.TestAPISpringBase;


abstract class OG99TestSpringForSampleCompPersistenceBase
       extends TestAPISpringBase {{
{SEP}
//  JUnit
{SEP}
	@BeforeAll
	public static void setUpBeforeClass() {{
		//
	}}
	@AfterAll
	public static void tearDownAfterClass()  {{
	  //
	}}
{SEP}
//
{SEP}
	public OG90ClientAPIForSampleComp getSampleCompClientApi() {{
		OG90ClientAPIForSampleComp clientApi = super.getClientApi(OG90ClientAPIForSampleComp.class);
		return clientApi;
	}}
{SEP}
//
{SEP}
}}
"""

T_SPRING_TEST = f"""package ogov.samplesys.samplecomp.test.spring.persistence;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;


/**
 * JVM arguments:
 * -javaagent:c:/develop/java/local-libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true
 * -javaagent:/develop/java/local-libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true
 */
@SpringBootTest(classes = OG99TestSpringBootstrapForSampleComp.class)
@ActiveProfiles("test")
@Slf4j
public class OG99TestSpringForSampleCompPersistableObjects
	 extends OG99TestSpringForSampleCompPersistenceBase {{
{SEP}
//
{SEP}
}}
"""


def desc(ids: Ids) -> str:
    return f" (*{ids.description}*)" if getattr(ids, "description", None) else ""


def readme_api(ids: Ids) -> str:
    return f"""# {ids.repo_prefix}-api

Client API of the **{ids.comp}** component{desc(ids)} of the **{ids.system}** system (`{ids.bai}-{ids.domain}-{ids.system}`), built on the FABRIC (r01f) layered architecture.

It holds the component's model objects, the service interfaces the CORE implements (`@ExposedServiceInterface`), and the Client API (`{ids.api}ClientAPIFor{ids.comp_pascal}`) that clients use to call them, plus its Guice and Spring bootstrap.

## Identifiers

| Item | Value |
| :--- | :--- |
| Domain / BAI | `{ids.domain}` / `{ids.bai}` |
| System | `{ids.system}` |
| Component | `{ids.comp}` (`{ids.comp_pkg}` in packages and runtime ids) |
| API app code | `{ids.api}` (`{ids.api.lower()}`) |
| CORE app code | `{ids.core}` (`{ids.core.lower()}`) |
| Runtime API FQN | `{ids.api.lower()}.{ids.comp_pkg}` |
| Base package | `{ids.base_pkg}.api` |

## Maven modules

| Module | Contents |
| :--- | :--- |
| `{ids.artifact}APIModelClasses` | Model objects, base model types, OIDs / IDs / Refs, search and summary models (`.api.model`). |
| `{ids.artifact}APIClasses` | App codes, service interfaces (`.api.interfaces`), Client API, delegates and ref solvers. |
| `{ids.artifact}APIBootstrapGuiceClasses` | Guice module `{ids.api}APIBootstrapGuiceModuleFor{ids.comp_pascal}`. |
| `{ids.artifact}APIBootstrapSpringClasses` | Spring configuration `{ids.api}APISpringBootstrapConfigurationFor{ids.comp_pascal}`. |

Persistable objects are added with the `r01-architecture` skill (`scripts/add_persistable_object.py`, or the playbooks in `references/`).

## Build

```bash
mvn -Drevision={{version}} clean install
```

The parent `pom.xml` uses `${{revision}}` and placeholder versions (`OGOV-COMMON-API-VERSION`, `R01F-BASE-VERSION`, ...) that the CI replaces.
"""


def readme_core(ids: Ids) -> str:
    core_l = ids.core.lower()
    return f"""# {ids.repo_prefix}-core

CORE (DB persistence) of the **{ids.comp}** component{desc(ids)} of the **{ids.system}** system (`{ids.bai}-{ids.domain}-{ids.system}`), built on the FABRIC (r01f) layered architecture.

It implements the service interfaces defined in [`{ids.repo_prefix}-api`](../{ids.repo_prefix}-api) in three tiers (services impl, services delegates, DAOs / searchers / summarizers) over JPA (Hibernate), plus its Guice and Spring bootstrap.

## Identifiers

| Item | Value |
| :--- | :--- |
| Domain / BAI | `{ids.domain}` / `{ids.bai}` |
| System | `{ids.system}` |
| Component | `{ids.comp}` (`{ids.comp_pkg}` in packages and runtime ids) |
| CORE app code | `{ids.core}` (`{core_l}`) |
| API app code | `{ids.api}` (`{ids.api.lower()}`) |
| Runtime CORE FQN | `{core_l}.{ids.comp_pkg}` |
| Persistence unit | `persistenceUnit.{core_l}.{ids.comp_pkg}` |
| Base package | `{ids.base_pkg}.core` |

## Maven modules

| Module | Contents |
| :--- | :--- |
| `{ids.artifact}COREDBPersistenceClasses` | DB entities, transformers, DAOs, searchers, summarizers and CORE services (`.core.db`), and `META-INF/persistence.xml` + `META-INF/jpa/{core_l}.{ids.comp_pkg}.persistence.xml`. |
| `{ids.artifact}COREBootstrapGuiceClasses` | Guice bootstrap (`{ids.core}COREBootstrapGuiceConfigBuilderFor{ids.comp_pascal}`, persistence, DB and search modules). |
| `{ids.artifact}COREBootstrapSpringClasses` | Spring bootstrap (`{ids.core}CORESpringBootstrapConfigurationFor{ids.comp_pascal}` and its persistence post processor). |

The CORE of each persistable object is added with the `r01-architecture` skill (`scripts/add_persistable_object.py`, or the playbooks in `references/`). The component's CORE config (`{core_l}.{ids.comp_pkg}` properties) lives in `ogov-config`.

## Build

```bash
mvn -Drevision={{version}} clean install
```

Requires the `-api` artifacts installed first. The parent `pom.xml` uses `${{revision}}` and placeholder versions (`OGOV-{ids.system_upper}-{ids.comp_upper}-API-VERSION`, `OGOV-COMMON-CORE-VERSION`, ...) that the CI replaces.
"""


# =========================================================================================
#  HELPERS
# =========================================================================================
def is_copied(rel: Path) -> bool:
    parts = rel.parts
    if any(p in SKIP_DIRS for p in parts) or rel.name in SKIP_FILES:
        return False
    if "samplepersistableobj" in parts or "SamplePersistableObj" in rel.name or rel.name in OWNED:
        return False
    return "src" in parts or rel.name in ("pom.xml", ".gitignore")


def pad_box_comments(tpl: str, out: str) -> str:
    """Keeps the width of '<!-- ... -->' box comments whose text changed length with the substitution."""
    res = []
    for a, b in zip(tpl.split("\n"), out.split("\n")):
        m = re.match(r"^(\s*<!--.*?\S)(\s+)(-->)\s*$", b)
        if m and len(a) != len(b) and re.match(r"^\s*<!--.*-->\s*$", a):
            b = m.group(1) + " " * max(1, len(a.rstrip()) - len(m.group(1)) - 3) + "-->"
        res.append(b)
    return "\n".join(res)


def target_path(ids: Ids, rel: Path, project_dir: Path) -> Path:
    return project_dir / ids.sub(rel.as_posix())


# =========================================================================================
#  API / CORE
# =========================================================================================
def scaffold_project(kind: str, ids: Ids, sample: Path, out_root: Path, out: Output, force: bool) -> None:
    src = sample / f"bai-ogov-samplesys-samplecomp-{kind}"
    dst = out_root / f"{ids.repo_prefix}-{kind}"
    if exists(dst) and any(walk_files(dst)) and not force:
        out.notes.append(f"SKIPPED: {dst} already exists (use --force to write into it)")
        return
    for f in walk_files(src):
        rel = f.relative_to(src)
        if not is_copied(rel):
            continue
        tpl = read_text(f)
        text = ids.sub(tpl)
        if f.suffix == ".xml":
            text = pad_box_comments(tpl, text)
        out.write_new(dst / ids.sub(rel.as_posix()), text, eol=eol_of(f), force=force)

    if kind == "api":
        mc = dst / f"{ids.artifact}APIModelClasses/src/main/java" / ids.base_pkg.replace(".", "/") / "api"
        ac = dst / f"{ids.artifact}APIClasses/src/main/java" / ids.base_pkg.replace(".", "/") / "api"
        out.write_new(ac / f"{ids.api}ClientAPIFor{ids.comp_pascal}.java", ids.sub(T_CLIENT_API), force=force)
        for name, comment in (("OIDs", "OIDs"), ("IDs", "business IDs"), ("Refs", "Refs")):
            out.write_new(mc / f"model/oids/{ids.api}{ids.comp_pascal}{name}.java", ids.sub(t_holder(name, comment)), force=force)
        out.write_new(dst / "README.md", readme_api(ids), eol="\n", force=force)
    else:
        db = dst / f"{ids.artifact}COREDBPersistenceClasses/src/main"
        dbj = db / "java" / ids.base_pkg.replace(".", "/") / "core/db"
        gj = dst / f"{ids.artifact}COREBootstrapGuiceClasses/src/main/java" / ids.base_pkg.replace(".", "/") / "core/bootstrap/guice"
        sj = dst / f"{ids.artifact}COREBootstrapSpringClasses/src/main/java" / ids.base_pkg.replace(".", "/") / "core/bootstrap/spring"
        out.write_new(gj / f"{ids.core}COREPersistenceBootstrapGuiceModuleFor{ids.comp_pascal}.java", ids.sub(T_GUICE_PERSISTENCE), force=force)
        out.write_new(gj / f"{ids.core}SearchGuiceModuleFor{ids.comp_pascal}.java", ids.sub(T_GUICE_SEARCH), force=force)
        out.write_new(sj / f"{ids.core}CORESpringBootstrapConfigurationFor{ids.comp_pascal}.java", ids.sub(T_SPRING_CORE), force=force)
        sz = dbj / "summarizers"
        out.write_new(sz / f"{ids.core}Summarizable{ids.comp_pascal}PersistableObjType.java", ids.sub(T_SUMM_TYPE), force=force)
        out.write_new(sz / f"{ids.core}Summarizable{ids.comp_pascal}PersistableObjVisitors.java", ids.sub(T_SUMM_VISITORS), force=force)
        out.write_new(sz / f"{ids.core}SummarizersFor{ids.comp_pascal}PersistableObjs.java", ids.sub(T_SUMMARIZERS), force=force)
        # summarizer service interfaces: the sample's, without its object
        sdb = src / "ogovSampleSysSampleCompCOREDBPersistenceClasses/src/main"
        t = read_text(sdb / "java/ogov/samplesys/samplecomp/core/db/summarizers/OG91SummarizersInterfacesForSampleCompPersistaleObjects.java")
        t = "\n".join(l for l in t.split("\n") if not (l.startswith("import ") and "SamplePersistableObj" in l))
        m = re.search(rf"^/{{89}}[ \t]*\n//[ \t]*SamplePe\w*[ \t]*\n", t, re.M)
        t = t[:m.start()] + "\t// add the summarizer service interface of each persistable object here\n}\n"
        out.write_new(sz / f"{ids.core}SummarizersInterfacesFor{ids.comp_pascal}PersistaleObjects.java", ids.sub(t), force=force)
        # persistence.xml: the sample's, without its entity
        for rel in ("resources/META-INF/persistence.xml", "resources/META-INF/jpa/og91.samplecomp.persistence.xml"):
            t = read_text(sdb / rel)
            t = re.sub(r"<class>[^<]*SamplePersistableObj</class>", "<!-- add the DB entity of each persistable object here -->", t)
            out.write_new(db / ids.sub(rel), ids.sub(t), eol=eol_of(sdb / rel), force=force)
        out.write_new(dst / "README.md", readme_core(ids), eol="\n", force=force)


def register_app_codes(codes_file: Path, ids: Ids, out: Output, register_system: bool) -> None:
    t = read_text(codes_file)
    new = t
    if register_system and f"{ids.system_upper}_API_APPCODE_STR" not in t:
        block = (f"{SEP}\n//\t{ids.system_upper} SYSTEM\n{SEP}\n"
                 f"\t////////// --- SYSTEM: {ids.system_pascal}\n"
                 f"\tpublic static final String {ids.system_upper}_STR = \"{ids.system}\";\n\n"
                 f"\tpublic static final String {ids.system_upper}_API_APPCODE_STR = \"{ids.api.lower()}\";\n"
                 f"\tpublic static final ClientApiAppCode {ids.system_upper}_API_APPCODE = ClientApiAppCode.forId({ids.system_upper}_API_APPCODE_STR);\n\n"
                 f"\tpublic static final String {ids.system_upper}_CORE_APPCODE_STR = \"{ids.core.lower()}\";\n"
                 f"\tpublic static final CoreAppCode {ids.system_upper}_CORE_APPCODE = CoreAppCode.forId({ids.system_upper}_CORE_APPCODE_STR);\n")
        i = new.rindex("}")                     # a new section at the end of the class
        new = new[:i].rstrip("\n") + "\n\n" + block + new[i:]
    pkg = f'JavaPackage.named("{ids.base_pkg}.api.model")'
    if pkg not in new:
        m = re.search(r'^([ \t]*)JavaPackage\.named\("ogov\.common\.api\.context"\)', new, re.M)
        if not m:
            out.notes.append(f"ERROR: MODEL_JAVA_PACKAGES not found in {codes_file}: add {pkg} by hand")
        else:
            new = new[:m.start()] + f"{m.group(1)}{pkg},\n" + new[m.start():]
    out.write_modified(codes_file, new)


# =========================================================================================
#  TESTS & CONFIG
# =========================================================================================
def scaffold_tests(ids: Ids, tests_root: Path, config_root: Path, config_byenv_root: Path, out: Output, force: bool) -> None:
    pkg_path = ids.base_pkg.replace(".", "/")
    sg = tests_root / "ogovTestGuiceClasses/src/test/java/ogov/samplesys/samplecomp/test/guice/persistence"
    ss = tests_root / "ogovTestSpringClasses/src/test/java/ogov/samplesys/samplecomp/test/spring/persistence"
    tg = tests_root / f"ogovTestGuiceClasses/src/test/java/{pkg_path}/test/guice/persistence"
    ts = tests_root / f"ogovTestSpringClasses/src/test/java/{pkg_path}/test/spring/persistence"
    obj_test_import = "import ogov.samplesys.samplecomp.test.persistence.OG99TestForSamplePersistableObjPersistence;\n"

    # guice base: the sample's, with an empty TESTS section
    t = read_text(sg / "OG99TestGuiceForSampleCompBase.java")
    s = re.search(rf"^/{{89}}[ \t]*\n//[ \t]*TEST[ \t]*\n", t, re.M).start()
    e = re.search(rf"^/{{89}}[ \t]*\n//[ \t]*APIs[ \t]*\n", t, re.M).start()
    t = t[:s] + f"{SEP}\n//\tTESTS\n{SEP}\n" + t[e:]
    t = t.replace(obj_test_import, "")
    out.write_new(tg / f"OG99TestGuiceFor{ids.comp_pascal}Base.java", ids.sub(t), force=force)
    out.write_new(tg / f"OG99TestGuiceFor{ids.comp_pascal}PersistableObjects.java", ids.sub(T_GUICE_TEST), force=force)

    # spring bootstrap: the sample's, without its test case beans
    t = read_text(ss / "OG99TestSpringBootstrapForSampleComp.java")
    s = re.search(r"^[ \t]*@Bean[ \t]*\n[ \t]*public OG99TestFor", t, re.M).start()
    t = t[:s] + t[t.rindex("}"):]
    t = t.replace(obj_test_import, "").replace("// SamplePersistableObj", "// SampleComp")
    out.write_new(ts / f"OG99TestSpringBootstrapFor{ids.comp_pascal}.java", ids.sub(t), force=force)
    out.write_new(ts / f"OG99TestSpringFor{ids.comp_pascal}PersistenceBase.java", ids.sub(T_SPRING_TEST_BASE), force=force)
    out.write_new(ts / f"OG99TestSpringFor{ids.comp_pascal}PersistableObjects.java", ids.sub(T_SPRING_TEST), force=force)

    # spring db config
    res = tests_root / "ogovTestSpringClasses/src/test/resources"
    yml = read_text(res / "config/default/samplesys-samplecomp-database.yml")
    out.write_new(res / f"config/default/{ids.system}-{ids.comp_pkg}-database.yml",
                  yml.replace("  samplecomp:\n", f"  {ids.comp_pkg}:\n", 1), eol=eol_of(res / "config/default/samplesys-samplecomp-database.yml"), force=force)

    # maven
    api_v, core_v = f"{ids.base_pkg}.api.version", f"{ids.base_pkg}.core.version"
    p = tests_root / "pom.xml"
    t = read_text(p)
    if api_v not in t:
        anchor = re.search(r"^[ \t]*<ogov\.samplesys\.samplecomp\.core\.version>[^\n]*\n", t, re.M)
        if not anchor:
            out.notes.append(f"ERROR: samplecomp version properties not found in {p}: register {api_v} / {core_v} by hand")
        else:
            up = f"OGOV-{ids.system_upper}-{ids.comp_upper}"
            t = (t[:anchor.end()] + "\t\t\n"
                 + f"\t\t<{api_v}>{up}-API-VERSION</{api_v}>\n\t\t<{core_v}>{up}-CORE-VERSION</{core_v}>\n" + t[anchor.end():])
    out.write_modified(p, t)
    box = f"\t\t<!-- {ids.system_pascal} / {ids.comp_pascal}".ljust(2 + len("<!-- ///////////////////////////////////////// ")) + "-->"
    for mod, anchor_art, art, ver in (
            ("ogovTestCommonClasses", "ogovSampleSysSampleCompAPIClasses", f"{ids.artifact}APIClasses", api_v),
            ("ogovTestGuiceClasses", "ogovSampleSysSampleCompCOREBootstrapGuiceClasses", f"{ids.artifact}COREBootstrapGuiceClasses", core_v),
            ("ogovTestSpringClasses", "ogovSampleSysSampleCompCOREBootstrapSpringClasses", f"{ids.artifact}COREBootstrapSpringClasses", core_v)):
        p = tests_root / mod / "pom.xml"
        t = read_text(p)
        if f"<artifactId>{art}</artifactId>" not in t:
            i = t.find(f"<artifactId>{anchor_art}</artifactId>")
            if i < 0:
                out.notes.append(f"ERROR: {anchor_art} dependency not found in {p}: add {art} by hand")
                continue
            j = t.index("</dependency>\n", i) + len("</dependency>\n")
            group = f"{ids.base_pkg}.api" if art.endswith("APIClasses") else f"{ids.base_pkg}.core"
            t = (t[:j] + "\n\t\t<!-- ///////////////////////////////////////// -->\n" + box + "\n"
                 + "\t\t<!-- ///////////////////////////////////////// -->\n\t\t<dependency>\n"
                 + f"\t\t\t<groupId>{group}</groupId>\n\t\t\t<artifactId>{art}</artifactId>\n"
                 + f"\t\t\t<version>${{{ver}}}</version>\n\t\t</dependency>\n" + t[j:])
        out.write_modified(p, t)
    p = res / "application.yml"
    t = read_text(p)
    line = f"      - optional:classpath:config/${{env:default}}/{ids.system}-{ids.comp_pkg}-database.yml\n"
    if line not in t:
        anchor = "      - optional:classpath:config/${env:default}/samplesys-samplecomp-database.yml\n"
        if anchor not in t:
            out.notes.append(f"ERROR: samplecomp import not found in {p}: add {line.strip()} by hand")
        else:
            i = t.index(anchor) + len(anchor)
            t = t[:i] + line + t[i:]
    out.write_modified(p, t)

    # CORE config (copied from og91): the system's (only if missing) and the component's
    core_l, t_core_l = ids.core.lower(), T_CORE.lower()
    for root, rels in ((config_root, (f"{t_core_l}/{t_core_l}.services.properties.xml", f"{t_core_l}/{t_core_l}.dbpersistence.properties.xml",
                                      f"{t_core_l}/components/{t_core_l}.services.xml")),
                       (config_byenv_root, (f"{t_core_l}/{t_core_l}.dbpersistence.properties.xml",))):
        for rel in rels:
            dst = root / rel.replace(t_core_l, core_l)
            if not exists(dst):
                out.write_new(dst, read_text(root / rel).replace(f"{t_core_l}/{t_core_l}.", f"{core_l}/{core_l}."), eol=eol_of(root / rel))
    src = config_root / f"{t_core_l}/components/{t_core_l}.samplecomp.dbpersistence.xml"
    out.write_new(config_root / f"{core_l}/components/{core_l}.{ids.comp_pkg}.dbpersistence.xml",
                  read_text(src).replace(f"{t_core_l}/{t_core_l}.", f"{core_l}/{core_l}."), eol=eol_of(src), force=force)


# =========================================================================================
#  MAIN
# =========================================================================================
def resolve_ids(args, pocs: Path, codes_file: Path) -> Ids:
    sys_dir = pocs / f"ogov-{args.system}"
    api, core = detect_app_codes(codes_file, args.system)
    api, core = (args.api_app_code or api), (args.core_app_code or core)
    if not api or not core:
        sys.exit(f"ERROR: the app codes of {args.system} are not registered in {codes_file}: pass --api-app-code and "
                 f"--core-app-code (API even OGx0, CORE the next odd OGx1) and they will be registered")
    bai = args.domain_bai or detect_bai(sys_dir, args.system)
    if not bai:
        sys.exit(f"ERROR: {sys_dir} has no projects to take the domain BAI from: pass --domain-bai")
    return Ids(system=args.system, system_pascal=args.system_pascal or detect_system_pascal(sys_dir, args.system) or pascal_from_id(args.system),
               comp=args.component, comp_pascal=args.component_pascal or pascal_from_id(args.component),
               bai=bai, api=api.upper(), core=core.upper())


def common_args(ap: argparse.ArgumentParser) -> None:
    ap.add_argument("--system", required=True, help="system id, lowercase (communic)")
    ap.add_argument("--component", required=True, help="component id as in folder names (media-register)")
    ap.add_argument("--domain-bai", help="repo folder prefix (detected from the system's projects)")
    ap.add_argument("--system-pascal", help="PascalCase of the system (detected from its poms, else capitalized)")
    ap.add_argument("--component-pascal", help="PascalCase of the component (default: media-register -> MediaRegister)")
    ap.add_argument("--description", help='what the component is, for the README (e.g. "Registro de medios")')
    ap.add_argument("--api-app-code", help="API app code, e.g. OG10 (detected from OG00AppCodes)")
    ap.add_argument("--core-app-code", help="CORE app code, e.g. OG11 (detected from OG00AppCodes)")
    ap.add_argument("--workspace", type=Path, help="folder containing presencia-web (detected)")
    ap.add_argument("--out-root", type=Path, help="where the -api / -core projects go (default: ogov-pocs/ogov-{system})")
    ap.add_argument("--tests-root", type=Path, help="the domain test project (default: ogov-pocs/ogov-test/e80a180-ogov-test)")
    ap.add_argument("--config-root", type=Path, help="ogovCOREConfig/src/main/config (default: in ogov-pocs/ogov-config)")
    ap.add_argument("--config-byenv-root", type=Path, help="ogovCOREConfigByEnv/src/main/config/default")
    ap.add_argument("--app-codes-file", type=Path, help="OG00AppCodes.java (default: in ogov-common)")
    ap.add_argument("--dry-run", action="store_true")
    ap.add_argument("--no-format", action="store_true", help="don't apply the r01-java-formatter skill")
    ap.add_argument("--force", action="store_true", help="overwrite existing generated files")


def roots(args, ws: Path):
    pocs = ws / "presencia-web/ogov-pocs"
    return (pocs,
            args.tests_root or pocs / "ogov-test/e80a180-ogov-test",
            args.config_root or pocs / "ogov-config/e80a180-ogov-config/ogovCOREConfig/src/main/config",
            args.config_byenv_root or pocs / "ogov-config/e80a180-ogov-config-byenv/ogovCOREConfigByEnv/src/main/config/default",
            args.app_codes_file or app_codes_file(pocs))


def main() -> None:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    common_args(ap)
    ap.add_argument("--only", choices=("api", "core", "tests"), help="generate only one part")
    args = ap.parse_args()
    ws = args.workspace or find_workspace()
    pocs, tests_root, config_root, byenv_root, codes_file = roots(args, ws)
    registered = all(detect_app_codes(codes_file, args.system))
    ids = resolve_ids(args, pocs, codes_file)
    ids.description = args.description
    out_root = args.out_root or pocs / f"ogov-{ids.system}"
    sample = pocs / "ogov-samplesys"
    print(f"system={ids.system} ({ids.system_pascal}, {ids.system_upper})  component={ids.comp} ({ids.comp_pascal}, {ids.comp_pkg})"
          f"  bai={ids.bai}  api={ids.api}  core={ids.core}")
    for part in ("api", "core", "tests"):
        if args.only and args.only != part:
            continue
        out = Output(dry_run=args.dry_run, formatted=not args.no_format)
        if part == "tests":
            print(f"\n[tests] {tests_root}")
            scaffold_tests(ids, tests_root, config_root, byenv_root, out, args.force)
            out.report(pocs)
        else:
            print(f"\n[{part}] {out_root / f'{ids.repo_prefix}-{part}'}")
            scaffold_project(part, ids, sample, out_root, out, args.force)
            out.report(out_root)
        if part == "api":
            print(f"\n[app codes] {codes_file}")
            out2 = Output(dry_run=args.dry_run, formatted=not args.no_format)
            register_app_codes(codes_file, ids, out2, register_system=not registered)
            out2.report(pocs)


if __name__ == "__main__":
    main()
