package ru.netology.repository;

import ru.netology.model.Post;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

// Stub
public class PostRepository {
  private final AtomicInteger ID = new AtomicInteger(0);
  private final List<Post> postList = new CopyOnWriteArrayList<>();

  public List<Post> all() {
    return Collections.emptyList();
  }

  public Optional<Post> getById(long id) {
    return Optional.empty();
  }

  public Post save(Post post) {
    if (post.getId() == 0) {
      ID.getAndIncrement();
      post.setId(ID.get());
    }
    if (post.getId() != 0) {
      for (Post currentPost : postList) {
        if (currentPost.getId() == post.getId()) {
          currentPost.setContent(post.getContent());
        } else {
          ID.getAndIncrement();
          System.out.println("Post с ID не существует, новый ID = " + ID.get());
          post.setId(ID.get());
        }
      }
    }
    postList.add(post);
    return post;
  }

  public void removeById(long id) {
  }
}
