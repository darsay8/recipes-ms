package dev.rm.recipes.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import dev.rm.recipes.model.Comment;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.model.User;
import dev.rm.recipes.service.CommentService;
import dev.rm.recipes.service.RecipeService;
import dev.rm.recipes.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/recipes")
public class CommentController {

  private final CommentService commentService;
  private final RecipeService recipeService;
  private final UserService userService;

  public CommentController(CommentService commentService, UserService userService, RecipeService recipeService) {
    this.commentService = commentService;
    this.recipeService = recipeService;
    this.userService = userService;
  }

  @GetMapping("/{id}/comments")
  public ResponseEntity<List<Comment>> getCommentsByRecipeId(@PathVariable Long id) {
    List<Comment> comments = commentService.getCommentsByRecipeId(id);
    return new ResponseEntity<>(comments, HttpStatus.OK);
  }

  @PostMapping("/{recipeId}/comments")
  public ResponseEntity<Comment> createComment(@PathVariable Long recipeId, @RequestBody Comment comment) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();

    User user = userService.getUserByEmail(currentUsername);

    Recipe recipe = recipeService.getRecipeById(recipeId);

    comment.setUser(user);
    comment.setRecipe(recipe);

    Comment createdComment = commentService.createComment(comment);

    return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
  }
}
