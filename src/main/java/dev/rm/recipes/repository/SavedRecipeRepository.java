package dev.rm.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.recipes.model.SavedRecipe;

import java.util.List;

public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Long> {
  List<SavedRecipe> findByUserId(Long userId);

  List<SavedRecipe> findByRecipeId(Long recipeId);
}
