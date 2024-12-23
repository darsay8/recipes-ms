package dev.rm.recipes.service;

import dev.rm.recipes.model.Like;
import java.util.List;

public interface LikeService {
  Like addLike(Like like);

  List<Like> getLikesByUserId(Long userId);

  List<Like> getLikesByRecipeId(Long recipeId);

  List<Like> getLikesByUserIdAndRecipeId(Long userId, Long recipeId);

  Long getLikeCount(Long recipeId);

  void removeLike(Long likeId);
}
