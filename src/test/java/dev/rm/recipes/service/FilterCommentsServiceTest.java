package dev.rm.recipes.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FilterCommentsServiceTest {

  private FilterCommentsService filterCommentsService;

  @BeforeEach
  void setUp() {
    filterCommentsService = new FilterCommentsService();
  }

  @Test
  void testContainsBadWords_WithBadWord() {
    String badComment = "This comment contains badword.";

    assertTrue(filterCommentsService.containsBadWords(badComment));
  }

  @Test
  void testContainsBadWords_WithOffensiveWord() {
    String offensiveComment = "This is an offensiveword in the comment.";

    assertTrue(filterCommentsService.containsBadWords(offensiveComment));
  }

  @Test
  void testContainsBadWords_EmptyString() {
    String emptyComment = "";

    assertFalse(filterCommentsService.containsBadWords(emptyComment));
  }

  @Test
  void testContainsBadWords_NullInput() {

    assertFalse(filterCommentsService.containsBadWords(null));
  }

  @Test
  void testContainsBadWords_SanitizesPunctuation() {
    String commentWithPunctuation = "This comment contains badword! Isn't that offensive?";

    assertTrue(filterCommentsService.containsBadWords(commentWithPunctuation));
  }

  @Test
  void testContainsBadWords_CaseInsensitive() {
    String commentInUppercase = "This comment contains OFFENSIVEWORD.";
    String commentInMixedCase = "This comment contains OfFeNsIvEwOrD.";

    assertTrue(filterCommentsService.containsBadWords(commentInUppercase));
    assertTrue(filterCommentsService.containsBadWords(commentInMixedCase));
  }

  @Test
  void testContainsBadWords_WithSubstring() {
    String commentWithBadWordAsSubstring = "This comment contains badwordly.";

    assertTrue(filterCommentsService.containsBadWords(commentWithBadWordAsSubstring));
  }

  @Test
  void testContainsBadWords_NoBadWords() {
    String cleanComment = "This is a totally clean comment with no bad words.";

    assertFalse(filterCommentsService.containsBadWords(cleanComment));
  }
}
