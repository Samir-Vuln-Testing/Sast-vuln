package com.example.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.cfg.Configuration;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Map;

/**
 * VULNERABLE CODE - Improper Build of SQL Mapping
 * 
 * This class demonstrates multiple TRUE POSITIVE cases of SQL injection
 * vulnerabilities through improper use of createQuery() with dynamic SQL building.
 * 
 * The vulnerabilities occur when user input is concatenated directly into
 * HQL/JPQL queries, especially for column names, ORDER BY clauses, and WHERE conditions.
 */
public class UserDAO {

    private SessionFactory sessionFactory;
    private EntityManagerFactory entityManagerFactory;

    public UserDAO() {
        // Initialize Hibernate SessionFactory with XML mapping
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        configuration.addResource("User.hbm.xml");  // Links to our XML file
        this.sessionFactory = configuration.buildSessionFactory();
        
        // Initialize JPA EntityManagerFactory
        this.entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit");
    }

    // ========================================================================
    // VULNERABLE PATTERN 1: Dynamic ORDER BY with string concatenation
    // TRUE POSITIVE - Should be detected by security scanner
    // ========================================================================
    
    public List<User> findUsersSortedVulnerable(String sortColumn, String sortDirection) {
        Session session = sessionFactory.openSession();
        
        try {
            // VULNERABILITY: User-controlled sortColumn directly concatenated into query
            // Attack Example: sortColumn = "username; DELETE FROM User--"
            String hql = "FROM User u ORDER BY u." + sortColumn + " " + sortDirection;
            
            Query<User> query = session.createQuery(hql, User.class);
            return query.list();
            
        } finally {
            session.close();
        }
    }

    // ========================================================================
    // VULNERABLE PATTERN 2: Dynamic column name in WHERE clause
    // TRUE POSITIVE - Should be detected by security scanner
    // ========================================================================
    
    public List<User> findUsersByFieldVulnerable(String fieldName, String value) {
        Session session = sessionFactory.openSession();
        
        try {
            // VULNERABILITY: User-controlled fieldName in WHERE clause
            // Attack Example: fieldName = "username OR 1=1--"
            String hql = "FROM User u WHERE u." + fieldName + " = :value";
            
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("value", value);
            return query.list();
            
        } finally {
            session.close();
        }
    }

    // ========================================================================
    // VULNERABLE PATTERN 3: Dynamic column selection
    // TRUE POSITIVE - Should be detected by security scanner
    // ========================================================================
    
    public List<?> selectUserColumnVulnerable(String columnName) {
        Session session = sessionFactory.openSession();
        
        try {
            // VULNERABILITY: User-controlled column name in SELECT
            // Attack Example: columnName = "(SELECT password FROM AdminUser)"
            String hql = "SELECT u." + columnName + " FROM User u";
            
            Query<?> query = session.createQuery(hql);
            return query.list();
            
        } finally {
            session.close();
        }
    }

    // ========================================================================
    // VULNERABLE PATTERN 4: Building query from XML base + user input
    // TRUE POSITIVE - Should be detected by security scanner
    // ========================================================================
    
    public List<User> searchUsersVulnerable(String searchTerm, String additionalFilter) {
        Session session = sessionFactory.openSession();
        
        try {
            // Gets base query from XML: "FROM User u WHERE u.username LIKE :searchTerm"
            String baseHql = session.getNamedQuery("searchUsersBase").getQueryString();
            
            // VULNERABILITY: Appending user-controlled filter
            // Attack Example: additionalFilter = "OR 1=1--"
            String hql = baseHql + " " + additionalFilter;
            
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("searchTerm", "%" + searchTerm + "%");
            return query.list();
            
        } finally {
            session.close();
        }
    }

    // ========================================================================
    // VULNERABLE PATTERN 5: Dynamic UPDATE with field mapping
    // TRUE POSITIVE - Should be detected by security scanner
    // ========================================================================
    
    public void updateUserFieldsVulnerable(Long userId, Map<String, Object> fields) {
        Session session = sessionFactory.openSession();
        
        try {
            session.beginTransaction();
            
            // Gets base from XML: "UPDATE User u SET "
            String baseHql = session.getNamedQuery("updateUserFieldBase").getQueryString();
            
            StringBuilder hql = new StringBuilder(baseHql);
            int count = 0;
            
            // VULNERABILITY: Building SET clause from user-controlled field names
            // Attack Example: field key = "role = 'admin' WHERE 1=1 OR u.id"
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                if (count > 0) hql.append(", ");
                hql.append("u.").append(entry.getKey()).append(" = :param").append(count);
                count++;
            }
            
            hql.append(" WHERE u.id = :userId");
            
            Query<?> query = session.createQuery(hql.toString());
            
            count = 0;
            for (Object value : fields.values()) {
                query.setParameter("param" + count, value);
                count++;
            }
            query.setParameter("userId", userId);
            
            query.executeUpdate();
            session.getTransaction().commit();
            
        } finally {
            session.close();
        }
    }

    // ========================================================================
    // VULNERABLE PATTERN 6: Native SQL query with dynamic columns
    // TRUE POSITIVE - Should be detected by security scanner
    // ========================================================================
    
    public List<?> executeNativeQueryVulnerable(String columnNames, String tableName) {
        Session session = sessionFactory.openSession();
        
        try {
            // VULNERABILITY: User-controlled column names and table name
            // Attack Example: columnNames = "* FROM users WHERE 1=1 UNION SELECT password FROM admin--"
            String sql = "SELECT " + columnNames + " FROM " + tableName;
            
            Query<?> query = session.createNativeQuery(sql);
            return query.list();
            
        } finally {
            session.close();
        }
    }

    // ========================================================================
    // VULNERABLE PATTERN 7: JPA EntityManager with dynamic query
    // TRUE POSITIVE - Should be detected by security scanner
    // ========================================================================
    
    public List<User> findUsersJPAVulnerable(String orderByClause) {
        EntityManager em = entityManagerFactory.createEntityManager();
        
        try {
            // VULNERABILITY: User-controlled ORDER BY clause with JPA
            // Attack Example: orderByClause = "u.id; DELETE FROM User u"
            String jpql = "SELECT u FROM User u ORDER BY " + orderByClause;
            
            javax.persistence.Query query = em.createQuery(jpql);
            return query.getResultList();
            
        } finally {
            em.close();
        }
    }

    // ========================================================================
    // VULNERABLE PATTERN 8: Conditional query building
    // TRUE POSITIVE - Should be detected by security scanner
    // ========================================================================
    
    public List<User> searchWithFiltersVulnerable(String role, String sortBy, boolean includeInactive) {
        Session session = sessionFactory.openSession();
        
        try {
            String hql = "FROM User u WHERE u.role = :role";
            
            // VULNERABILITY: Conditional concatenation of user input
            if (includeInactive) {
                hql += " OR u.status = 'inactive'";
            }
            
            // VULNERABILITY: User-controlled sort column
            // Attack Example: sortBy = "(SELECT password FROM AdminUser)"
            hql += " ORDER BY u." + sortBy;
            
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("role", role);
            return query.list();
            
        } finally {
            session.close();
        }
    }

    // ========================================================================
    // VULNERABLE PATTERN 9: Using named query incorrectly with concatenation
    // TRUE POSITIVE - Should be detected by security scanner
    // ========================================================================
    
    public List<User> findUsersSortedFromXMLVulnerable(String sortColumn) {
        Session session = sessionFactory.openSession();
        
        try {
            // Gets incomplete query from XML: "FROM User u ORDER BY "
            String baseQuery = session.getNamedQuery("findUsersSorted").getQueryString();
            
            // VULNERABILITY: Completing query with user input
            // Attack Example: sortColumn = "1; DROP TABLE users--"
            String completeHql = baseQuery + sortColumn;
            
            Query<User> query = session.createQuery(completeHql, User.class);
            return query.list();
            
        } finally {
            session.close();
        }
    }

    // ========================================================================
    // VULNERABLE PATTERN 10: Native query from XML with dynamic completion
    // TRUE POSITIVE - Should be detected by security scanner
    // ========================================================================
    
    public List<?> executeSQLFromXMLVulnerable(String whereClause) {
        Session session = sessionFactory.openSession();
        
        try {
            // Gets base SQL from XML: "SELECT * FROM users WHERE "
            org.hibernate.query.NativeQuery<?> baseQuery = 
                session.getNamedNativeQuery("nativeFindUsers");
            String baseSql = baseQuery.getQueryString();
            
            // VULNERABILITY: Appending user-controlled WHERE clause
            // Attack Example: whereClause = "1=1 OR username = 'admin'--"
            String completeSql = baseSql + whereClause;
            
            Query<?> query = session.createNativeQuery(completeSql);
            return query.list();
            
        } finally {
            session.close();
        }
    }

    // ========================================================================
    // BONUS: Multiple vulnerabilities in one method
    // TRUE POSITIVE - Should detect multiple issues
    // ========================================================================
    
    public List<User> complexQueryVulnerable(String selectFields, String whereColumn, 
                                              String whereValue, String orderBy) {
        Session session = sessionFactory.openSession();
        
        try {
            // MULTIPLE VULNERABILITIES in one query
            String hql = "SELECT " + selectFields +                    // SQL Injection 1
                        " FROM User u WHERE u." + whereColumn +        // SQL Injection 2
                        " = :value ORDER BY u." + orderBy;             // SQL Injection 3
            
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("value", whereValue);
            return query.list();
            
        } finally {
            session.close();
        }
    }

    // ========================================================================
    // SECURE EXAMPLE: Proper implementation for comparison
    // FALSE POSITIVE - This should NOT be flagged
    // ========================================================================
    
    public List<User> findUsersSortedSecure(String sortColumn, String sortDirection) {
        Session session = sessionFactory.openSession();
        
        try {
            // SECURE: Whitelist validation
            if (!isValidSortColumn(sortColumn) || !isValidSortDirection(sortDirection)) {
                throw new IllegalArgumentException("Invalid sort parameters");
            }
            
            // Safe to use after validation
            String hql = "FROM User u ORDER BY u." + sortColumn + " " + sortDirection;
            Query<User> query = session.createQuery(hql, User.class);
            return query.list();
            
        } finally {
            session.close();
        }
    }

    private boolean isValidSortColumn(String column) {
        return column != null && 
               (column.equals("username") || column.equals("email") || 
                column.equals("role") || column.equals("id"));
    }

    private boolean isValidSortDirection(String direction) {
        return direction != null && 
               (direction.equalsIgnoreCase("ASC") || direction.equalsIgnoreCase("DESC"));
    }

    // ========================================================================
    // User entity class
    // ========================================================================
    
    public static class User {
        private Long id;
        private String username;
        private String email;
        private String role;
        private String status;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}