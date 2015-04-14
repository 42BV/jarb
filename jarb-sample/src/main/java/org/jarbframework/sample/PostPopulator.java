package org.jarbframework.sample;

import org.jarbframework.populator.DatabasePopulator;
import org.springframework.beans.factory.annotation.Autowired;

public class PostPopulator implements DatabasePopulator {

    @Autowired
    private PostRepository postRepository;

    @Override
    public void execute() {
        Post post = new Post();
        post.setAuthor("jeroen@42.nl");
        post.setTitle("inserted via java");
        post.setMessage("some message");
        postRepository.save(post);
    }

}
