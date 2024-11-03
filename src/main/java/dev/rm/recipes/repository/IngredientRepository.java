package dev.rm.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.recipes.model.Ingredient;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
  List<Ingredient> findByRecipeId(Long recipeId);
}
