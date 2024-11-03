package dev.rm.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.recipes.model.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

}
