package org.jarb.sample.service;

import java.util.List;

import org.jarb.sample.dao.PostRepository;
import org.jarb.sample.domain.Post;
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
