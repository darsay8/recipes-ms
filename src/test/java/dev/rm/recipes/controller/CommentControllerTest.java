package dev.rm.recipes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.rm.recipes.config.SecurityConfig;

import dev.rm.recipes.model.Comment;
import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.Ingredient;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.model.Role;
import dev.rm.recipes.model.User;
import dev.rm.recipes.security.CustomUserDetails;
import dev.rm.recipes.security.CustomUserDetailsService;

import dev.rm.recipes.security.JwtTokenProvider;
import dev.rm.recipes.service.CommentService;
import dev.rm.recipes.service.RecipeService;
import dev.rm.recipes.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@WebMvcTest(CommentController.class)
@Import(SecurityConfig.class)
public class CommentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  CommentService commentService;

  @MockBean
  private RecipeService recipeService;

  @MockBean
  private UserService userService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @MockBean
  CustomUserDetailsService customUserDetailsService;

  @MockBean
  private AuthenticationManager authenticationManager;

  private String token;
  private Recipe recipe;
  private User user;
  private Long recipeId;
  private Comment comment;
  private Comment comment2;

  @BeforeEach
  void setUp() {

    user = User.builder()
        .userId(1L)
        .username("user")
        .email("user@example.com")
        .password("password")
        .role(Role.USER)
        .build();

    recipeId = 1L;
    recipe = Recipe.builder()
        .recipeId(recipeId)
        .user(user)
        .name("Spaghetti Carbonara")
        .image("spaghetti.jpg")
        .mealType(MealType.LUNCH)
        .difficulty(Difficulty.MEDIUM)
        .instructions("Boil pasta, cook bacon, mix together.")
        .ingredients(Arrays.asList(
            new Ingredient(1L, "Spaghetti", "200g", null),
            new Ingredient(2L, "Bacon", "100g", null)))
        .build();

    comment = Comment.builder()
        .commentId(1L)
        .user(user)
        .recipe(recipe)
        .content("Great recipe!")
        .build();

    comment2 = Comment.builder()
        .commentId(2L)
        .user(user)
        .recipe(recipe)
        .content("Love it!")
        .build();

    GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
    Collection<GrantedAuthority> authorities = Collections.singletonList(authority);

    CustomUserDetails userDetails = new CustomUserDetails(
        user.getEmail(),
        user.getUsername(),
        user.getPassword(),
        authorities);

    when(customUserDetailsService.loadUserByUsername(eq(user.getEmail()))).thenReturn(userDetails);

    token = "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImphbWVzQG1haWwuY29tIiwidXNlcm5hbWUiOiJqYW1lcyIsInJvbGVzIjoiQURNSU4iLCJzdWIiOiJqYW1lc0BtYWlsLmNvbSIsImlhdCI6MTczMzU3OTUyNywiZXhwIjoxNzMzNjY1OTI3fQ.g_IcKA8hcX6KZmCkzy7cwkjDw440dhob-cwTW0q3VMI";
    when(jwtTokenProvider.validateToken(token)).thenReturn(true);
    when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(user.getEmail());

    Authentication authentication = mock(Authentication.class);
    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(user);
    when(jwtTokenProvider.generateToken(authentication)).thenReturn(token);

  }

  @Test
  void testGetCommentsByRecipeId() throws Exception {

    List<Comment> comments = Arrays.asList(comment, comment2);
    when(commentService.getCommentsByRecipeId(1L)).thenReturn(comments);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/1/comments")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].content").value("Great recipe!"))
        .andExpect(jsonPath("$[1].content").value("Love it!"));

    verify(commentService, times(1)).getCommentsByRecipeId(1L);
  }

  @Test
  void testCreateComment() throws Exception {

    when(userService.getUserByEmail(anyString())).thenReturn(user);
    when(recipeService.getRecipeById(1L)).thenReturn(recipe);
    when(commentService.createComment(any(Comment.class))).thenReturn(comment);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/recipes/1/comments")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"content\": \"Amazing recipe!\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("Great recipe!"));

    verify(commentService, times(1)).createComment(any(Comment.class));
    verify(userService, times(1)).getUserByEmail("user@example.com");
    verify(recipeService, times(1)).getRecipeById(1L);
  }

}
