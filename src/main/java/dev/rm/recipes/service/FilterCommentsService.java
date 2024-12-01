package dev.rm.recipes.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FilterCommentsService {
  private static final List<String> BAD_WORDS = List.of(
      "badword", "offensiveword", "curseword", "inappropriateword", "vulgarword");

  public boolean containsBadWords(String input) {
    if (input == null || input.isEmpty()) {
      return false;
    }

    log.info("=========================== Checking for bad words in comment: '{}'", input);

    String sanitizedInput = input.trim().replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();

    log.info("Sanitized comment: '{}'", sanitizedInput);

    for (String badWord : BAD_WORDS) {
      System.out.println("======================== Checking for bad word: '" + badWord + "'");

      if (sanitizedInput.contains(badWord.toLowerCase())) {
        return true;
      }
    }

    return false;
  }
}
