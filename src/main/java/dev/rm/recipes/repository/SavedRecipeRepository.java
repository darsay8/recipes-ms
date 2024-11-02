package dev.rm.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.recipes.model.SavedRecipe;

import java.util.List;
import java.util.UUID;

public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, UUID> {
  List<SavedRecipe> findByUserId(UUID userId);

  List<SavedRecipe> findByRecipeId(UUID recipeId);
}
