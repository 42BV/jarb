package org.jarb.sample.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jarb.sample.domain.Post;
import org.springframework.stereotype.Repository;

@Repository
public class JpaPostRepository implements PostRepository {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Post> all() {
        return entityManager.createQuery("from Post", Post.class).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Post add(Post post) {
        entityManager.persist(post);
        return post;
    }

}
