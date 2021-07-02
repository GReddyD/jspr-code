package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  private PostController controller;
  public static final String API = "/api/posts";
  public static final String API_D = "/api/posts/\\d+";

  @Override
  public void init() {
    // OLD //
//    final var repository = new PostRepository();
//    final var service = new PostService(repository);
//    controller = new PostController(service);
    // OLD //

    final var context = new AnnotationConfigApplicationContext("ru.netology");
    final var service = context.getBean(PostService.class);
    final var repository = context.getBean(PostRepository.class);
    controller = context.getBean(PostController.class);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp){
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      // primitive routing
      if (method.equals("GET")) {
        if (path.equals(API)) {
          controller.all(resp);
          return;
        }
        if (path.matches(API_D)){
          // easy way
          final var id = Long.parseLong(path.substring(path.lastIndexOf("/")));
          controller.getById(id, resp);
          return;
        }
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp){
    final var path = req.getRequestURI();
    final var method = req.getMethod();
    try {
      if (method.equals("POST") && path.equals(API)) {
        controller.save(req.getReader(), resp);
        return;
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      if (method.equals("DELETE") && path.matches(API_D)) {
        // easy way
        final var id = Long.parseLong(path.substring(path.lastIndexOf("/")));
        controller.removeById(id, resp);
        return;
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
}

