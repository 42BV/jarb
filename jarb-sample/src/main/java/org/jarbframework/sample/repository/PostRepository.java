package org.jarbframework.sample.repository;

import java.util.List;

import org.jarbframework.sample.domain.Post;

public interface PostRepository {

    List<Post> all();

    Post add(Post post);

}
