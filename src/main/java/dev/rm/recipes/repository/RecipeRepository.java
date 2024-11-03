package dev.rm.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.recipes.model.Recipe;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
  List<Recipe> findByUserId(Long userId);
}
