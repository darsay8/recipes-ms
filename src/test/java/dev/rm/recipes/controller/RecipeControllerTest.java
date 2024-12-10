package dev.rm.recipes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.rm.recipes.config.SecurityConfig;
import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.Ingredient;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.model.Role;
import dev.rm.recipes.model.User;
import dev.rm.recipes.security.CustomUserDetails;
import dev.rm.recipes.security.CustomUserDetailsService;

import dev.rm.recipes.security.JwtTokenProvider;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@WebMvcTest(RecipeController.class)
@Import(SecurityConfig.class)
public class RecipeControllerTest {

  @Autowired
  private MockMvc mockMvc;

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
  void testGetAllRecipes() throws Exception {
    List<Recipe> recipes = new ArrayList<>();
    recipes.add(recipe);

    when(recipeService.getAllRecipes()).thenReturn(recipes);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Spaghetti Carbonara"));

    verify(recipeService, times(1)).getAllRecipes();
  }

  @Test
  void testGetRecipeById() throws Exception {
    when(recipeService.getRecipeById(1L)).thenReturn(recipe);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/1")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Spaghetti Carbonara"))
        .andExpect(jsonPath("$.mealType").value("LUNCH"));

    verify(recipeService, times(1)).getRecipeById(1L);
  }

  @Test
  void testCreateRecipe() throws Exception {
    when(userService.getUserByEmail(anyString())).thenReturn(user);
    when(recipeService.createRecipe(any(Recipe.class))).thenReturn(recipe);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/recipes")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(
            "{\"name\": \"Spaghetti Carbonara\", \"mealType\": \"LUNCH\", \"ingredients\": [{\"name\": \"Spaghetti\"}, {\"name\": \"Eggs\"}, {\"name\": \"Pancetta\"}]}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Spaghetti Carbonara"))
        .andExpect(jsonPath("$.mealType").value("LUNCH"));

    verify(recipeService, times(1)).createRecipe(any(Recipe.class));
    verify(userService, times(1)).getUserByEmail("user@example.com");
  }

  @Test
  void testUpdateRecipe() throws Exception {

    Recipe updatedRecipe = Recipe.builder()
        .recipeId(recipeId)
        .user(user)
        .name("Updated Spaghetti Carbonara")
        .image("spaghetti.jpg")
        .mealType(MealType.DINNER)
        .difficulty(Difficulty.MEDIUM)
        .instructions("Boil pasta, cook bacon, mix together.")
        .ingredients(Arrays.asList(
            new Ingredient(1L, "Spaghetti", "200g", null),
            new Ingredient(2L, "Bacon", "100g", null)))
        .build();

    when(recipeService.updateRecipe(eq(1L), any(Recipe.class))).thenReturn(updatedRecipe);

    mockMvc.perform(MockMvcRequestBuilders.put("/api/recipes/1")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\": \"Updated Spaghetti Carbonara\", \"mealType\": \"DINNER\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Spaghetti Carbonara"))
        .andExpect(jsonPath("$.mealType").value("DINNER"));

    verify(recipeService, times(1)).updateRecipe(eq(1L), any(Recipe.class));
  }

  @Test
  void testDeleteRecipe() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/recipes/1")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isNoContent());

    verify(recipeService, times(1)).deleteRecipe(1L);
  }

}
