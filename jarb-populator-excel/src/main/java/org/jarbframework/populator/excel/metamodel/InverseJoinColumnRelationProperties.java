package org.jarbframework.populator.excel.metamodel;

import java.util.Collection;

/**
 * Data structure for storing information needed for an inversed JoinColumn relation (ElementCollection or OneToMany with JoinColumn).
 * @author Sander Benschop
 *
 */
public final class InverseJoinColumnRelationProperties {

    private String referencedTableName;
    private Collection<String> joinColumnNames;

    /**
     * Constructor which creates an InverseJoinColumnRelationProperties.
     * @param referencedTableName The destination tablename of the inversed JoinColumn relation
     * @param joinColumnNames The @JoinColumn names the destination table uses the refer back to its owner record
     */
    public InverseJoinColumnRelationProperties(String referencedTableName, Collection<String> joinColumnNames){
        this.setReferencedTableName(referencedTableName);
        this.setJoinColumnNames(joinColumnNames);
    }

    /**
     * Returns the destination tablename of the inversed JoinColumn relation.
     * @return Destination tablename
     */
    public String getReferencedTableName() {
        return referencedTableName;
    }

    /**
     * Sets the destination tablename of the inversed JoinColumn relation.
     * @param referencedTableName The destination tablename
     */
    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }

    /**
     * Returns the @JoinColumn names the destination table uses the refer back to its owner record.
     * @return @JoinColumn names
     */
    public Collection<String> getJoinColumnNames() {
        return joinColumnNames;
    }

    /**
     * Returns the @JoinColumn names the destination table uses the refer back to its owner record.
     * @param joinColumnNames @JoinColumn names
     */
    public void setJoinColumnNames(Collection<String> joinColumnNames) {
        this.joinColumnNames = joinColumnNames;
    }

}