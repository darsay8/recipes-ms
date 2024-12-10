package dev.rm.recipes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.rm.recipes.model.Comment;
import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.Ingredient;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.model.Role;
import dev.rm.recipes.model.User;
import dev.rm.recipes.repository.CommentRepository;

@ExtendWith(MockitoExtension.class)

public class CommentServiceImplTest {

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private RecipeService recipeService;

  @Mock
  private FilterCommentsService filterCommentsService;

  @InjectMocks
  private CommentServiceImpl commentService;

  private Comment validComment;
  private Comment badComment;
  private Recipe recipe;
  private User user;
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

    validComment = Comment.builder()
        .commentId(1L)
        .content("Great recipe!")
        .recipe(recipe)
        .user(user)
        .build();

    badComment = Comment.builder()
        .commentId(2L)
        .content("badword!")
        .recipe(recipe)
        .user(user)
        .build();

  }

  @Test
  void testGetCommentsByRecipeId() {

    when(recipeService.getRecipeById(1L)).thenReturn(recipe);
    when(commentRepository.findByRecipe(recipe)).thenReturn(List.of(validComment));

    List<Comment> comments = commentService.getCommentsByRecipeId(1L);

    assertNotNull(comments);
    assertEquals(1, comments.size());
    assertTrue(comments.contains(validComment), "Comment should be present in the list");

    verify(recipeService, times(1)).getRecipeById(1L);
    verify(commentRepository, times(1)).findByRecipe(recipe);
  }

  @Test
  void testCreateComment_Success() {

    when(commentRepository.save(validComment)).thenReturn(validComment);

    Comment result = commentService.createComment(validComment);

    assertNotNull(result, "The result should not be null");
    assertEquals(validComment, result, "The returned comment should be the same as the input");

    verify(commentRepository, times(1)).save(validComment);
  }

}
