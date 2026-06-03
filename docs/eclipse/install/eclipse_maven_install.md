# Eclipse install
=======================================

The following instructions creates an eclipse MASTER workspace that will be used as a TEMPLATE to
create any eclipse workspaces for any project

The idea is:

1 - Install eclipse and create a MASTER workspace (_a TEMPLATE workspace_)

2.- To create an eclipse workspace for a given project, just *copy* the MASTER (_or TEMPLATE_) eclipse workspace

## [1]: Create the file system structure
```
/{dev_home} = d:\develop in windows or /develop in linux
  + develop
    + ide
        + eclipse
            + .eclipse <-- will contain eclipse plugin config that usually is located at {user-home}/.eclipse
            + {eclipse-version}
            + ...
        + eclipse_workspaces
            + master-{eclipse-version}
            + ...
    + java
        + maven_libs
        + local_libs
        + jdk
            + {jdk-version}
    + projects
    + servers
    + tools
        + DBeaver
    + DB
      + ddl_scripts
```

## [2]: Install Eclipse

a) Download the [eclipse IDE for Java Developers] from https://www.eclipse.org/downloads/packages/

> BEWARE!! do NOT download the [eclipse IDE for Java EE Developers] just "plain" [eclipse IDE]

b) Extract the contents of the [eclipse] folder inside the previously downloaded eclipse ZIP to the `/{dev_home}/eclipse/{eclipse-version}`

c) Copy the `/{dev_home}/eclipse/{eclipse-version}/eclipse.ini` to eclipse.ini.original

d) Edit the `/{dev_home}/eclipse/{eclipse-version}/eclipse.ini` file and set this content:

> BEWARE!!!

* check that the `org.eclipse.equinox.launcher` versions still the SAME as those in the eclipse.ini.original file

* replace {eclipse-version} with it's real value

> Download lombok.jar from this site https://projectlombok.org/download, and copy to eclipse instance root dir.

```
  -clean
  -refresh
  -startup
  plugins/org.eclipse.equinox.launcher_1.6.800.v20240513-1750.jar
  --launcher.library
  plugins/org.eclipse.equinox.launcher.win32.win32.x86_64_1.2.1000.v20240507-1834
  -product
  org.eclipse.epp.package.java.product
  -showsplash
  org.eclipse.epp.package.common
  --launcher.defaultAction
  openFile
  --launcher.defaultAction
  openFile
  --launcher.appendVmargs
  -vm
  plugins/org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_21.0.3.v20240426-1530/jre/bin
  -vmargs
  -server
  # disable code verification
  -Xverify:none
  --illegal-access=warn
  # by default eclipse stores some plugin config at {USER_HOME}/.eclipse (ie c:/users/{user}/.eclipse in windows)
  # this vm argument CHANGES the location of these eclipse config
  -Duser.home=/{dev_home}/eclipse/.eclipse-{eclipse-version} <<<<<< replace with eclipse version!!!
  # see [Runtime Options] http://help.eclipse.org/mars/topic/org.eclipse.platform.doc.isv/reference/misc/index.html
  # see http://stackoverflow.com/questions/316265/how-can-you-speed-up-eclipse/316535#316535
  # 	-XX:ReservedCodeCacheSize=29m, don't to have dead compiled code in memory for long
  # 	-XX:MinHeapFreeRatio=1 -XX:MaxHeapFreeRatio=2  early GC > since G1GC is parallel,the earlier the better
  #	-javaagent:system-gc.jar > a java agent that calls System.gc() after every minute
  -Duser.language=en
  -Duser.country=US
  -Dhelp.lucene.tokenizer=standard
  -javaagent:lombok.jar
  # -Xbootclasspath/a:lombok.jar  << NOT supported since java9
  -Dorg.eclipse.ecf.provider.filetransfer.excludeContributors=org.eclipse.ecf.provider.filetransfer.httpclientjava
  -Dosgi.requiredJavaVersion=17
  -Dosgi.instance.area.default=@user.home/eclipse-workspace
  -Dosgi.dataAreaRequiresExplicitInit=true
  -Dorg.eclipse.swt.graphics.Resource.reportNonDisposed=true
  -Declipse.e4.inject.javax.warning=false
  -Dsun.java.command=Eclipse
  -Xms256m
  -Xmx2048m
  -XX:+UseG1GC
  -XX:+UseStringDeduplication
  --add-modules=ALL-SYSTEM
  -Djava.security.manager=allow
```

NOTE: latest versions of eclipse have an embeded JRE so this is **NOT necessary** (see eclipse.ini)

```
  # JDK 1.8 <<<<<<<< USE JDK8 if runninig OEPE (Oracle Enterprise Pack)
  -vm
  d:/java/jdk8/jre/bin/server/jvm.dll
  -vmargs
```

**BEWARE If eclipse tools need to access any EJIE server (for example when downloading maven artifacts), the IZENPE certs are needed**

Import the IZENPE CERTS at the jdk secure store used to start eclipse (seee -vm param above)

a) open the `eclipse.ini` file and locate the JRE folder, something like:

```
  -vm
  plugins/org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_21.0.3.v20240426-1530/jre/bin
```

b) open a system shell (cmd) and cd to the previous jre path:

```
  cd {develop_home}/eclipse/{eclipse-version}/plugins/{jre-version}/jre/bin
```

then install the certs (replace `{develop_home}` with the correct path:

```
.\keytool -keystore ..\lib\security\cacerts -import -file {develop_home}\projects\fabric\r01f\docs\eclipse\maven\certs\izenpe.com.cer -alias izenpe_root -storepass changeit
.\keytool -keystore ..\lib\security\cacerts -import -file {develop_home}\projects\fabric\r01f\docs\eclipse\maven\certs\CAAAPPVascas.cer -alias CAAAPPVascas -storepass changeit
.\keytool -keystore ..\lib\security\cacerts -import -file {develop_home}\projects\fabric\r01f\docs\eclipse\maven\certs\builds.alm02.itbatera.euskadi.eus.crt -alias builds.alm02.itbatera.euskadi.eus -storepass changeit
```



## [3]: Launch Eclipse

Launch eclipse.
When asked for the `[workspace]` location select: `/{dev_home}/eclipse_workspaces/master_{eclipse-version}` (do **NOT** set use this workspace as default: don't ask again)
> **BEWARE** this workspace location will later act as a _template_ pre-configured workspace that will be copied when creating a **new** workspace


## [4]: Install plugins

a) **AJDT: AspectJ Development Tools**   
> Eclipse 2026-03: https://download.eclipse.org/tools/aspectj/ajdt/439/dev/update/  
>
> ** IMPORTANT!!! **  
> Latest versions of [eclipse] comes with an embedded jdk 21 and at this time [AJDT] version at the "official update site" **does NOT > work properly**  
see `https://github.com/eclipse/org.aspectj/blob/master/docs/developer/IDE.md` for further explanations  
>   
>
> In previous versions of [eclipse] use `https://download.eclipse.org/tools/ajdt/423/dev/update` instead of `http://download.eclipse.org/tools/ajdt/410/dev/update`


b) **Eclipse GIT plugins** (from eclipse update site https://download.eclipse.org/egit/updates) (install Git Client https://git-scm.com/download/gui/windows)

```
      Collaboration
          [X] Git Integration for eclipse
          [X] Git Integration for eclipse - GitFlow Support
          [X] Java implementation of Git
          [X] Java implementation of Git - GPG suport using Bouncy Castle
          [X] Java implementation of Git - Optional Http support using Apache httpclient
          [X] Java implementation of Git - Optional LFS support
          [X] Java implementation of Git - ssh support using Apache MINA sshd
          [X] Java implementation of Git - ssh support using JCraft jsch

      General Purpose Tools
          [X] M2E - Maven Integration for Eclipse
          [X] M2E - PDE Integration
          [X] M2E - POM Editor using LemMinX language server
          [X] M2E - SLF4J over Logback Logging

      Programming Languages
          [X] Bytecode Outline View
          [X] Eclipse Java Development Tools
          [X] Eclipse XML Editor and Tools
          [X] Java AST View
          [X] Java Element View
          [X] Javadoc Help Feature
          [X] JavaScript Development Tools
          [X] JavaScript Development Tools Chromium/V8 Remote Debugger
          [X] Wild Web Developer HTML, CSS, JSON, Yaml, JavaScript, TypeScript, Node tools

      Web, XML, Java EE and OSGi Enterprise Development
          [X] Eclipse Faceted Project Framework
          [X] Eclipse Faceted Project Framework JDT Enablement
          [X] Eclipse Java EE developer tools
          [X] Eclipse Java Web Developer Tools
          [X] Eclipse Java Web Developer Tools - JavaScript Support
          [X] Eclipse Web Developer Tools
          [X] Eclipse XML Editor and Tools
          [X] Eclipse XSL Developer Tools
          [X] JavaScript Development Tools
          [X] JavaScript Development Tools Chromium/V8 Remote Debugger
          [X] JST Server Adapters
          [X] JST Server Adapters Extensions (Apache Tomcat)
          [X] JST Server UI
          [X] m2e-wtp - JAX-RS configurator for WTP.
          [X] m2e-wtp - JPA configurator for WTP.
          [X] m2e-wtp - Maven Integration for WTP.
          [x] Wild Web Developer
          [X] Wild Web Developer HTML, CSS, JSON, Yaml, JavaScript, TypeScript, Node
          [X] WST Server Adapters
```


c) **[Other Plugins]**  

  **[AnyEdit Tools]** > use [eclipse marketplace](https://marketplace.eclipse.org/content/anyedit-tools) or update site at: [Andrei Loskutov gmxhome site](http://andrei.gmxhome.de/eclipse/) 

    [X] Eclipse 4.10-4.14 plugins
      [X] AnyEditTools

  **[IndentGuide]** from update site https://www.certiv.net/updates (install ONLY indent guide!)
  
  **[Github Copilot]** use [eclipse marketplace]
  
Configure [Github Copilot] to complete when pressing [ctrl+tab] keys instead of just [tab] 

> Window > Preferences > General > Keys > (filter for 'Copilot') > change [Accept Suggestion] binding for [CTRL+TAB]


## [5]: Clone R01F Git repository

(do NOT import projects in eclipse)

## [6]: Configure plugins

a) **General**
> `[General] > [Startup & Shutdown] > Workspaces`: [x] Prompt for workspace on startup

b) **[Maven]**

> `[Maven > Download artifact sources]` : true

> `[Maven > Hide folders of physically nested modules`: true  > this avoids duplicate search results of resources within nested projects

> `[Maven > Archetypes]` > Add a NEW remote catalog at: http://repo1.maven.org/maven2/archetype-catalog.xml

> `[Maven > User Settings] > Global Settings`: {dev_home}/projects/fabric-r01f/docs/eclipse/maven/settings_{env}.xml

> `[Maven > Error/Warnings] > Plugin execution not covered by lifecycle configuration`: change to WARNING!

By default, EGit automatically adds resources marked as "Derived" to .gitignore
... if `[Maven > Hide folders of physically nested modules` is **enabled** (true), eclipse adds nested modules' folders to .gitignore
This behavior can be disabled at `[Version Control (Team)] > [Git] > [Projects]` and **deselect** _"Automatically ignore derived resources by adding them to .gitignore"_

*BEWARE*:
* Ensure that `[Maven > User Settings][Local Repository] (from merged user and global settings)` is `{dev_home}/maven-libs`

* Some artifacts are NOT published at MAVEN CENTRAL; this is the case of javax.ejb / javax.servlet-api or javax.jms. The only workarround is to put all those artifacts at `{dev_home}/maven_libs/` manually (See fabric-r01f\docs\maven_local_libs\read.me file)

* if using weblogic, create a weblogic fullclient jar to be used as external dependency:

```
  > cd \app-server\wls_10.3.6\wlserver\server\lib
  > java -jar d:\app-server\wls_10.3.6\modules\com.bea.core.jarbuilder_1.7.0.0.jar
```

c) **[Java]**

Java 8
> Create a new [JRE]: `[Java] > [Installed JREs] > [Add]` pointing to a JDK8 at  `{dev_home}/java/jdk8` **Make this JRE the DEFAULT one**

> Create another [JRE] pointing to a JDK8 hot-deploy *patched* JDK8 at `{dev_home}/java/jdk8-hotswap`
> see [how to install a hotswap jre](../../java/java-hotswap.md) to patch the JDK
> set the JRE default JVM arguments: `-XXaltjvm=dcevm -javaagent:c:\develop\local-libs\hotswap-agent\hotswap-agent-1.4.0.jar`

Java 17/21/25
> Create a new [JRE]: `[Java] > [Installed JREs] > [Add]` pointing to a JDKxx at  `{dev_home}/java/jdkxx` **Make this JRE the DEFAULT one**

Java 17/21/25 HOTSWAP - see [HotswapAgent](http://hotswapagent.org/) and [jdk17 install guide](http://hotswapagent.org/mydoc_quickstart-jdk17.html) or [how to install a hotswap jre](../../java/java-hotswap.md) 
> Download a [JetBrains Runtime (JBR)](https://github.com/JetBrains/JetBrainsRuntime/releases) > *Use JBR Vanilla*
> JBR is a variant of the OpenJDK with some JetBrains modifications for their IDE's
> There are different JBR variants:  
> - JBR Vanilla: the most basic version of JBR <<<<< CHOOSE THIS!!!!!   
> - JBR with JCEF (Java Chrome Embedded Framework): allows to embed a lightweight version of Chromium web browser engine within the Java apps that can enable functionalities like displaying web content using HTML, CSS or JS  
> - JBR with JCEF fastdebug, a debugging tool for JCEF  
> 
> Create another [JRE] pointing to a JDKxx hot-deploy *patched* JDKxx at `{dev_home}/java/jdkxx-hotswap`
> set the JRE default JVM arguments: `-XX:+AllowEnhancedClassRedefinition -XX:HotswapAgent=fatjar`  

Preferences

> `[Java] > [Editor] > [Templates]` add a NEW **Java** template named **_sep** with the following content

```
/////////////////////////////////////////////////////////////////////////////////////////
//	${cursor}
/////////////////////////////////////////////////////////////////////////////////////////
```

> `[Java] > [Editor] > [Typing]`

>       - Automatically insert at correct position [X] semicolons [X] braces
>       - When pasting (remove check):  [-] Adjust indentation

Code formatter: create a NEW code formatter called [EJIE-AdminDigital] (import the config from `eclipse/install/eclipse_codestyle_formatters_EJIE-AdminDigital.xml`)

> `[Java] > [Code Style] > [Formatter]` create a NEW profile based on the [eclipse built-in] and EDIT it and change the following values:  

> `Whitespace > Declarations`  
>  `> Methods` UNCHECK the values: `After comma in parameters` and `Before comma in parameters`  
>  `> Clases` UNCHECK the values: `After comma in implements` and `Before comma in implements`

d) **[AnyEdit Tools]**
> `[General] > [Editors] > [AnyEditTools]` Remove Trailing whitespace (DISABLE)

e) **Team Colaboration**
> `[Version Control (team)] > [Ignored Resources] > add new pattern [Add Pattern] named */build/* `
This excludes .class files from synchronized files.


## [7]: Create a workspace for a project

Just copy the _template_ workspace folder: `/{dev_home}/eclipse_workspaces/master_{eclipse-version}` with a new name id: `/{dev_home}/eclipse_workspaces/my_project`

... now simply launch eclipse from  `/{dev_home}/eclipse/{eclipse-version}` as usual and when asked, select the workspace folder


## [8]: BEWARE antivirus! > https://www.genuitec.com/stop-slow-eclipse-myeclipse-startups/

Exclude folder at the antivirus config:

- the ´{dev_home}´ folder
- %user_home%/.eclipse
- %user_home%/.m2e
- %user_home%/.p2


## [9]: Tomcat config:

Select the patched HOTSWAP Java runtime (see above)

Create a NEW [run time environment] named [tomcat9] using the previously created JRE (hotswap)

Create a NEW [tomcat server] using the previously created [run time environment] and set:
- UNCHECK [modules auto-reload by default]
- Open [lauch configuration] and add to the [VM arguments]:

```
-javaagent:c:/develop/local-libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true --add-opens java.base/java.lang=ALL-UNNAMED -DEJIE_PROPERTIES_PATTERN=/default/[entityCode]/[entityCode].properties.xml -DEJIE_PROPERTY_LOADER=classPathLoader
```




