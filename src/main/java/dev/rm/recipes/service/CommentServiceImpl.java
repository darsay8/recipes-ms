package dev.rm.recipes.service;

import org.springframework.stereotype.Service;

import dev.rm.recipes.exception.BadWordException;
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
  private final FilterCommentsService filterCommentsService;

  public CommentServiceImpl(CommentRepository commentRepository,
      RecipeService recipeService, FilterCommentsService filterCommentsService) {
    this.commentRepository = commentRepository;
    this.recipeService = recipeService;
    this.filterCommentsService = new FilterCommentsService();
  }

  @Override
  public List<Comment> getCommentsByRecipeId(Long recipeId) {
    Recipe recipe = recipeService.getRecipeById(recipeId);
    return commentRepository.findByRecipe(recipe);
  }

  @Override
  public Comment createComment(Comment comment) {
    if (filterCommentsService.containsBadWords(comment.getContent())) {
      log.warn("Comment contains bad words: {}", comment.getContent());
      throw new BadWordException("Comment contains bad words");
    }

    return commentRepository.save(comment);
  }
}
