package dev.rm.recipes.service;

import java.util.List;

import dev.rm.recipes.model.Comment;

public interface CommentService {

  List<Comment> getCommentsByRecipeId(Long recipeId);

  Comment createComment(Comment comment);
}
