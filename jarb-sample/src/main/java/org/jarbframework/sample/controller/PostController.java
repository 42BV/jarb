package org.jarbframework.sample.controller;

import org.jarbframework.sample.model.Post;
import org.jarbframework.sample.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepository;
    
    @Autowired
    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Post> index() {
        return postRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public Post post(@RequestBody Post post) {
        return postRepository.save(post);
    }

}
