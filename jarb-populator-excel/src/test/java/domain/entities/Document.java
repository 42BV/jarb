package domain.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Document is extended by Wordfile to mimic another Panorama case.
 * In this case a circular reference is present with an abstract class and an implementation.
 * This class is there to test ETD's behaviour with this problem.
 * @author Sander Benschop
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "format")
@Table(name = "documents")
public abstract class Document {

    @Id
    @Column(name = "document_id")
    @GeneratedValue
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Employee writer;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentRevision> documentRevisions = new ArrayList<DocumentRevision>();

    @ManyToOne
    @JoinColumn(name = "current_revision_id")
    private DocumentRevision currentDocumentRevision;

    /**
     * Returns the document's id.
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the document's title.
     * @param title Title of document
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the document's title.
     * @return Document title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Replaces the DocumentRevisions list.
     * @param documentRevisions List of document revisions.
     */
    public void setDocumentRevisions(List<DocumentRevision> documentRevisions) {
        this.documentRevisions = documentRevisions;
    }

    /**
     * Returns the DocumentRevisions list.
     * @return List of document revisions.
     */
    public List<DocumentRevision> getDocumentRevisions() {
        return documentRevisions;
    }

    /**
     * Sets the current document revision.
     * @param currentDocumentRevision Latest revision of the document.
     */
    public void setCurrentDocumentRevision(DocumentRevision currentDocumentRevision) {
        this.currentDocumentRevision = currentDocumentRevision;
    }

    /**
     * Returns the current document revision.
     * @return Current document revision.
     */
    public DocumentRevision getCurrentDocumentRevision() {
        return currentDocumentRevision;
    }

    /**
     * Sets the document's writer.
     * @param writer Employee who wrote the document.
     */
    public void setWriter(Employee writer) {
        this.writer = writer;
    }

    /**
     * Returns the document's writer.
     * @return Employee who wrote the document.
     */
    public Employee getWriter() {
        return writer;
    }

    /** This function is just to appease Sonar by having an abstract method. */
    public abstract void abstractMethod();
}
