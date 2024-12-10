package dev.rm.recipes.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.jpa.domain.Specification;

import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.Ingredient;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.model.Role;
import dev.rm.recipes.model.User;
import dev.rm.recipes.repository.RecipeRepository;

import java.util.List;
import java.util.Optional;

public class RecipeServiceImplTest {

  @Mock
  private RecipeRepository recipeRepository;

  private RecipeServiceImpl recipeService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    recipeService = new RecipeServiceImpl(recipeRepository);
  }

  @Test
  void testCreateRecipe() {

    User user = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();

    Ingredient ingredient1 = new Ingredient(1L, "Tomato", "2", null);
    Ingredient ingredient2 = new Ingredient(2L, "Pasta", "200g", null);
    List<Ingredient> ingredients = List.of(ingredient1, ingredient2);

    Recipe recipe = Recipe.builder()
        .name("Spaghetti Bolognese")
        .image("image_url")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("Italy")
        .difficulty(Difficulty.MEDIUM)
        .instructions("Cook the pasta and make the sauce.")
        .user(user)
        .ingredients(ingredients)
        .build();

    when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

    Recipe createdRecipe = recipeService.createRecipe(recipe);

    assertNotNull(createdRecipe);
    assertEquals("Spaghetti Bolognese", createdRecipe.getName());
    assertEquals(MealType.LUNCH, createdRecipe.getMealType());
    assertEquals("Italy", createdRecipe.getCountryOfOrigin());
    assertEquals(Difficulty.MEDIUM, createdRecipe.getDifficulty());
    assertEquals(2, createdRecipe.getIngredients().size());

    verify(recipeRepository, times(1)).save(recipe);
  }

  @Test
  void testGetRecipeById() {

    User user = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();
    Ingredient ingredient1 = new Ingredient(1L, "Tomato", "2", null);
    Ingredient ingredient2 = new Ingredient(2L, "Pasta", "200g", null);
    List<Ingredient> ingredients = List.of(ingredient1, ingredient2);

    Recipe recipe = Recipe.builder()
        .recipeId(1L)
        .name("Spaghetti Bolognese")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("Italy")
        .difficulty(Difficulty.MEDIUM)
        .instructions("Cook the pasta and make the sauce.")
        .user(user)
        .ingredients(ingredients)
        .build();

    when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

    Recipe foundRecipe = recipeService.getRecipeById(1L);

    assertNotNull(foundRecipe);
    assertEquals("Spaghetti Bolognese", foundRecipe.getName());
    assertEquals(2, foundRecipe.getIngredients().size());

    verify(recipeRepository, times(1)).findById(1L);
  }

  @Test
  void testGetRecipeById_NotFound() {
    when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () -> recipeService.getRecipeById(1L));
    assertEquals("Recipe not found", exception.getMessage());

    verify(recipeRepository, times(1)).findById(1L);
  }

  @Test
  void testGetAllRecipes() {

    User user = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();
    Ingredient ingredient1 = new Ingredient(1L, "Tomato", "2", null);
    Ingredient ingredient2 = new Ingredient(2L, "Pasta", "200g", null);
    List<Ingredient> ingredients = List.of(ingredient1, ingredient2);

    Recipe recipe1 = Recipe.builder()
        .recipeId(1L)
        .name("Spaghetti Bolognese")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("Italy")
        .difficulty(Difficulty.MEDIUM)
        .instructions("Cook the pasta and make the sauce.")
        .user(user)
        .ingredients(ingredients)
        .build();

    Recipe recipe2 = Recipe.builder()
        .recipeId(2L)
        .name("Chicken Curry")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("India")
        .difficulty(Difficulty.HARD)
        .instructions("Cook the chicken and make the curry.")
        .user(user)
        .ingredients(ingredients)
        .build();

    when(recipeRepository.findAll()).thenReturn(List.of(recipe1, recipe2));

    List<Recipe> recipes = recipeService.getAllRecipes();

    assertNotNull(recipes);
    assertEquals(2, recipes.size());
    assertTrue(recipes.contains(recipe1));
    assertTrue(recipes.contains(recipe2));

    verify(recipeRepository, times(1)).findAll();
  }

  @Test
  void testUpdateRecipe() {

    User user = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();

    Ingredient ingredient1 = new Ingredient(1L, "Tomato", "2", null);
    Ingredient ingredient2 = new Ingredient(2L, "Pasta", "200g", null);
    List<Ingredient> ingredients = List.of(ingredient1, ingredient2);

    Recipe existingRecipe = Recipe.builder()
        .recipeId(1L)
        .name("Spaghetti Bolognese")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("Italy")
        .difficulty(Difficulty.MEDIUM)
        .instructions("Cook the pasta and make the sauce.")
        .user(user)
        .ingredients(ingredients)
        .build();

    Recipe updatedRecipe = Recipe.builder()
        .recipeId(1L)
        .name("Spaghetti Bolognese with Meatballs")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("Italy")
        .difficulty(Difficulty.HARD)
        .instructions("Cook the pasta, make the sauce, and add meatballs.")
        .user(user)
        .ingredients(ingredients)
        .build();

    when(recipeRepository.findById(1L)).thenReturn(Optional.of(existingRecipe));
    when(recipeRepository.save(updatedRecipe)).thenReturn(updatedRecipe);

    Recipe result = recipeService.updateRecipe(1L, updatedRecipe);

    assertNotNull(result);
    assertEquals("Spaghetti Bolognese with Meatballs", result.getName());
    assertEquals(Difficulty.HARD, result.getDifficulty());

    verify(recipeRepository, times(1)).findById(1L);
    verify(recipeRepository, times(1)).save(updatedRecipe);
  }

  @Test
  void testDeleteRecipe() {
    User user = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();

    Ingredient ingredient1 = new Ingredient(1L, "Tomato", "2", null);
    Ingredient ingredient2 = new Ingredient(2L, "Pasta", "200g", null);
    List<Ingredient> ingredients = List.of(ingredient1, ingredient2);

    Recipe recipe = Recipe.builder()
        .recipeId(1L)
        .name("Spaghetti Bolognese with Meatballs")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("Italy")
        .difficulty(Difficulty.HARD)
        .instructions("Cook the pasta, make the sauce, and add meatballs.")
        .user(user)
        .ingredients(ingredients)
        .build();

    when(recipeRepository.existsById(1L)).thenReturn(true);

    recipeService.deleteRecipe(1L);

    verify(recipeRepository, times(1)).deleteById(1L);
  }

  @Test
  void testDeleteRecipe_NotFound() {
    when(recipeRepository.existsById(1L)).thenReturn(false);

    RuntimeException exception = assertThrows(RuntimeException.class, () -> recipeService.deleteRecipe(1L));
    assertEquals("Recipe not found", exception.getMessage());

    verify(recipeRepository, times(1)).existsById(1L);
  }

  @Test
  void testSearchRecipes() {

    User user = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();

    Ingredient ingredient1 = new Ingredient(1L, "Tomato", "2", null);
    Ingredient ingredient2 = new Ingredient(2L, "Pasta", "200g", null);
    List<Ingredient> ingredients = List.of(ingredient1, ingredient2);

    Recipe recipe1 = Recipe.builder()
        .recipeId(1L)
        .name("Spaghetti Bolognese")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("Italy")
        .difficulty(Difficulty.MEDIUM)
        .instructions("Cook the pasta and make the sauce.")
        .user(user)
        .ingredients(ingredients)
        .build();

    Recipe recipe2 = Recipe.builder()
        .recipeId(2L)
        .name("Chicken Curry")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("India")
        .difficulty(Difficulty.HARD)
        .instructions("Cook the chicken and make the curry.")
        .user(user)
        .ingredients(ingredients)
        .build();

    when(recipeRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(recipe1));

    List<Recipe> result = recipeService.searchRecipes("Spaghetti", MealType.LUNCH, "Italy", Difficulty.MEDIUM);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.contains(recipe1));
    assertFalse(result.contains(recipe2));

    verify(recipeRepository, times(1)).findAll(any(Specification.class));
  }

  @Test
  void testSearchRecipesNoMatch() {

    Specification<Recipe> spec = Specification.where(null);

    when(recipeRepository.findAll(any(Specification.class))).thenReturn(List.of());

    List<Recipe> result = recipeService.searchRecipes("Nonexistent", MealType.DINNER, "Unknown", Difficulty.EASY);

    assertNotNull(result);
    assertTrue(result.isEmpty());

    ArgumentCaptor<Specification> captor = ArgumentCaptor.forClass(Specification.class);
    verify(recipeRepository, times(1)).findAll(captor.capture());

    Specification<Recipe> capturedSpec = captor.getValue();
    assertNotNull(capturedSpec);

  }

  @Test
  void testSearchRecipesWithPartialNameMatch() {

    User user = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();

    Ingredient ingredient1 = new Ingredient(1L, "Tomato", "2", null);
    Ingredient ingredient2 = new Ingredient(2L, "Pasta", "200g", null);
    List<Ingredient> ingredients = List.of(ingredient1, ingredient2);

    Recipe recipe1 = Recipe.builder()
        .recipeId(1L)
        .name("Spaghetti Bolognese")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("Italy")
        .difficulty(Difficulty.MEDIUM)
        .instructions("Cook the pasta and make the sauce.")
        .user(user)
        .ingredients(ingredients)
        .build();

    when(recipeRepository.findAll(any(Specification.class))).thenReturn(List.of(recipe1));

    List<Recipe> result = recipeService.searchRecipes("Spaghetti", null, null, null);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.contains(recipe1));

    verify(recipeRepository, times(1)).findAll(any(Specification.class));
  }

  @Test
  void testSearchRecipesWithMultipleFilters() {

    User user = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();

    Ingredient ingredient1 = new Ingredient(1L, "Tomato", "2", null);
    Ingredient ingredient2 = new Ingredient(2L, "Pasta", "200g", null);
    List<Ingredient> ingredients = List.of(ingredient1, ingredient2);

    Recipe recipe1 = Recipe.builder()
        .recipeId(1L)
        .name("Spaghetti Bolognese")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("Italy")
        .difficulty(Difficulty.MEDIUM)
        .instructions("Cook the pasta and make the sauce.")
        .user(user)
        .ingredients(ingredients)
        .build();

    Recipe recipe2 = Recipe.builder()
        .recipeId(2L)
        .name("Chicken Curry")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("India")
        .difficulty(Difficulty.HARD)
        .instructions("Cook the chicken and make the curry.")
        .user(user)
        .ingredients(ingredients)
        .build();

    when(recipeRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(recipe1));

    List<Recipe> result = recipeService.searchRecipes("Spaghetti", MealType.LUNCH, "Italy", Difficulty.MEDIUM);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.contains(recipe1));
    assertFalse(result.contains(recipe2));

    verify(recipeRepository, times(1)).findAll(any(Specification.class));
  }

}
