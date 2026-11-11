#!/usr/bin/env python3
"""
Adds a hand-written persistable object (e.g. OG10MediaGroup) to a scaffolded component: its API
(OID/ID/Ref, search and summary models, CRUD/Find/Count/Search/Summarize service interfaces, Client API
delegates and ref solver), its CORE (entity, transformers, DAOs, searcher, summarizer, Tier 2 / Tier 1
services) and its Guice / Spring integration tests, from the SamplePersistableObj reference implementation.

    python add_persistable_object.py --system communic --component media-register --object MediaGroup
                                     [--layers api,core,tests] [--dry-run] [--force]

- The model class ({api}{Object} in ...api.model) must exist, written by hand, extending
  {api}{Component}PersistableObjectWithIDBase (objects without business ID: use the playbooks).
- No custom finders are generated (the sample's findByNameExact is left out): add them with
  references/task5-custom-finder.md.
- The component classes that list every object (Client API, OIDs/IDs/Refs, summarizers, the Summarize
  services of the other objects, bootstrap modules, persistence.xml, test base classes) are edited in
  place: the new object is inserted next to the existing ones, hand-made code is kept.

New Java files are formatted with the r01-java-formatter skill; edited files only on the changed lines.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from r01gen import (SEP, Ids, Obj, Output, add_imports, exists, find_workspace, insert_at_type_end,  # noqa: E402
                    insert_before_closing_line, method_body, read_text, regroup_imports, replace_placeholder_line,
                    remove_section_placeholder, sub_object, type_body, walk_files, words)
from scaffold_component import common_args, resolve_ids, roots  # noqa: E402

OPS = ("CRUD", "Find", "Count", "Search", "Summarize")
SAMPLE_API = "ogov-samplesys/bai-ogov-samplesys-samplecomp-api"
SAMPLE_CORE = "ogov-samplesys/bai-ogov-samplesys-samplecomp-core"

API_FILES = (["ogovSampleSysSampleCompAPIModelClasses/{j}/api/model/search/OG90SearchFilterForSamplePersistableObj.java",
              "ogovSampleSysSampleCompAPIModelClasses/{j}/api/model/search/OG90SearchResultItemForSamplePersistableObj.java",
              "ogovSampleSysSampleCompAPIModelClasses/{j}/api/model/summaries/OG90SummarizedSamplePersistableObj.java"]
             + [f"ogovSampleSysSampleCompAPIClasses/{{j}}/api/interfaces/samplepersistableobj/OG90{op}ServicesForSamplePersistableObj.java" for op in OPS]
             + [f"ogovSampleSysSampleCompAPIClasses/{{j}}/api/samplepersistableobj/OG90ClientAPIDelegateForSamplePersistableObj{op}Services.java" for op in OPS]
             + ["ogovSampleSysSampleCompAPIClasses/{j}/api/samplepersistableobj/OG90SamplePersistableObjRefSolver.java"])
CORE_FILES = ["dao/samplepersistableobj/OG91DBCRUDForSamplePersistableObj.java",
              "dao/samplepersistableobj/OG91DBFindForSamplePersistableObj.java",
              "dao/samplepersistableobj/OG91DBCountForSamplePersistableObj.java",
              "entities/samplepersistableobj/OG91DBEntityForSamplePersistableObj.java",
              "transfer/samplepersistableobj/OG91ModelObjFromDBEntityTransformerForSamplePersistableObj.java",
              "transfer/samplepersistableobj/OG91ModelObjToDBEntityTransferDelegateForSamplePersistableObj.java",
              "search/samplepersistableobj/OG91DBEntityToSearchResultItemTransformerForSamplePersistableObj.java",
              "search/samplepersistableobj/OG91DBSearcherForSamplePersistableObj.java",
              "search/samplepersistableobj/OG91DBSearchQueryForSamplePersistableObj.java",
              "services/delegates/samplepersistableobj/OG91CORECRUDServicesDelegateForSamplePersistableObj.java",
              "services/delegates/samplepersistableobj/OG91FindServicesDelegateForSamplePersistableObj.java",
              "services/delegates/samplepersistableobj/OG91CORECountServicesDelegateForSamplePersistableObj.java",
              "services/delegates/samplepersistableobj/OG91CORESearchServicesDelegateForSamplePersistableObj.java",
              "services/delegates/samplepersistableobj/OG91CORESummarizeServicesDelegateForSamplePersistableObj.java",
              "services/samplepersistableobj/OG91CORECRUDServicesForSamplePersistableObj.java",
              "services/samplepersistableobj/OG91COREFindServicesForSamplePersistableObj.java",
              "services/samplepersistableobj/OG91CORECountServicesForSamplePersistableObj.java",
              "services/samplepersistableobj/OG91CORESearchServicesForSamplePersistableObj.java",
              "services/samplepersistableobj/OG91CORESummarizeServicesForSamplePersistableObj.java",
              "summarizers/OG91SummarizerServiceImplForSamplePersistableObj.java"]


class Ctx:
    def __init__(self, ids: Ids, obj: Obj, pocs: Path, out_root: Path, out: Output, force: bool):
        self.ids, self.obj, self.pocs, self.out, self.force = ids, obj, pocs, out, force
        pp = ids.base_pkg.replace(".", "/")
        api = out_root / f"{ids.repo_prefix}-api"
        core = out_root / f"{ids.repo_prefix}-core"
        self.api_root, self.core_root = api, core
        self.model = api / f"{ids.artifact}APIModelClasses/src/main/java/{pp}/api"
        self.apic = api / f"{ids.artifact}APIClasses/src/main/java/{pp}/api"
        self.dbres = core / f"{ids.artifact}COREDBPersistenceClasses/src/main/resources/META-INF"
        self.db = core / f"{ids.artifact}COREDBPersistenceClasses/src/main/java/{pp}/core/db"
        self.guice = core / f"{ids.artifact}COREBootstrapGuiceClasses/src/main/java/{pp}/core/bootstrap/guice"
        self.spring = core / f"{ids.artifact}COREBootstrapSpringClasses/src/main/java/{pp}/core/bootstrap/spring"

    # shortcuts
    def sub(self, t: str) -> str:
        return sub_object(t, self.obj, self.ids)

    def edit(self, path: Path, fn) -> None:
        if not exists(path):
            self.out.notes.append(f"ERROR: {path} not found (is the component scaffolded?)")
            return
        self.out.write_modified(path, fn(read_text(path)))


# =========================================================================================
#  API
# =========================================================================================
def add_api(c: Ctx) -> None:
    ids, o, A = c.ids, c.obj, c.ids.api
    sample = c.pocs / SAMPLE_API
    for rel in API_FILES:
        src = sample / rel.format(j="src/main/java/ogov/samplesys/samplecomp")
        t = c.sub(read_text(src))
        name = src.name
        if name.startswith("OG90FindServicesFor"):
            t = _cut_custom_finders(t, interface=True)
            t = regroup_imports(t, remove=("r01f.model.persistence.FindResult", "r01f.model.security.context.SecurityContext"))
        elif "ServicesFor" in name and name.startswith("OG90"):
            t = regroup_imports(t)
        elif name.startswith("OG90ClientAPIDelegateForSamplePersistableObjFindServices"):
            t = _cut_custom_finders(t, interface=True)
            t = regroup_imports(t, remove=("java.util.Collection",))
        dst = c.api_root / ids.sub(c.sub(rel.format(j="src/main/java/ogov/samplesys/samplecomp")))
        c.out.write_new(dst, t, force=c.force)

    # OIDs / IDs / Refs holders
    for holder in ("OIDs", "IDs", "Refs"):
        s = read_text(sample / f"ogovSampleSysSampleCompAPIModelClasses/src/main/java/ogov/samplesys/samplecomp/api/model/oids/OG90SampleComp{holder}.java")
        head, _comment, block = re.split(r"^/{89}[ \t]*\n", s, maxsplit=2, flags=re.M)
        block = block[:block.rstrip().rindex("}")]
        section = f"{SEP}\n//\t{o.name}\n{SEP}\n" + c.sub(block).rstrip("\n") + "\n"
        imps = [l[7:-1] for l in c.sub(head).split("\n") if l.startswith("import ")]
        cls = f"{A}{ids.comp_pascal}{holder}"
        def fn(t, section=section, imps=imps, cls=cls):
            if f" {A}{o.name}{holder[:-1]}" in t:
                return t
            try:
                t, _ = remove_section_placeholder(t, r"add the .* of the component's persistable objects here")
            except ValueError:
                pass
            return add_imports(insert_at_type_end(t, cls, section), imps)
        c.edit(c.model / f"model/oids/{cls}.java", fn)

    # client API
    N, l, pkg, base = o.name, o.lname, o.pkg, ids.base_pkg
    fields = (f"\t////////// {N} services\n"
              + "".join(f"\t@Getter private final {A}ClientAPIDelegateFor{N}{op}Services _for{N}{op};\n" for op in OPS)
              + f"\t@Getter private final {A}{N}RefSolver _{l}RefSolver;\n")
    ctor = (f"\t\t// {N}\n"
            + "".join(f"\t\t_for{N}{op} = new {A}ClientAPIDelegateFor{N}{op}Services(securityContextProvider,\n"
                      f"\t\t\tmodelObjectsMarshaller,\n"
                      f"\t\t\tthis.getServiceInterfaceCoreImplOrProxy({A}{op}ServicesFor{N}.class));\n" for op in OPS)
            + f"\t\t// {N} ref solver\n"
            + f"\t\t_{l}RefSolver = new {A}{N}RefSolver(securityContextProvider,\n"
            + f"\t\t\tthis.getServiceInterfaceCoreImplOrProxy({A}CRUDServicesFor{N}.class));\n")
    imps = (["lombok.Getter", f"{base}.api.{pkg}.{A}{N}RefSolver"]
            + [f"{base}.api.interfaces.{pkg}.{A}{op}ServicesFor{N}" for op in OPS]
            + [f"{base}.api.{pkg}.{A}ClientAPIDelegateFor{N}{op}Services" for op in OPS])
    def client_api(t):
        if f" _for{N}CRUD;" in t:
            return t
        m = re.search(r"^[ \t]*// add a @Getter private final field[^\n]*\n[ \t]*//[^\n]*here\n", t, re.M)
        if m:
            t = t[:m.start()] + fields + t[m.end():]
        else:
            m = re.search(rf"^/{{89}}[ \t]*\n//[ \t]*CONSTRUCTOR", t, re.M)
            t = t[:m.start()] + "\n" + fields + t[m.start():]
        t, found = replace_placeholder_line(t, r"create each persistable object's client API delegates here", ctor)
        if not found:
            _, close = method_body(t, rf"public {A}ClientAPIFor{ids.comp_pascal}\(")
            t = insert_before_closing_line(t, close, ctor)
        return add_imports(t, imps)
    c.edit(c.apic / f"{A}ClientAPIFor{ids.comp_pascal}.java", client_api)


def _cut_custom_finders(t: str, interface: bool) -> str:
    """The sample's custom finders (findByNameExact) are the last members of its Find classes: remove them."""
    pat = r"\n[ \t]*/\*\*\n[ \t]*\* Finds a" if interface else r"\n[ \t]*@Override\n[ \t]*public FindResult<"
    m = re.search(pat, t)
    if not m:
        return t
    return t[:m.start() + 1] + t[t.rindex("}"):]


# =========================================================================================
#  CORE
# =========================================================================================
def existing_summarizers(c: Ctx) -> list[tuple[str, str]]:
    p = c.db / f"summarizers/{c.ids.core}SummarizersFor{c.ids.comp_pascal}PersistableObjs.java"
    if not exists(p):
        return []
    return re.findall(rf"@Getter protected final {c.ids.core}SummarizerServiceFor(\w+) _(\w+)Summarizer;", read_text(p))


def add_core(c: Ctx) -> None:
    ids, o = c.ids, c.obj
    A, C, CP, N, l, pkg, base = ids.api, ids.core, ids.comp_pascal, o.name, o.lname, o.pkg, ids.base_pkg
    sample_db = c.pocs / SAMPLE_CORE / "ogovSampleSysSampleCompCOREDBPersistenceClasses/src/main/java/ogov/samplesys/samplecomp/core/db"
    others = [x for x in existing_summarizers(c) if x[0] != N]
    summarizers = others + [(N, l)]
    for rel in CORE_FILES:
        t = read_text(sample_db / rel)
        fn = rel.rsplit("/", 1)[1]
        if fn == "OG91DBEntityForSamplePersistableObj.java":
            # no custom finders: no named queries nor columns beyond the ones of the base entity
            t = t[:t.index("@NamedQueries({")] + t[t.index("@Accessors(prefix=\"_\")"):]
            t = t[:re.search(rf"^/{{89}}[ \t]*\n//[ \t]*FIELDS", t, re.M).start()] + "}\n"
            t = regroup_imports(t, remove=("jakarta.persistence.Basic", "jakarta.persistence.Column", "jakarta.persistence.NamedQueries",
                                           "jakarta.persistence.NamedQuery", "lombok.Getter", "lombok.Setter"))
        elif fn == "OG91ModelObjToDBEntityTransferDelegateForSamplePersistableObj.java":
            t = re.sub(r"[ \t]*// fields exposed outside the descriptor\n[ \t]*dbEntity\.setName\(modelObj\.getName\(\)\);\n",
                       "\t\t// copy here the model fields exposed outside the descriptor (the ones filtered by finders)\n", t)
        elif fn == "OG91DBFindForSamplePersistableObj.java":
            t = _cut_custom_finders(t, interface=False)
            t = regroup_imports(t, remove=("java.util.Collection", "jakarta.persistence.TypedQuery", "r01f.model.persistence.FindResult",
                                           "r01f.model.persistence.FindResultBuilder", "r01f.model.security.context.SecurityContext"))
        elif fn == "OG91FindServicesDelegateForSamplePersistableObj.java":
            t = _cut_custom_finders(t, interface=False)
            t = regroup_imports(t, remove=("r01f.model.persistence.FindResult", "r01f.model.security.context.SecurityContext", "r01f.util.types.Strings"))
        elif fn == "OG91COREFindServicesForSamplePersistableObj.java":
            t = _cut_custom_finders(t, interface=False)
            t = regroup_imports(t, remove=("r01f.model.persistence.FindResult", "r01f.model.security.context.SecurityContext"))
        elif fn == "OG91CORESummarizeServicesForSamplePersistableObj.java":
            # the summarizers container needs ALL the component's summarizers
            m = re.search(r"([ \t]*)final OG91SummarizerServiceForSamplePersistableObj samplePersistableObjSummarizer\) \{", t)
            params = ",\n".join(f"{m.group(1)}final OG91SummarizerServiceFor{n} {ln}Summarizer" for n, ln in summarizers) + ") {"
            t = t[:m.start()] + params + t[m.end():]
            t = t.replace("new OG91SummarizersForSampleCompPersistableObjs(samplePersistableObjSummarizer)",
                          "new OG91SummarizersForSampleCompPersistableObjs(" + ",".join(ln + "Summarizer" for _n, ln in summarizers) + ")")
            t = c.sub(t)
            t = regroup_imports(t, add=[f"{base}.core.db.summarizers.{C}SummarizersInterfacesFor{CP}PersistaleObjects.{C}SummarizerServiceFor{n}"
                                        for n, _ln in summarizers])
            c.out.write_new(c.db / c.sub(rel), t, force=c.force)
            continue
        c.out.write_new(c.db / c.sub(rel), c.sub(t), force=c.force)

    # --- the Summarize services of the other objects get the new summarizer
    for svc in sorted(c.db.glob(f"services/*/{C}CORESummarizeServicesFor*.java")) if c.db.is_dir() else []:
        if svc.stem == f"{C}CORESummarizeServicesFor{N}":
            continue
        def upd(t):
            if f" {l}Summarizer" in t:
                return t
            t = re.sub(rf"(new {C}SummarizersFor{CP}PersistableObjs\([^)]*)\)", rf"\1,{l}Summarizer)", t)
            t = re.sub(rf"(final {C}SummarizerServiceFor\w+ \w+Summarizer)\) \{{",
                       rf"\1,\n\t\t\t\tfinal {C}SummarizerServiceFor{N} {l}Summarizer) {{", t, count=1)
            return add_imports(t, [f"{base}.core.db.summarizers.{C}SummarizersInterfacesFor{CP}PersistaleObjects.{C}SummarizerServiceFor{N}"])
        c.edit(svc, upd)

    # --- searcher providers
    provider = f"""{SEP}
// 	{N} searcher provider
{SEP}
	public static class {C}SearcherProviderFor{N}
	            extends DBSearcherProviderBase<{A}SearchFilterFor{N},{A}SearchResultItemFor{N}> {{
		public {C}SearcherProviderFor{N}(final Marshaller marshaller,
										  final DBModuleConfig dbModuleConfig,
										  final Provider<EntityManager> entityManagerProvider) {{
			super(marshaller,
				  dbModuleConfig,
				  entityManagerProvider);
		}}
		@Override
		public Searcher<{A}SearchFilterFor{N},{A}SearchResultItemFor{N}> get() {{
			return new {C}DBSearcherFor{N}(_dbModuleConfig,
											_entityManagerProvider.get(),	// a fresh new entity manager
											_marshaller);
		}}
	}}
"""
    prov_imps = [f"{base}.api.model.search.{A}SearchFilterFor{N}", f"{base}.api.model.search.{A}SearchResultItemFor{N}",
                 f"{base}.core.db.search.{pkg}.{C}DBSearcherFor{N}"]
    prov_cls = f"{C}DBSearcherProvidersFor{CP}PersistableObjs"
    prov_file = c.db / f"search/{prov_cls}.java"
    if not exists(prov_file):
        imps = ["jakarta.inject.Provider", "jakarta.persistence.EntityManager", "r01f.objectstreamer.Marshaller",
                "r01f.persistence.search.Searcher", "r01f.services.core.persistence.db.config.DBModuleConfig",
                "r01f.services.core.persistence.search.db.DBSearcherProviderBase"] + prov_imps
        t = f"package {base}.core.db.search;\n\nimport x;\n\npublic class {prov_cls} {{\n{provider}}}\n"
        c.out.write_new(prov_file, regroup_imports(t, add=imps, remove=("x",)), force=c.force)
    else:
        c.edit(prov_file, lambda t: t if re.search(rf"\bclass {C}SearcherProviderFor{N}\b", t)
               else add_imports(insert_at_type_end(t, prov_cls, provider), prov_imps))

    # --- summarizer service interface
    sz = c.db / "summarizers"
    iface = f"""{SEP}
//  {N}
{SEP}
	public interface {C}SummarizerServiceFor{N}
	         extends {C}Is{CP}PersistableObjSummarizerService<{A}{N}OID,{A}{N},
																		{A}Summarized{N}> {{
		// just extend
	}}
"""
    model_imps = [f"{base}.api.model.{A}{N}", f"{base}.api.model.oids.{A}{CP}OIDs.{A}{N}OID"]
    def interfaces(t):
        if f"interface {C}SummarizerServiceFor{N}\n" in t:
            return t
        t, found = replace_placeholder_line(t, r"add the summarizer service interface", iface)
        if not found:
            t = insert_at_type_end(t, f"{C}SummarizersInterfacesFor{CP}PersistaleObjects", iface)
        return add_imports(t, model_imps + [f"{base}.api.model.summaries.{A}Summarized{N}"])
    c.edit(sz / f"{C}SummarizersInterfacesFor{CP}PersistaleObjects.java", interfaces)

    # --- summarizable object type enum
    const = f"""	{N.upper()}({A}{N}OID.class,{A}{N}.class) {{
			@Override @SuppressWarnings("unchecked")
			public <S extends {A}IsSummarized{CP}PersistableObject<?,?>> S summarize(final {C}SummarizerVisitorFor{CP}PersistableObjByObjectOid visitor) {{
				return (S)visitor.summarize{N}();
			}}
			@Override @SuppressWarnings("unchecked")
			public <S extends {A}IsSummarized{CP}PersistableObject<?,?>> S summarize(final {C}SummarizerVisitorFor{CP}PersistableObj visitor) {{
				return (S)visitor.summarize{N}();
			}}
			@Override
			public void invalidateCache(final {C}SummarizedCacheInvalidatorVisitorFor{CP}PersistableObj visitor) {{
				visitor.invalidateCached{N}();
			}}
			@Override
			public boolean isCached(final {C}SummarizedCacheExistenceVisitorFor{CP}PersistableObj visitor) {{
				return visitor.isCached{N}();
			}}
	}}"""
    def enum(t):
        if re.search(rf"^\s*{N.upper()}\(", t, re.M):
            return t
        m = re.search(r"^[ \t]*// add a constant for each summarizable[^\n]*\n[ \t]*;\n", t, re.M)
        if m:
            t = t[:m.start()] + const + ";\n" + t[m.end():]
        else:
            k = t.rindex("};", 0, t.index("@Getter private final Class<"))
            t = t[:k] + "},\n" + const + ";" + t[k + 2:]
        return add_imports(t, model_imps)
    c.edit(sz / f"{C}Summarizable{CP}PersistableObjType.java", enum)

    # --- visitors
    methods = {
        f"{C}SummarizerVisitorFor{CP}PersistableObjByObjectOid": ("add a summarize", f"""		// {N}
		public {A}Summarized{N} summarize{N}() {{
			return _summarizers.get{N}Summarizer()
							   .summarize(_securityContext,
										  ({A}{N}OID)_modelObjOid,_lang);
		}}
"""),
        f"{C}SummarizerVisitorFor{CP}PersistableObj": ("add a summarize", f"""		// {N}
		public {A}Summarized{N} summarize{N}() {{
			return _summarizers.get{N}Summarizer()
							   .summarize(_securityContext,
										  ({A}{N})_modelObj,_lang);
		}}
"""),
        f"{C}SummarizedCacheInvalidatorVisitorFor{CP}PersistableObj": ("add an invalidateCached", f"""		// {N}
		public void invalidateCached{N}() {{
			{A}{N} obj = ({A}{N})_modelObj;
			_summarizers.get{N}Summarizer()
						.invalidate(obj.getOid());
		}}
"""),
        f"{C}SummarizedCacheExistenceVisitorFor{CP}PersistableObj": ("add an isCached", f"""		// {N}
		public boolean isCached{N}() {{
			{A}{N}OID oid = ({A}{N}OID)_modelObjOid;
			return _summarizers.get{N}Summarizer()
							   .isCached(oid);
		}}
"""),
    }
    def visitors(t):
        if f" isCached{N}()" in t:
            return t
        t = re.sub(r'^[ \t]*@SuppressWarnings\("unused"\)\n', "", t, flags=re.M)
        for cls, (placeholder, code) in methods.items():
            start, close = type_body(t, cls)
            m = re.search(rf"^[ \t]*// {placeholder}[^\n]*\n", t[start:close], re.M)
            if m:
                t = t[:start + m.start()] + code + t[start + m.end():]
            else:
                t = insert_before_closing_line(t, close, code)
        return add_imports(t, model_imps + [f"{base}.api.model.summaries.{A}Summarized{N}"])
    c.edit(sz / f"{C}Summarizable{CP}PersistableObjVisitors.java", visitors)

    # --- summarizers container
    field_line = f"\t@Getter protected final {C}SummarizerServiceFor{N} _{l}Summarizer;\n"
    def container(t):
        if f" _{l}Summarizer;" in t:
            return t
        t, found = replace_placeholder_line(t, r"add a @Getter protected final field", "\t// summarizers\n" + field_line)
        if not found:
            last = list(re.finditer(r"^[ \t]*@Getter protected final [^\n]*\n", t, re.M))[-1]
            t = t[:last.end()] + field_line + t[last.end():]
        return add_imports(t, ["lombok.Getter", f"{base}.core.db.summarizers.{C}SummarizersInterfacesFor{CP}PersistaleObjects.{C}SummarizerServiceFor{N}"])
    c.edit(sz / f"{C}SummarizersFor{CP}PersistableObjs.java", container)

    # --- persistence.xml
    entity = f"<class>{base}.core.db.entities.{pkg}.{C}DBEntityFor{N}</class>"
    def pxml(t):
        if entity in t:
            return t
        if "<!-- add the DB entity of each persistable object here -->" in t:
            return t.replace("<!-- add the DB entity of each persistable object here -->", entity)
        k = t.rindex("</class>") + len("</class>")
        return t[:k] + "\n\t\t" + entity + t[k:]
    for p in (c.dbres / "persistence.xml", c.dbres / f"jpa/{C.lower()}.{ids.comp_pkg}.persistence.xml"):
        c.edit(p, pxml)

    # --- guice: summarizer binding
    bind = (f"\t\tbinder.bind({C}SummarizerServiceFor{N}.class)\n"
            f"\t\t\t  .to({C}SummarizerServiceImplFor{N}.class)\n"
            f"\t\t\t  .in(Singleton.class);\n")
    def guice_persistence(t):
        if f"bind({C}SummarizerServiceFor{N}.class)" in t:
            return t
        m = re.search(r"^[ \t]*// bind each persistable object's summarizer.*?\.in\(Singleton\.class\);\n", t, re.M | re.S)
        if m:
            t = t[:m.start()] + "\t\t// individual summarizer services\n" + bind + t[m.end():]
        else:
            _, close = method_body(t, r"public void configureMoreBindings\(")
            t = insert_before_closing_line(t, close, bind)
        return add_imports(t, ["jakarta.inject.Singleton", f"{base}.core.db.summarizers.{C}SummarizerServiceImplFor{N}",
                               f"{base}.core.db.summarizers.{C}SummarizersInterfacesFor{CP}PersistaleObjects.{C}SummarizerServiceFor{N}"])
    c.edit(c.guice / f"{C}COREPersistenceBootstrapGuiceModuleFor{CP}.java", guice_persistence)

    # --- guice search module / spring config: searcher provider
    prov_imports = ["jakarta.inject.Provider", "jakarta.persistence.EntityManager", f"{base}.api.{A}AppCodesFor{CP}",
                    "r01f.model.annotations.ModelObjectsMarshaller", "r01f.objectstreamer.Marshaller", "r01f.services.core.ServicesCoreModuleConfig",
                    "r01f.services.core.annotations.ServicesCoreConfigFor", "r01f.services.core.persistence.db.annotations.EntityManagerFor",
                    "r01f.services.core.persistence.db.config.DBModuleConfig", "r01f.services.ids.ServiceIDs.CoreSubModule",
                    f"{base}.core.db.search.{prov_cls}.{C}SearcherProviderFor{N}"]
    fqn = f"{A}AppCodesFor{CP}.{ids.system_upper}_{ids.comp_upper}_CORE_FQN"
    def provider_method(annotations):
        return f"""	{annotations}
	public {C}SearcherProviderFor{N} searcherProviderFor{N}(@ModelObjectsMarshaller final Marshaller marshaller,
			@ServicesCoreConfigFor({fqn}) final ServicesCoreModuleConfig serviceCoreModuleConfig,
			@EntityManagerFor({fqn}) final Provider<EntityManager> entityManagerProvider) {{
		return new {C}SearcherProviderFor{N}(marshaller,
			serviceCoreModuleConfig.getSubModuleConfigFor(CoreSubModule.DBPERSISTENCE,DBModuleConfig.class),
			entityManagerProvider);
	}}
"""
    def with_provider(cls, annotations, placeholder, extra_imports):
        def fn(t):
            if f" searcherProviderFor{N}(" in t:
                return t
            t, found = replace_placeholder_line(t, placeholder, provider_method(annotations))
            if not found:
                t = insert_at_type_end(t, cls, provider_method(annotations))
            return add_imports(t, prov_imports + extra_imports)
        return fn
    c.edit(c.guice / f"{C}SearchGuiceModuleFor{CP}.java",
           with_provider(f"{C}SearchGuiceModuleFor{CP}", "@Provides @Singleton", r"add a @Provides @Singleton method",
                         ["com.google.inject.Provides", "jakarta.inject.Singleton"]))
    c.edit(c.spring / f"{C}CORESpringBootstrapConfigurationFor{CP}.java",
           with_provider(f"{C}CORESpringBootstrapConfigurationFor{CP}", '@Bean @SuppressWarnings("static-method")', r"add a @Bean method", []))


# =========================================================================================
#  TESTS
# =========================================================================================
def string_fields(model_text: str) -> list[str]:
    return re.findall(r"@Getter\s+@Setter\s+private\s+String\s+_(\w+)\s*;", model_text)


def sample_value(o: Obj, field: str) -> str:
    if field == "name":
        return f"a {o.words} name"
    if re.search(r"web|url|site", field, re.I):
        return f"https://www.a-{o.words.replace(' ', '-')}.test"
    return f"a {o.words} {words(field[:1].upper() + field[1:])}"


def add_tests(c: Ctx, tests_root: Path, model_text: str) -> None:
    ids, o = c.ids, c.obj
    A, CP, N, l, w, base = ids.api, ids.comp_pascal, o.name, o.lname, o.words, ids.base_pkg
    pp = base.replace(".", "/")
    fields = string_fields(model_text)
    has_name = "name" in fields
    setters = "".join(f'\t\toutObj.set{f[:1].upper() + f[1:]}("{sample_value(o, f)}");\n' for f in fields)
    cj = tests_root / f"ogovTestCommonClasses/src/main/java/{pp}/test/persistence"
    c.out.write_new(cj / f"OG99Mock{N}Factory.java", f"""package {base}.test.persistence;

import {base}.api.model.{A}{N};
import {base}.api.model.oids.{A}{CP}IDs.{A}{N}ID;
import r01f.patterns.Factory;

public class OG99Mock{N}Factory
  implements Factory<{A}{N}> {{
{SEP}
//
{SEP}
	@Override
	public {A}{N} create() {{
		{A}{N} outObj = new {A}{N}();
		outObj.setId({A}{N}ID.forId("a {w} id"));
{setters}		return outObj;
	}}
}}
""", force=c.force)
    load_check = f"\t\tAssertions.assertEquals({l}.getName(),retrieved{N}.getName());\n" if has_name else ""
    update = (f'\t\tretrieved{N}.setName("UpdatedName");\n' if has_name else "")
    update_check = (f'\t\tAssertions.assertEquals("UpdatedName",updated{N}.getName());\n' if has_name
                    else f"\t\tAssertions.assertNotNull(updated{N});\n")
    c.out.write_new(cj / f"OG99TestFor{N}Persistence.java", f"""package {base}.test.persistence;

import org.junit.jupiter.api.Assertions;

import {base}.api.{A}ClientAPIFor{CP};
import {base}.api.model.{A}{N};

public class OG99TestFor{N}Persistence {{
{SEP}
//	FIELDS
{SEP}
	private final {A}ClientAPIFor{CP} _{camel_comp(ids)}API;
	private final OG99Mock{N}Factory _{l}Factory = new OG99Mock{N}Factory();
{SEP}
//	CONSTRUCTORS
{SEP}
	public OG99TestFor{N}Persistence(final {A}ClientAPIFor{CP} {camel_comp(ids)}API) {{
		_{camel_comp(ids)}API = {camel_comp(ids)}API;
	}}
{SEP}
//	TEST
{SEP}
	public void doTest{N}Persistence() {{
		// create a {w}
		{A}{N} {l} = _{l}Factory.create();

		// persist the {w}
		{A}{N} persisted{N} = _{camel_comp(ids)}API.getFor{N}CRUD()
												.create({l});

		// retrieve the {w}
		{A}{N} retrieved{N} = _{camel_comp(ids)}API.getFor{N}CRUD()
												.load(persisted{N}.getOid());
		Assertions.assertNotNull(retrieved{N});
{load_check}
		// update the {w}
{update}		{A}{N} updated{N} = _{camel_comp(ids)}API.getFor{N}CRUD()
											  .update(retrieved{N});
{update_check}
		// delete the {w} (always, so reruns start from a clean table)
		_{camel_comp(ids)}API.getFor{N}CRUD()
						 .delete(updated{N}.getOid());
	}}
}}
""", force=c.force)

    test_cls = f"OG99TestFor{N}Persistence"
    test_imp = f"{base}.test.persistence.{test_cls}"
    tg = tests_root / f"ogovTestGuiceClasses/src/test/java/{pp}/test/guice/persistence"
    ts = tests_root / f"ogovTestSpringClasses/src/test/java/{pp}/test/spring/persistence"

    def guice_base(t):
        if f" get{N}Test()" in t:
            return t
        m = re.search(rf"^/{{89}}[ \t]*\n//[ \t]*APIs[ \t]*\n", t, re.M)
        code = (f"\tpublic {test_cls} get{N}Test() {{\n"
                f"\t\treturn new {test_cls}(OG99TestGuiceFor{CP}Base.get{CP}Api());\n\t}}\n")
        return add_imports(t[:m.start()] + code + t[m.start():], [test_imp])
    c.edit(tg / f"OG99TestGuiceFor{CP}Base.java", guice_base)
    c.edit(tg / f"OG99TestGuiceFor{CP}PersistableObjects.java", lambda t: t if f" test{N}Persistence()" in t else insert_at_type_end(
        t, f"OG99TestGuiceFor{CP}PersistableObjects",
        f'\t@Test @Tag("persistence")\n\tpublic void test{N}Persistence() {{\n\t\tthis.get{N}Test()\n\t\t\t.doTest{N}Persistence();\n\t}}\n'))
    c.edit(ts / f"OG99TestSpringBootstrapFor{CP}.java", lambda t: t if f" provide{N}Test(" in t else add_imports(insert_at_type_end(
        t, f"OG99TestSpringBootstrapFor{CP}",
        f"\t@Bean\n\tpublic {test_cls} provide{N}Test(final {A}ClientAPIFor{CP} api) {{\n\t\treturn new {test_cls}(api);\n\t}}\n"), [test_imp]))
    c.edit(ts / f"OG99TestSpringFor{CP}PersistenceBase.java", lambda t: t if f" _get{N}Test()" in t else add_imports(insert_at_type_end(
        t, f"OG99TestSpringFor{CP}PersistenceBase",
        f"\tprotected {test_cls} _get{N}Test() {{\n\t\treturn _context.getBean({test_cls}.class);\n\t}}\n"), [test_imp]))
    c.edit(ts / f"OG99TestSpringFor{CP}PersistableObjects.java", lambda t: t if f" test{N}Persistence()" in t else insert_at_type_end(
        t, f"OG99TestSpringFor{CP}PersistableObjects",
        f'\t@Test @Tag("persistence")\n\tpublic void test{N}Persistence() {{\n\t\t_get{N}Test().doTest{N}Persistence();\n\t}}\n'))


def camel_comp(ids: Ids) -> str:
    return ids.comp_pascal[:1].lower() + ids.comp_pascal[1:]


# =========================================================================================
#  MAIN
# =========================================================================================
def main() -> None:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    common_args(ap)
    ap.add_argument("--object", required=True, help="the persistable object: MediaGroup or OG10MediaGroup")
    ap.add_argument("--layers", default="api,core,tests", help="comma separated: api,core,tests (default: all)")
    args = ap.parse_args()
    ws = args.workspace or find_workspace()
    pocs, tests_root, _config_root, _byenv_root, codes_file = roots(args, ws)
    ids = resolve_ids(args, pocs, codes_file)
    name = args.object[len(ids.api):] if args.object.upper().startswith(ids.api) else args.object
    obj = Obj(name)
    out_root = args.out_root or pocs / f"ogov-{ids.system}"
    layers = {x.strip() for x in args.layers.split(",") if x.strip()}
    c = Ctx(ids, obj, pocs, out_root, Output(dry_run=args.dry_run, formatted=not args.no_format), args.force)

    model = c.model / f"model/{ids.api}{obj.name}.java"
    if not exists(model):
        sys.exit(f"ERROR: the model class {model} does not exist: write it first (see references/task2-api-layer.md)")
    model_text = read_text(model)
    if "PersistableObjectWithIDBase<" not in model_text:
        sys.exit(f"ERROR: {model.name} does not extend {ids.api}{ids.comp_pascal}PersistableObjectWithIDBase: objects "
                 f"without business ID are not generated by this script, follow references/task2-api-layer.md")
    print(f"system={ids.system}  component={ids.comp}  object={ids.api}{obj.name}  api={ids.api}  core={ids.core}  layers={','.join(sorted(layers))}")
    if "api" in layers:
        print(f"\n[api] {c.api_root}")
        add_api(c)
        c.out.report(out_root)
        c.out = Output(dry_run=args.dry_run, formatted=not args.no_format)
    if "core" in layers:
        print(f"\n[core] {c.core_root}")
        add_core(c)
        c.out.report(out_root)
        c.out = Output(dry_run=args.dry_run, formatted=not args.no_format)
    if "tests" in layers:
        print(f"\n[tests] {tests_root}")
        add_tests(c, tests_root, model_text)
        c.out.report(pocs)
    print(f"\nNOT generated: custom finders (references/task5-custom-finder.md). Refresh / rebuild the affected Eclipse projects.")


if __name__ == "__main__":
    main()
