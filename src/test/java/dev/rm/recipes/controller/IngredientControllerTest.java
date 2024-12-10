package dev.rm.recipes.controller;

import static org.mockito.ArgumentMatchers.any;

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

import dev.rm.recipes.service.IngredientService;
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

@WebMvcTest(IngredientController.class)
@Import(SecurityConfig.class)
public class IngredientControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  IngredientService ingredientService;

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
  private Ingredient ingredient;
  private Ingredient ingredient2;

  @BeforeEach
  void setUp() {

    user = User.builder()
        .userId(1L)
        .username("user")
        .email("user@example.com")
        .password("password")
        .role(Role.USER)
        .build();

    ingredient = Ingredient.builder()
        .ingredientId(1L)
        .name("Tomato")
        .quantity("200g")
        .build();

    ingredient2 = Ingredient.builder()
        .ingredientId(2L)
        .name("Bacon")
        .quantity("100g")
        .build();

    recipe = Recipe.builder()
        .recipeId(1L)
        .user(user)
        .name("Spaghetti Carbonara")
        .image("spaghetti.jpg")
        .mealType(MealType.LUNCH)
        .difficulty(Difficulty.MEDIUM)
        .instructions("Boil pasta, cook bacon, mix together.")
        .ingredients(Arrays.asList(ingredient, ingredient2))
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
  void testCreateIngredient() throws Exception {

    when(ingredientService.createIngredient(any(Ingredient.class))).thenReturn(ingredient);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/ingredients")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\": \"Tomato\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Tomato"));

    verify(ingredientService, times(1)).createIngredient(any(Ingredient.class));
  }

  @Test
  void testGetIngredientById() throws Exception {

    when(ingredientService.getIngredientById(1L)).thenReturn(ingredient);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/ingredients/1")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Tomato"));

    verify(ingredientService, times(1)).getIngredientById(1L);
  }

  @Test
  void testGetAllIngredients() throws Exception {

    List<Ingredient> ingredients = Arrays.asList(ingredient, ingredient2);

    when(ingredientService.getAllIngredients()).thenReturn(ingredients);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/ingredients")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value("Tomato"))
        .andExpect(jsonPath("$[1].name").value("Bacon"));

    verify(ingredientService, times(1)).getAllIngredients();
  }

  @Test
  void testUpdateIngredient() throws Exception {

    Ingredient updatedIngredient = Ingredient.builder()
        .ingredientId(1L)
        .name("Cherry Tomato")
        .quantity("200g")
        .build();

    when(ingredientService.updateIngredient(eq(1L), any(Ingredient.class))).thenReturn(updatedIngredient);

    mockMvc.perform(MockMvcRequestBuilders.put("/api/ingredients/1")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\": \"Cherry Tomato\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Cherry Tomato"));

    verify(ingredientService, times(1)).updateIngredient(eq(1L), any(Ingredient.class));
  }

  @Test
  void testDeleteIngredient() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/ingredients/1")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(ingredientService, times(1)).deleteIngredient(1L);
  }

}
