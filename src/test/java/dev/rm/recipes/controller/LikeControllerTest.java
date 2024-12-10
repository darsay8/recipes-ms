package dev.rm.recipes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import dev.rm.recipes.config.SecurityConfig;
import dev.rm.recipes.model.Like;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.model.User;
import dev.rm.recipes.security.CustomUserDetails;
import dev.rm.recipes.security.CustomUserDetailsService;
import dev.rm.recipes.security.JwtTokenProvider;
import dev.rm.recipes.service.LikeService;
import dev.rm.recipes.service.RecipeService;
import dev.rm.recipes.service.UserService;

import java.util.Collection;
import java.util.Collections;

@WebMvcTest(LikeController.class)
@Import(SecurityConfig.class)
public class LikeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private LikeService likeService;

  @MockBean
  private UserService userService;

  @MockBean
  private RecipeService recipeService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @MockBean
  CustomUserDetailsService customUserDetailsService;

  @MockBean
  private AuthenticationManager authenticationManager;

  private User user;
  private Recipe recipe;
  private Like like;
  private String token;

  @BeforeEach
  public void setUp() {

    user = User.builder().userId(1L).email("user@example.com").password("password").build();
    recipe = Recipe.builder().recipeId(1L).build();
    like = Like.builder().likeId(1L).user(user).recipe(recipe).build();

    GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
    Collection<GrantedAuthority> authorities = Collections.singletonList(authority);

    CustomUserDetails userDetails = new CustomUserDetails(
        user.getEmail(),
        user.getUsername(),
        user.getPassword(),
        authorities);

    when(customUserDetailsService.loadUserByUsername(eq(user.getEmail()))).thenReturn(userDetails);

    // Mock the JWT token
    token = "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImphbWVzQG1haWwuY29tIiwidXNlcm5hbWUiOiJqYW1lcyIsInJvbGVzIjoiQURNSU4iLCJzdWIiOiJqYW1lc0BtYWlsLmNvbSIsImlhdCI6MTczMzU3OTUyNywiZXhwIjoxNzMzNjY1OTI3fQ.g_IcKA8hcX6KZmCkzy7cwkjDw440dhob-cwTW0q3VMI";
    when(jwtTokenProvider.validateToken(token)).thenReturn(true);
    when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(user.getEmail());

    Authentication authentication = mock(Authentication.class);
    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(user);
    when(jwtTokenProvider.generateToken(authentication)).thenReturn(token);

  }

  @Test
  public void testCreateLike() throws Exception {

    when(userService.getUserByEmail(anyString())).thenReturn(user);
    when(recipeService.getRecipeById(eq(1L))).thenReturn(recipe);
    when(likeService.getLikesByUserIdAndRecipeId(eq(1L), eq(1L))).thenReturn(Collections.emptyList());
    when(likeService.addLike(any(Like.class))).thenReturn(like);

    mockMvc.perform(post("/api/recipes/1/likes")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.likeId").value(1L));
  }

  @Test
  public void testRemoveLike() throws Exception {
    when(userService.getUserByEmail(anyString())).thenReturn(user);
    when(recipeService.getRecipeById(eq(1L))).thenReturn(recipe);
    when(likeService.getLikesByUserIdAndRecipeId(eq(1L),
        eq(1L))).thenReturn(Collections.singletonList(like));

    mockMvc.perform(post("/api/recipes/1/likes")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Like removed"));
  }

  @Test
  public void testGetLikesByRecipeId() throws Exception {
    when(likeService.getLikesByRecipeId(eq(1L))).thenReturn(Collections.singletonList(like));

    mockMvc.perform(get("/api/recipes/1/likes")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].likeId").value(1L));
  }

  @Test
  public void testGetLikeCount() throws Exception {
    when(likeService.getLikeCount(eq(1L))).thenReturn(1L);

    mockMvc.perform(get("/api/recipes/1/likes/total")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("1"));
  }
}