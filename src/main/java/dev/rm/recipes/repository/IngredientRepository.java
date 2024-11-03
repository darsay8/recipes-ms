package dev.rm.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.recipes.model.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

}
