package dev.rm.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.recipes.model.Like;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.model.User;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
  List<Like> findByUser_UserId(Long userId);

  List<Like> findByRecipe_RecipeId(Long recipeId);

  List<Like> findByUser(User user);

  List<Like> findByRecipe(Recipe recipe);

  List<Like> findByUserAndRecipe(User user, Recipe recipe);

  List<Like> findByUser_UserIdAndRecipe_RecipeId(Long userId, Long recipeId);

}
