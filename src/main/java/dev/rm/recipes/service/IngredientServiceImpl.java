package dev.rm.recipes.service;

import org.springframework.stereotype.Service;

import dev.rm.recipes.model.Ingredient;
import dev.rm.recipes.repository.IngredientRepository;

import java.util.List;

@Service
public class IngredientServiceImpl implements IngredientService {

  private final IngredientRepository ingredientRepository;

  public IngredientServiceImpl(IngredientRepository ingredientRepository) {
    this.ingredientRepository = ingredientRepository;
  }

  @Override
  public Ingredient createIngredient(Ingredient ingredient) {
    return ingredientRepository.save(ingredient);
  }

  @Override
  public Ingredient getIngredientById(Long id) {
    return ingredientRepository.findById(id).orElseThrow(() -> new RuntimeException("Ingredient not found"));
  }

  @Override
  public List<Ingredient> getAllIngredients() {
    return ingredientRepository.findAll();
  }

  @Override
  public Ingredient updateIngredient(Long id, Ingredient ingredient) {
    Ingredient existingIngredient = getIngredientById(id);
    ingredient.setId(existingIngredient.getId());
    return ingredientRepository.save(ingredient);
  }

  @Override
  public void deleteIngredient(Long id) {
    ingredientRepository.deleteById(id);
  }
}
