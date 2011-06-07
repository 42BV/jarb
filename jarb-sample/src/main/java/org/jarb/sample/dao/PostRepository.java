package org.jarb.sample.dao;

import java.util.List;

import org.jarb.sample.domain.Post;

public interface PostRepository {

    List<Post> all();

    Post add(Post post);

}
