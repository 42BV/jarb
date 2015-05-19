package org.jarbframework.sample.repository;

import org.jarbframework.sample.model.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {
}
