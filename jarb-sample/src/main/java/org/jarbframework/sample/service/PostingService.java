package org.jarbframework.sample.service;

import java.util.List;

import org.jarbframework.sample.domain.Post;
import org.jarbframework.sample.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostingService {
    
    @Autowired
    private PostRepository posts;

    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return posts.all();
    }

    @Transactional
    public Post createPost(Post post) {
        return posts.add(post);
    }

}
