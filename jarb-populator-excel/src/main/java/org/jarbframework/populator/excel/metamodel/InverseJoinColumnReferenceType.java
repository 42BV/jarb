package org.jarbframework.populator.excel.metamodel;

/**
 * Defines the possible types of an InverseJoinColumnReference.
 * @author Sander Benschop
 *
 */
public enum InverseJoinColumnReferenceType {

    /**
     * Means that the InverseJoinColumn reference is to an Embeddable via an ElementCollection.
     */
    EMBEDDABLE,

    /**
     * Means that the InverseJoinColumn reference is to a standard serializable class;
     */
    SERIALIZABLE_CLASS

}
