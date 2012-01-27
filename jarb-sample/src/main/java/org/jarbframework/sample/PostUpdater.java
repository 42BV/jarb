package org.jarbframework.sample;

import org.jarbframework.populator.DatabaseUpdater;
import org.springframework.beans.factory.annotation.Autowired;

public class PostUpdater implements DatabaseUpdater {

	@Autowired
	private PostRepository postRepository;
	
	@Override
	public void update() {
		Post post = new Post();
		post.setAuthor("jeroen@42.nl");
		post.setTitle("inserted via java");
		post.setMessage("some message");
		postRepository.save(post);
	}

}
