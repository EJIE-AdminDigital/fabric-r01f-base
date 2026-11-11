package test.fixture;

import java.util.Collection;
import java.util.function.Function;

/**
 * House-style fixture: format_java.py must leave this file unchanged, and must put back every continuation
 * line of it that is shifted. Not compiled: it only has to look like Java to the formatter.
 */
@Entity @Cacheable(false)
@Table(name = "OG11MEDIA",
       indexes = {
            @Index(name = "OG11MEDIAIDX_BY_ID",
                   columnList = "ID",unique = true),    // the id is unique
            @Index(name = "OG11MEDIAIDX_BY_MEDIAGROUP_OID",
                   columnList = "MEDIAGROUP_OID")
       })
@NamedQueries({
    @NamedQuery(name = "OG11DBEntityForMedia.findByMediaGroupAndName",
                query = "SELECT dbMedia " +
                          "FROM OG11DBEntityForMedia dbMedia " +
                         "WHERE dbMedia._mediaGroupOid = :mediaGroupOid " +
                           "AND dbMedia._name = :name"),
    @NamedQuery(name = "OG11DBEntityForMedia.leafs",
                query = "SELECT node " +
                          "FROM Node node " +
                         "WHERE node._oid IN (SELECT rel._descendantOid " +
                                               "FROM Relation rel " +
                                          "LEFT JOIN Relation rel2 ON (rel._oid = rel2._oid " +
                                                                       "AND " +
                                                                       "rel._x <> rel2._x) " +
                                              "WHERE rel2._oid IS NULL)")
})
@ComponentScan(basePackageClasses = {FirstConfiguration.class,
                                     SecondConfiguration.class})
public class MyType<T extends OtherType,
                    SELF_TYPE extends MyClass<T>>
     extends MyTypeBase<T,
                        SELF_TYPE>
  implements MyInterface1,
             MyInterface2<T,
                          SELF_TYPE> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @MarshallField(as="name",escape=true)
    @Getter @Setter private String _name;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public MyType(// the security context provider
                  final Provider<SecurityContext> securityContextProvider,
                  // the services
                  final MyServices services) {
        super(securityContextProvider,
              services);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    public Collection<MyEntity> find(final SecurityContext securityContext,
                                     final String name) {
        Collection<MyEntity> items = this.getEntityManager()
                                         .createNamedQuery(Strings.customized("{}.findByOid",
                                                                              MyEntity.class.getSimpleName()),
                                                           MyEntity.class)
                                         .setParameter("name",name)
                                         .getResultList();
        boolean valid = items != null
                     && !items.isEmpty();
        if (valid
         && name != null) {
            items.forEach(item -> {
                              System.out.println(item);
                          });
        }
        String text = valid ? "found"
                            : name != null ? "not found"
                                           : "no name";
        Function<String,Integer> fn = new Function<String,Integer>() {
                                          @Override
                                          public Integer apply(final String s) {
                                              return Integer.parseInt(s);
                                          }
                                      };
        String literal = "a\tb,  c (d,  e)";
        try {
            fn.apply(literal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return items.stream()
                    .map(i -> other.stream()
                                   .filter(Objects::nonNull))
                    .toList();
    }
}
