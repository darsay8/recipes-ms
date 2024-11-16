package dev.rm.recipes.service;

import org.springframework.stereotype.Service;

import dev.rm.recipes.model.Comment;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final RecipeService recipeService;

  public CommentServiceImpl(CommentRepository commentRepository,
      RecipeService recipeService) {
    this.commentRepository = commentRepository;
    this.recipeService = recipeService;
  }

  @Override
  public List<Comment> getCommentsByRecipeId(Long recipeId) {
    Recipe recipe = recipeService.getRecipeById(recipeId);
    return commentRepository.findByRecipe(recipe);
  }

  @Override
  public Comment createComment(Comment comment) {
    return commentRepository.save(comment);
  }
}
