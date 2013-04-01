package org.jarbframework.populator.excel.metamodel;

import java.util.Collection;
import java.util.Map;

import javax.persistence.metamodel.EmbeddableType;

/**
 * Data structure for storing information needed for an inversed JoinColumn relation (ElementCollection or OneToMany with JoinColumn).
 * @author Sander Benschop
 *
 */
public final class InverseJoinColumnReferenceProperties {

    private InverseJoinColumnReferenceType inverseJoinColumnReferenceType = InverseJoinColumnReferenceType.SERIALIZABLE_CLASS;

    private EmbeddableType<?> embeddableType;
    private String referencedTableName;

    /** JoinColumnNames needed for the reference. Key is the Column name on the enclosing side, Value is the column name on the InversedReference side. */
    private Map<String, String> joinColumnNames;

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
     * Returns the column name hashmap.
     * @return Key is the Column name on the enclosing side, Value is the column name on the InversedReference side
     */
    public Map<String, String> getReferencedColumnAndJoinColumnNamesHashMap() {
        return joinColumnNames;
    }

    /**
     * Returns the @JoinColumn names on the InversedReference side.
     * @return @JoinColumn names
     */
    public Collection<String> getJoinColumnNames() {
        return joinColumnNames.values();
    }

    /**
     * Returns the @JoinColumn names the destination table uses the refer back to its owner record.
     * @param joinColumnNames @JoinColumn names
     */
    public void setJoinColumnNames(Map<String, String> joinColumnNames) {
        this.joinColumnNames = joinColumnNames;
    }

    public void setInverseJoinColumnReferenceType(InverseJoinColumnReferenceType inverseJoinColumnReferenceType) {
        this.inverseJoinColumnReferenceType = inverseJoinColumnReferenceType;
    }

    public InverseJoinColumnReferenceType getInverseJoinColumnReferenceType() {
        return inverseJoinColumnReferenceType;
    }

    public void setEmbeddableType(EmbeddableType<?> embeddableType) {
        this.embeddableType = embeddableType;
    }

    public EmbeddableType<?> getEmbeddableType() {
        return embeddableType;
    }

}
