package dev.rm.recipes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.Ingredient;
import dev.rm.recipes.model.Like;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.model.Role;
import dev.rm.recipes.model.User;
import dev.rm.recipes.repository.LikeRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

  @Mock
  private LikeRepository likeRepository;

  @InjectMocks
  private LikeServiceImpl likeService;

  private Like like;
  private User user;
  private Recipe recipe;
  private Ingredient ingredient;

  @BeforeEach
  void setUp() {
    user = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();

    ingredient = new Ingredient(1L, "Tomato", "2", null);
    List<Ingredient> ingredients = List.of(ingredient);

    recipe = Recipe.builder()
        .recipeId(1L)
        .name("Spaghetti Bolognese")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("Italy")
        .difficulty(Difficulty.MEDIUM)
        .instructions("Cook the pasta and make the sauce.")
        .user(user)
        .ingredients(ingredients)
        .build();

    like = Like.builder().user(user).recipe(recipe).build();
  }

  @Test
  void testAddLike() {

    when(likeRepository.save(like)).thenReturn(like);

    Like result = likeService.addLike(like);

    assertNotNull(result, "The result should not be null");

    assertEquals(like, result, "The returned like should be the same as the input like");

    verify(likeRepository, times(1)).save(like);
  }

  @Test
  void testGetLikesByUserId() {

    when(likeRepository.findByUser_UserId(user.getUserId())).thenReturn(List.of(like));

    List<Like> result = likeService.getLikesByUserId(user.getUserId());

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.contains(like));
    verify(likeRepository, times(1)).findByUser_UserId(user.getUserId());
  }

  @Test
  void testGetLikesByRecipeId() {

    when(likeRepository.findByRecipe_RecipeId(1L)).thenReturn(List.of(like));

    List<Like> result = likeService.getLikesByRecipeId(1L);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.contains(like));
    verify(likeRepository, times(1)).findByRecipe_RecipeId(1L);
  }

  @Test
  void testGetLikesByUserIdAndRecipeId() {

    when(likeRepository.findByUser_UserIdAndRecipe_RecipeId(1L, 1L)).thenReturn(List.of(like));

    List<Like> result = likeService.getLikesByUserIdAndRecipeId(1L, 1L);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.contains(like));
    verify(likeRepository, times(1)).findByUser_UserIdAndRecipe_RecipeId(1L, 1L);
  }

  @Test
  void testGetLikeCount() {

    when(likeRepository.findByRecipe_RecipeId(1L)).thenReturn(List.of(like));

    Long result = likeService.getLikeCount(1L);

    assertNotNull(result);
    assertEquals(1, result);
    verify(likeRepository, times(1)).findByRecipe_RecipeId(1L);
  }

  @Test
  void testRemoveLike_Success() {

    when(likeRepository.findById(1L)).thenReturn(Optional.of(like));

    likeService.removeLike(1L);

    verify(likeRepository, times(1)).findById(1L);
    verify(likeRepository, times(1)).delete(like);
  }

  @Test
  void testRemoveLike_NotFound() {

    when(likeRepository.findById(1L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () -> likeService.removeLike(1L));

    assertEquals("Like with id 1 not found.", exception.getMessage());
    verify(likeRepository, times(1)).findById(1L);
    verify(likeRepository, never()).delete(any(Like.class));
  }
}
