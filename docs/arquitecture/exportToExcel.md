# Como hacer un servicio para exportar datos a Excel #

## Propósito
Establecer el patrón obligatorio para crear funcionalidades de exportación a Excel, asegurando coherencia en nomenclatura, ubicación y responsabilidad de clases.

## Puntos reseñables
La exportación a Excel se realiza de forma asíncrona utilizando la libreria `io.reactivex.rxjava3`

## Parametros iniciales
Para construir el servicio de exportación a Excel se necesita saber mínimo los siguientes datos:

- **PROYECTO**: Proyecto en el que se creará el servicio de exportación. En los ejemplos de esta guia se usará `Portal`
- **FICHERO**: Nombre del fichero que se va a generar, es decir normalmente en un proyecto se generarán mas de una exportación a Excel, cada una será de un fichero. En los ejemplos de esta guia se usará `PortalPages`


## Ubicación de los artefactos ##

Todas las clases necesarias deben ir en artefactos de maven de Reporting. 

- r01PLATEAWebCORE{PROYECTO}ReportingClasses
- r01PLATEAWebAPI{PROYECTO}ReportingClasses

Por ejemplo en el caso de portales sería en **r01PLATEAWebCOREPortalReportingClasses** para las clases del CORE y
**r01PLATEAWebAPIPortalReportingClasses** para las clases del API.

## SyndicationProgress ##

Sirve para dar soporte al indicador del progreso de la exportación y se tiene que crear en el paquete **ModelClasses** del API. Por ejemplo para el proyecto de portales seria el proyecto: **r01PLATEAWebAPIPortalModelClasses**

La clase tiene que:
- estar en el paquete r01mb.model.{PROYECTO}.syndication, en este caso de portal es **r01mb.model.portal.syndication**
- llamarse R01MSyndicationProgressFor{PROYECTO}, en este caso **R01MSyndicationProgressForPortal**
- extender de **ItemFlowProcessingProgress**
- implementar el objeto de modelo general del proyecto, es este caso **R01MPortalObject**

Se muestra un ejemplo:

```java
package r01mb.model.portal.syndication;

import java.util.Date;

import lombok.experimental.Accessors;
import r01f.core.batch.ItemFlowProcessingProgress;
import r01mb.model.portal.base.R01MPortalObject;

@Accessors(prefix="_")
public class R01MSyndicationProgressForPortal  
     extends ItemFlowProcessingProgress 
  implements R01MPortalObject {

    private static final long serialVersionUID = -1040763497029735656L;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
    public R01MSyndicationProgressForPortal() {
      // no-args constructor
    }
    public R01MSyndicationProgressForPortal(final Date startedAt,
                                            final long totalItemsToBeProcessed,
                                            final long currentlyProcessedItems) {
          super(startedAt,
                totalItemsToBeProcessed,
                currentlyProcessedItems);
   }
}
```

## Por cada Excel ##

Por cada Excel que se quiera crear se tendrá que crear una clase en el CORE con los siguientes requisitos:
- El nombre debe ser R01TExportAsExcelItemFlowProcessosFor{FICHERO}  en el caso del ejemplo de esta guia seria **R01TExportAsExcelItemFlowProcessosForPortals**
- Debe implementar en el método **_processItem** la generación de una fila del Excel correspondiente al OID recibido como parámetro **item**.
- Importante el método _processItem debe ser **synchronized** sino los datos salen desalineados.,
- Este método _processItem solo recibe el OID de lo que se quiere generar de tal forma que los datos se tienen que cargar de la base de datos. Para ello en el constructor de la clase se tiene que recibir los servicios necesarios para obtener los datos. En el ejemplo se pasan el servicio CRUD para páginas de un portal **R01MCRUDServicesForPortalPage pageCRUDServices**
. 
- Es obligatorio recibir en el constructor el **ExecutorService** para la gestión de la concurrencia
- Es obligatorio recibir en el constructor el **OutputStream** donde se va a ir escribiendo el Excel
- Es obligatorio recibir en el constructor el **Collection<String>** con los literales de las columnas del Excel

Se muestra la clase básica:

```java
package r01t.core.services.portal.syndication.exportto.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;

import r01f.core.batch.exportimport.excel.ExportAsExcelItemFlowProcessorBase;
import r01f.locale.Language;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.Dates;
import r01mb.api.interfaces.portal.R01MCRUDServicesForPortalPage;
import r01mb.model.portal.R01MPortalPage;
import r01mb.model.portal.syndication.R01MSyndicationProgressForPortal;
import r01mb.model.cms.oids.R01MPortalOIDs.R01MPortalPageOID;
import r01mb.model.facets.delegates.R01MFriendlyUrlInLanguage;

public class R01TExportAsExcelItemFlowProcessosForPortalPages 
     extends ExportAsExcelItemFlowProcessorBase<R01MPortalPageOID,
                                                R01MSyndicationProgressForPortal> {

/////////////////////////////////////////////////////////////////////////////////////////
// SERVICES
/////////////////////////////////////////////////////////////////////////////////////////

    private final R01MCRUDServicesForPortalPage _pageCRUDServices;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    private final Language _lang;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

    public R01TExportAsExcelItemFlowProcessosForPortalPages(final R01MCRUDServicesForPortalPage pageCRUDServices,
                                                            final Language fileLanguage,
                                                            final ExecutorService executorService,
                                                            final OutputStream outputStream,
                                                            final Collection<String> columnHeaders) {
        super(executorService,
              outputStream,
        // the progress factory
        (startedAt,totalItemsToBeProcessed,currentlyProcessedItems,item) -> new R01MSyndicationProgressForPortal(startedAt,
                                                                                                                 totalItemsToBeProcessed,
                                                                                                                 currentlyProcessedItems),
                                                                                                                 columnHeaders);
        _lang = fileLanguage;
        _pageCRUDServices = pageCRUDServices;
    }

    @Override
    protected synchronized void _processItem(SecurityContext securityContext, R01MPortalPageOID item) throws IOException {
        R01MPortalPage portalPage = _pageCRUDServices.load(securityContext, item).getOrNull();
        
        Row xlsRow = _createXlsRow();

        // Codigo
        _createXlsCell(xlsRow, portalPage.getId().asString());
        // Nombre
        _createXlsCell(xlsRow, portalPage.getDescription());
        // Subpagina
        _createXlsCell(xlsRow, portalPage.isSubPage());
        // Nivel de navegacion
        _createXlsCell(xlsRow, portalPage.getLevel());
        // Estado de publicacion
        _createXlsCell(xlsRow,"Por hacer");
        // Fecha de publicación
        _createXlsCell(xlsRow,"Por hacer");
        // Ultima actualizacion
        _createXlsDateCell(xlsRow, portalPage.getLastUpdateDate());
        // URLs amigables
        _createXlsCell(xlsRow, _aliases(portalPage));

    }

    private void _createXlsDateCell(final Row xlsRow, final Date value) {
        CellStyle dateCellStyle = _xlsWorkbook.createCellStyle();       
        CreationHelper createHelper = _xlsWorkbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat()
                                                .getFormat(_lang.is(Language.SPANISH)
                                                                ? Dates.ES_DEFAULT_FORMAT
                                                                : Dates.EU_DEFAULT_FORMAT));

        _createXlsCell(xlsRow, value).setCellStyle(dateCellStyle);
    }

    private String _aliases( R01MPortalPage portalPage) {
        Collection<R01MFriendlyUrlInLanguage> urlAliases = portalPage.getUrlAliases();
        return urlAliases.stream().map(url -> url.getUrlPath().asString()).collect(Collectors.joining(", "));
    }

}
```
Como se puede ver en el código lo primero que se hace es leer los datos en base al OID y luego se crea la fila y se van creando las celdas necesarias, en el ejemplo son 8 celdas, para el código, nombre, subpagina, nivel de navegación, estado de publicación, fecha de publicación, última actualización y URL amigables. 

Lógicamente esta parte es completamente dependiente del excel que se quiera generar.

## Servicio para la exportación ##

Se necesita un servicio que aglutine todas las exportaciones del proyecto, esto se realiza mediante una serie de clases e interfaces, tanto en el API como en el CORE.

Dependiendo del número de ficheros Excel que se necesiten generar igual conviene tener mas de un servicio, en esta guia vamos a fijar el mínimo necesario.

### Interface del Servicio ###

Siguiendo la estructura de capas de PlateaWeb se necesita una **interface de Servicio** que contiene todos los métodos para realizar con todas las exportaciones a Excel del proyecto ubicada en el API con los siguientes requisitos:

- El nombre será R01MExportServicesFor{PROYECTO} en el caso de esta guia seria **R01MExportServicesForPortal**. 
- Tiene que extender de la Interface marcador R01MReportingServiceInterfaceFor{PROYCTO}, en el caso de esta guia seria **R01MReportingServiceInterfaceForPortal**
- La interface debe utilizar la anotación **@ExposedServiceInterface**
- Esta interface deberá contener un método por cada uno de los Excel que se quieran exportar. 
- Por convención el nombre del método debe ser export{FICHERO} en el ejemplo de esta guia **exportPortalPages**
- El tipo de retorno debe ser siempre **COREServiceMethodExecResult<Boolean>**

Se muestra un ejemplo de una interface:

```java
package r01mb.api.interfaces.portal.export;

import java.io.OutputStream;
import java.util.List;

import r01f.locale.I18NBundleAccess;
import r01f.locale.Language;
import r01f.model.services.COREServiceMethodExecResult;
import r01f.patterns.ProgressSubscriber;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ExposedServiceInterface;
import r01mb.api.interfaces.R01MReportingServiceInterfaceForPortal;
import r01mb.model.cms.oids.R01MPortalOIDs.R01MPortalOID;
import r01mb.model.cms.oids.R01MPortalOIDs.R01MPortalPageOID;
import r01mb.model.cms.oids.R01MURLOIDs.R01MUrlAliasOID;
import r01mb.model.portal.syndication.R01MSyndicationProgressForPortal;

@ExposedServiceInterface
public interface R01MExportServicesForPortal 
         extends R01MReportingServiceInterfaceForPortal {

    public COREServiceMethodExecResult<Boolean> exportPortalPages(final List<R01MPortalPageOID> portalOID,
                                                                  final SecurityContext securityContext,
                                                                  final Language fileLanguage,
                                                                  final I18NBundleAccess i18n,
                                                                  final OutputStream os,
                                                                  final ProgressSubscriber<R01MSyndicationProgressForPortal> exportProgressSubscriber);

}
```

### Interface Marcador del API ###

Siguiendo la estructura de capas de PlateaWeb se necesita una interface marcador en el API para que el injección de dependencias sea capaz de encontrar esa interface. 

Se muestra un ejemplo de una interface marcador para el proyecto `Portal`:

```java
package r01mb.api.interfaces;

import r01mb.api.interfaces.portal.R01MPortalServiceInterface;

public interface R01MReportingServiceInterfaceForPortal 
         extends R01MPortalServiceInterface {
    // just a marker interface
}
```

### Client API Delegate  ###

Siguiendo la estructura de capas de PlateaWeb se necesita un Client API Delegate como punto de entrada a los servicios de exportación a Excel. Este Client API Delegate tiene que seguir las convenciones de los Client API Delegate o en el caso de que en el proyecto se utilicen ClientSubApi esas convenciones, que aqui no vamos a detallar porque no es algo específico de la exportación a Excel.

Se muestra un ejemplo:

```java
package r01mb.api;

import java.io.OutputStream;
import java.util.List;

import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.I18NBundleAccess;
import r01f.locale.Language;
import r01f.model.services.COREServiceMethodExecResult;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.ProgressSubscriber;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIServiceDelegateBase;
import r01mb.api.interfaces.portal.export.R01MExportServicesForPortal;
import r01mb.model.cms.oids.R01MPortalOIDs.R01MPortalOID;
import r01mb.model.cms.oids.R01MPortalOIDs.R01MPortalPageOID;
import r01mb.model.cms.oids.R01MURLOIDs.R01MUrlAliasOID;
import r01mb.model.portal.syndication.R01MSyndicationProgressForPortal;

@Slf4j
public class R01MClientAPIDelegateForPortalExportServices
     extends ClientAPIServiceDelegateBase<R01MExportServicesForPortal> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public R01MClientAPIDelegateForPortalExportServices(final Provider<SecurityContext> securityContextProvider,
                                                        final Marshaller modelObjectsMarshaller,
                                                        final R01MExportServicesForPortal persistenceServiceProxy) {
        super(securityContextProvider,
              modelObjectsMarshaller,
              persistenceServiceProxy);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

    public boolean exportPortalPages(final List<R01MPortalPageOID> portalPagesOIDs,
                                     final Language fileLanguage,
                                     final I18NBundleAccess i18n,
                                     final OutputStream os,
                                     final ProgressSubscriber<R01MSyndicationProgressForPortal> exportProgressSubscriber) {
        COREServiceMethodExecResult<Boolean> exportPortalPages = this.getServiceProxyAs(R01MExportServicesForPortal.class)
                                                                     .exportPortalPages(portalPagesOIDs,
                                                                                        getSecurityContext(), 
                                                                                        fileLanguage, 
                                                                                        i18n, 
                                                                                        os, 
                                                                                        exportProgressSubscriber);

        return exportPortalPages.getOrThrow();
    }

}
```


### Service Delegate ###

Siguiendo la estructura de capas de PlateaWeb se necesita un Service Delegate en el CORE que como todos los delegate se encargue validar los datos y orqueste la generación en este caso de los Excel.

Este Service Delegate tiene que cumplir los siguiente requisitos:

- Debe estar en el paquete **r01t.core.services.delegates.importexport**
- Se debe llamar R01TExportServicesDelegateFor{PROYECTO} en el caso de esta guia **R01TExportServicesDelegateForPortal**
- Debe implementar el Interface del servicio, en este caso **R01MExportServicesForPortal** es decir esta clase es la que implementa el servicio definido en el API, asi que debe implementar ese API.
- En el constructor se deberán recibir todos los servicios necesarios para realizar la construcción del Excel.

Se muestra un ejemplo de un Delegate para el proyecto de portales:

```java
package r01t.core.services.delegates.importexport;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.google.common.eventbus.EventBus;

import io.reactivex.rxjava3.core.Flowable;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.locale.I18NBundleAccess;
import r01f.locale.Language;
import r01f.model.services.COREServiceMethod;
import r01f.model.services.COREServiceMethodExecOK;
import r01f.model.services.COREServiceMethodExecResult;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.ProgressSubscriber;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.ServicesDelegateBase;
import r01f.util.types.collections.Lists;
import r01mb.api.interfaces.portal.R01MCRUDServicesForPortal;
import r01mb.api.interfaces.portal.R01MCRUDServicesForPortalPage;
import r01mb.api.interfaces.portal.R01MCountServicesForPortalPage;
import r01mb.api.interfaces.portal.export.R01MExportServicesForPortal;
import r01mb.api.interfaces.url.R01MServicesForUrlAlias.R01MCRUDServicesForUrlAlias;
import r01mb.model.cms.oids.R01MPortalOIDs.R01MPortalOID;
import r01mb.model.cms.oids.R01MPortalOIDs.R01MPortalPageOID;
import r01mb.model.cms.oids.R01MURLOIDs.R01MUrlAliasOID;
import r01mb.model.portal.syndication.R01MSyndicationProgressForPortal;
import r01t.core.services.portal.syndication.exportto.excel.R01TExportAsExcelItemFlowProcessosForAlias;
import r01t.core.services.portal.syndication.exportto.excel.R01TExportAsExcelItemFlowProcessosForPortalPages;
import r01t.core.services.portal.syndication.exportto.excel.R01TExportAsExcelItemFlowProcessosForPortals;

public class R01TExportServicesDelegateForPortal
     extends ServicesDelegateBase
  implements R01MExportServicesForPortal {

/////////////////////////////////////////////////////////////////////////////////////////
//  SERVICES
/////////////////////////////////////////////////////////////////////////////////////////

    private final R01MCRUDServicesForPortal _portalCRUDServices;
    private final R01MCRUDServicesForPortalPage _pageCRUDServices;
    private final R01MCountServicesForPortalPage _pageCountServices;
    private final R01MCRUDServicesForUrlAlias _aliasCRUDServices;
    private final ExecutorService _executorService;    

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

    public R01TExportServicesDelegateForPortal(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
                                               final Marshaller marshaller,                                                  
                                               final EventBus eventBus,
                                               final R01MCRUDServicesForPortal portalCRUDServices,
                                               final R01MCRUDServicesForPortalPage pageCRUDServices,
                                               final R01MCountServicesForPortalPage pageCountServices,
                                               final R01MCRUDServicesForUrlAlias aliasCRUDServices,
                                               final ExecutorService executorService) {
        super(coreCfg,
              eventBus);
        _portalCRUDServices = portalCRUDServices;
        _pageCRUDServices = pageCRUDServices;
        _pageCountServices= pageCountServices;
        _aliasCRUDServices = aliasCRUDServices;
        _executorService = executorService;
    }

    @Override
    public COREServiceMethodExecResult<Boolean> exportPortalPages(final List<R01MPortalPageOID> portalPageOID,
                                                                  final SecurityContext securityContext,
                                                                  final Language fileLanguage, 
                                                                  final I18NBundleAccess i18n, 
                                                                  final OutputStream outputStream,
                                                                  final ProgressSubscriber<R01MSyndicationProgressForPortal> exportProgressSubscriber) {

        Collection<String> columnHeaders = Lists.newArrayList(i18n.getMessage("portal.export.excel.portals.code"),
                                                              i18n.getMessage("portal.export.excel.portals.name"),
                                                              i18n.getMessage("portal.export.excel.portals.subpage"),
                                                              i18n.getMessage("portal.export.excel.portals.level"),
                                                              i18n.getMessage("portal.export.excel.portals.publishState"),
                                                              i18n.getMessage("portal.export.excel.portals.published"),
                                                              i18n.getMessage("portal.export.excel.portals.lastUpdated"),
                                                              i18n.getMessage("portal.export.excel.portals.alias"));

        int count = portalPageOID.size();

        R01TExportAsExcelItemFlowProcessosForPortalPages exp = new R01TExportAsExcelItemFlowProcessosForPortalPages(_pageCRUDServices,
                                                                                                                    fileLanguage,
                                                                                                                    _executorService,
                                                                                                                    outputStream,
                                                                                                                    columnHeaders);
        exp.process(securityContext,
                    count,
                    Flowable.fromIterable(portalPageOID),
                    exportProgressSubscriber);        
        return new COREServiceMethodExecOK<>(COREServiceMethod.named("exportPortalPages"),true);
    }
```

El esquema de los métodos de este Delegate es siempre el mismo.

- Se carga la lista de los literales de la cabecera de cada columna del Excel.
- Se recibe un OID indicativo de lo que se quiera exportar. En este caso se recibe una lista de OID porque se van a exportar muy pocos datos, normalmente decenas de paginas y ya se tienen los OID en el grid que los muestra y por eficiencia se pasan directamente, si fuesen muchos datos o no se tuviesen ya calculados se tendria que pasar el OID del portal y los criterios para realizar el filtrado de las paginas que se quieren exportar.
- Se tiene que saber cuantos elementos hay en ese Excel
- Se crea el Itemflow pasandole los parámetros.
- Se ejecuta el ItemFlow
- Se devuelve que la ejecución es correcta, ya que se realiza de forma asincrona. Hay que prestar expecial cuidado a que el nombre COREServiceMethod.named("exportPortalPages") coincida con el nombre del método para evitar confusiones en caso de error.

### Interface Marcador del CORE ###

Siguiendo la estructura de capas de PlateaWeb se necesita una interface marcador en el CORE para que el injección de dependencias sea capaz de encontrar la clase que implementa el servicio. 

Se muestra un ejemplo de una interface marcador para el proyecto `Portal`:

```java
package r01t.core.services.delegates.importexport;

import r01t.core.services.R01TCoreServiceImpl;

public interface R01TPortalCOREExportServiceImpl
         extends R01TCoreServiceImpl {
    // just a marker interface
}
```

### Service Impl ###

Siguiendo la estructura de capas de PlateaWeb se necesita una implementación del servicio, no se va a explicar aqui ya que es estandard de PlateaWeb.

Este Service Impl tiene que cumplir los siguiente requisitos:

- Debe estar en el paquete **r01t.core.services.importexport**
- La clase se debe llamar R01TCoreExportServicesFor{PROYECTO} en el ejemplo de esta guia **R01TCoreExportServicesForPortal**
- Debe extender de **CoreServicesBase**
- Debe implementar dos interfaces para que le injección de dependencias funcione:
    - La interface publica del API del servicio **R01MExportServicesForPortal**
    - La interface privada del CORE del servicio **R01TPortalCOREExportServiceImpl**

```java
package r01t.core.services.importexport;

import java.io.OutputStream;
import java.util.List;

import com.google.common.eventbus.EventBus;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.concurrent.ExecutorServiceManager;
import r01f.locale.I18NBundleAccess;
import r01f.locale.Language;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.services.COREServiceMethodExecResult;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.ProgressSubscriber;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.CoreServicesBase;
import r01f.services.persistence.ServiceDelegateProvider;
import r01mb.api.interfaces.portal.R01MCRUDServicesForPortal;
import r01mb.api.interfaces.portal.R01MCRUDServicesForPortalPage;
import r01mb.api.interfaces.portal.R01MCountServicesForPortalPage;
import r01mb.api.interfaces.portal.export.R01MExportServicesForPortal;
import r01mb.api.interfaces.url.R01MServicesForUrlAlias.R01MCRUDServicesForUrlAlias;
import r01mb.model.cms.oids.R01MPortalOIDs.R01MPortalOID;
import r01mb.model.cms.oids.R01MPortalOIDs.R01MPortalPageOID;
import r01mb.model.cms.oids.R01MURLOIDs.R01MUrlAliasOID;
import r01mb.model.portal.syndication.R01MSyndicationProgressForPortal;
import r01t.core.services.delegates.importexport.R01TExportServicesDelegateForPortal;
import r01t.core.services.delegates.importexport.R01TPortalCOREExportServiceImpl;

@Singleton
@Accessors(prefix="_")
public class R01TCoreExportServicesForPortal
     extends CoreServicesBase
  implements R01MExportServicesForPortal,
             R01TPortalCOREExportServiceImpl {
    
/////////////////////////////////////////////////////////////////////////////////////////
//  DELEGATE PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter private final ServiceDelegateProvider<R01MExportServicesForPortal> _delegateProvider;
    
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    @Inject
    public R01TCoreExportServicesForPortal(                        final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
                                           @ModelObjectsMarshaller final Marshaller modelObjectsMarshaller,
                                                                   final EventBus eventBus,
                                                                   final R01MCRUDServicesForPortal portalCRUDServices,
                                                                   final R01MCRUDServicesForPortalPage pageCRUDServices,
                                                                   final R01MCountServicesForPortalPage pageCountServices,
                                                                   final R01MCRUDServicesForUrlAlias aliasCRUDServices,
                                                                   final ExecutorServiceManager executorServiceManager) {
        super(coreCfg,
              modelObjectsMarshaller,
              eventBus);

        // delegate provider
        _delegateProvider = securityContext -> new R01TExportServicesDelegateForPortal(coreCfg,modelObjectsMarshaller,eventBus,
                                                                                       portalCRUDServices,
                                                                                       pageCRUDServices,
                                                                                       pageCountServices,
                                                                                       aliasCRUDServices,
                                                                                       executorServiceManager.getExecutorService());
        
   
    }

/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public COREServiceMethodExecResult<Boolean> exportPortalPages(final List<R01MPortalPageOID> portalOID,
                                                                  final SecurityContext securityContext, 
                                                                  final Language fileLanguage, 
                                                                  final I18NBundleAccess i18n, 
                                                                  final OutputStream os,
                                                                  final ProgressSubscriber<R01MSyndicationProgressForPortal> exportProgressSubscriber) {
        return this.forSecurityContext(securityContext)
                   .createDelegateAs(R01MExportServicesForPortal.class)
                   .exportPortalPages(portalOID,
                                      securityContext,
                                      fileLanguage,
                                      i18n,
                                      os,
                                      exportProgressSubscriber);
    }

}
```

Como se puede ver en el código el constructor debe recibir los servicios necesarios y tambien el ExecutorServiceManager para manejr los hilos.

En el constructor se debe crear el delegado y guardarlo.

Luego por cada metodo se tiene que crear el delegado para el contexto de seguridad

## Consideraciones sobre los hilos ##

La generación de Excel utiliza un mecanismo de multiples hilos para mejorar el rendimiento, el número de hilos se controla en la clase de la UI R01UIWarBootstrapGuiceModuleFor{PROYECTO}ManagerWar, en el caso de esta guia es **R01UIWarBootstrapGuiceModuleForPortalManagerWar**

En el método configure suele haber un código similar a este 

```java
        // render executor service
        String bindingName = "portal.ui.render-pool";
        binder.bind(ExecutorServiceManager.class)
              .annotatedWith(Names.named(bindingName))
              .toProvider(R01UIRenderExecutorServiceManagerProvider.class)
              .in(Singleton.class);

        // Expose the ServiceHandler to stop the exec manager threads
        // do NO forget!!
        ServicesBootstrapGuiceUtil.bindServiceHandler(binder,
                                                      ExecutorServiceManager.class,bindingName);

        log.warn("END____________ R01 UI Portal Manager Bootstraping _____________________________");
    }

/////////////////////////////////////////////////////////////////////////////////////////
//  RENDER EXECUTOR SERVICE
/////////////////////////////////////////////////////////////////////////////////////////
    @Singleton
    public static class R01UIRenderExecutorServiceManagerProvider
                extends ExecutorServiceManagerProvider {
        @Inject
        public R01UIRenderExecutorServiceManagerProvider(final R01UIPortalManagerConfig renderConfig) {
            super(renderConfig.getRenderThreadPoolSize());
        }
    }
```

En el se hace el bind de guice entre el **ExecutorServiceManager** y la implementación **R01UIRenderExecutorServiceManagerProvider** en la cual se le especifica el número de hilos, en esta caso cogiendolo del R01UIPortalManagerConfig

Si no se expecidica este bind se realiza uno por defecto en la configuración del EventBus.

### Core Mediator ###

Siguiendo la estructura de capas de PlateaWeb se necesita un CoreMediator.

Se muestra un ejemplo:

```java
package r01ui.portal.manager.vaadin.reporting;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.I18NBundleAccess;
import r01f.locale.Language;
import r01f.patterns.ProgressSubscriber;
import r01f.ui.coremediator.UICOREMediatorBase;
import r01mb.api.portal.R01MClientAPIForPortal;
import r01mb.model.cms.oids.R01MPortalOIDs.R01MPortalOID;
import r01mb.model.cms.oids.R01MPortalOIDs.R01MPortalPageOID;
import r01mb.model.cms.oids.R01MURLOIDs.R01MUrlAliasOID;
import r01mb.model.portal.syndication.R01MSyndicationProgressForPortal;

@Singleton
@Slf4j
public class R01UIPortalExcelExportCOREMediator 
     extends UICOREMediatorBase<R01MClientAPIForPortal> {

/////////////////////////////////////////////////////////////////////////////////////////
// BUFFER FOR EXPORT
/////////////////////////////////////////////////////////////////////////////////////////
   public static final int PIPE_BUFFER = 1 * 1024 * 1024;  // 1 Mb     

/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    @Inject
    public R01UIPortalExcelExportCOREMediator(final R01MClientAPIForPortal api) {
        super(api);
    }

/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////

    public InputStream exportPortalPages(final List<R01MPortalPageOID> oid,
            final I18NBundleAccess i18n,
            final Language fileLanguage,
            final ProgressSubscriber<R01MSyndicationProgressForPortal> exportProgressSubscriber) throws IOException {
        // The export api needs an OutputStream where the export file will be WRITTEN
        // ... but the UI needs an InputStream from where to READ and return to the client
        // data is written to the outputstream and readed at the inputstream        
        try (PipedOutputStream out = new PipedOutputStream()) {
            PipedInputStream in = new PipedInputStream(out, PIPE_BUFFER);   // BEWARE! the buffer is VERY IMPORTANT when generating EXCEL files
            
            this.getApi()
                .getForPortalExportServices()
                .exportPortalPages(oid, 
                                   fileLanguage, 
                                   i18n, 
                                   out, 
                                   exportProgressSubscriber);
            return in;
        }
    }
}
```

Lo mas reseñable sobre otros CoreMediator estandard de la arquitectura de PlateaWeb es que se tiene que crear los OutputStream y los ImputStream con un tamaño, por defecto será de un mega, pero si el tamaño habitual de tus ficheros Excel es diferente tanto por defecto como por exceso convendria que lo ajustases.