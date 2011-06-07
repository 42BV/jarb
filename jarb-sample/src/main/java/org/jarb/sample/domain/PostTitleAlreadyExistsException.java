package org.jarb.sample.domain;

public class PostTitleAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = -7112473482705797922L;

    public PostTitleAlreadyExistsException() {
        super("Post title already exists.");
    }
}
