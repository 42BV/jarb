package org.jarbframework.constraint.violation.resolver.recognize;

public final class DatabaseProduct {

    public final String databaseName;
    
    public final String version;

    public DatabaseProduct(String name, String version) {
        this.databaseName = name;
        this.version = version;
    }
    
    public String getName() {
        return databaseName;
    }
    
    public String getVersion() {
        return version;
    }
    
}
