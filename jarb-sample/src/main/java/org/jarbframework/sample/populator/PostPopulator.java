package org.jarbframework.sample.populator;

import org.jarbframework.init.populate.DatabasePopulator;
import org.jarbframework.sample.model.Post;
import org.jarbframework.sample.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class PostPopulator implements DatabasePopulator {

    @Autowired
    private PostRepository postRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        Post post = new Post();
        post.setAuthor("jeroen@42.nl");
        post.setTitle("inserted via java");
        post.setMessage("some message");
        postRepository.save(post);
    }

}
