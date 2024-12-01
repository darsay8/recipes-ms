package dev.rm.recipes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import dev.rm.recipes.model.Like;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.model.User;
import dev.rm.recipes.service.LikeService;
import dev.rm.recipes.service.RecipeService;
import dev.rm.recipes.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/recipes")
public class LikeController {

  private final LikeService likeService;
  private final UserService userService;
  private final RecipeService recipeService;

  public LikeController(LikeService likeService, UserService userService, RecipeService recipeService) {
    this.likeService = likeService;
    this.userService = userService;
    this.recipeService = recipeService;
  }

  @PostMapping("/{recipeId}/likes")
  public ResponseEntity<Object> createLike(@PathVariable Long recipeId) {
    try {

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String currentUsername = authentication.getName();

      User user = userService.getUserByEmail(currentUsername);
      Recipe recipe = recipeService.getRecipeById(recipeId);

      List<Like> existingLikes = likeService.getLikesByUserIdAndRecipeId(user.getUserId(), recipeId);

      if (!existingLikes.isEmpty()) {

        likeService.removeLike(existingLikes.get(0).getLikeId());

        return new ResponseEntity<>("Like removed", HttpStatus.OK);
      }

      Like like = Like.builder().user(user).recipe(recipe).build();
      Like createdLike = likeService.addLike(like);

      return new ResponseEntity<>(createdLike, HttpStatus.CREATED);

    } catch (Exception e) {

      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/{id}/likes")
  public ResponseEntity<List<Like>> getLikesByRecipeId(@PathVariable Long id) {
    List<Like> likes = likeService.getLikesByRecipeId(id);
    return new ResponseEntity<>(likes, HttpStatus.OK);
  }

  @GetMapping("/{id}/likes/total")
  public ResponseEntity<Long> getLikeCount(@PathVariable Long id) {
    Long likeCount = likeService.getLikeCount(id);
    return new ResponseEntity<>(likeCount, HttpStatus.OK);
  }

  // @GetMapping("/{id}/likes/total")
  // public ResponseEntity<Map<String, Object>> getRecipeById(@PathVariable Long
  // id) {
  // try {
  // long likeCount = recipeService.getLikeCount(id);
  // Map<String, Object> response = new HashMap<>();
  // response.put("likeCount", likeCount);
  // return ResponseEntity.ok(response);
  // } catch (RuntimeException e) {
  // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",
  // "Recipe not found"));
  // }
  // }
}