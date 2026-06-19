package r01f.spring.boot;



/**
 * Spring Boot Auto-configuration class names for V3.x and V4.x [D-H-J-T]
 * Using Strings instead of .class prevents Compilation Errors and NoClassDefFoundErrors 
 * when specific dependencies (like Hibernate 8 or Jakarta 4) are missing from the classpath.
 *    
 *    [ Spring 3]
 *	    import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
 *		import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
 *		import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
 *		import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
 *  
 */
public final class SpringBootAutoconfigurationNames {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
    /**
     * Spring Boot 4.x constants [ Spring 7 / Jakarta EE 11].
     * Note the new package structure under .autoconfigure subpackages.
     */
    public static final class V4 {
        public static final String DATA_SOURCE = 
            "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration";
        
        public static final String HIBERNATE_JPA = 
            "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration";
        
        public static final String TRANSACTION = 
            "org.springframework.boot.transaction.autoconfigure.TransactionAutoConfiguration";
     

        public static final String[] ALL_PERSISTENCE = { 
            DATA_SOURCE, HIBERNATE_JPA, TRANSACTION,  
        };
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Spring Boot 3.x constants [ Spring 6 / Jakarta EE 10 ].
     * Legacy package paths.
     */
    public static final class V3 {
        public static final String DATA_SOURCE = 
            "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration";
        
        public static final String HIBERNATE_JPA = 
            "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration";
        
        public static final String JPA_REPOSITORIES = 
                "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration";
        
        public static final String TRANSACTION = 
            "org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration";
        
       

        public static final String[] ALL_PERSISTENCE = { 
            DATA_SOURCE, HIBERNATE_JPA, TRANSACTION, JPA_REPOSITORIES 
        };
   }
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Combined list for hybrid environments.
     * Spring Boot will safely ignore any class name not found in the current classpath.
     */
    public static final String[] ALL_VERSIONS_EXCLUSIONS = {
    	// V4
        V4.DATA_SOURCE,V4.HIBERNATE_JPA,V4.TRANSACTION,
        // V3
        V3.DATA_SOURCE,V3.HIBERNATE_JPA,V3.TRANSACTION,V3.JPA_REPOSITORIES
    };
}