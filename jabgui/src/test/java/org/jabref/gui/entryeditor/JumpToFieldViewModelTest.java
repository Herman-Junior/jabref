package org.jabref.gui.entryeditor;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JumpToFieldViewModelTest {

    private static final List<String> FIELDS =
            List.of("author", "timestamp", "title", "titleaddon", "translator", "type");

    @ParameterizedTest
    @CsvSource({
            "t,     false",   // "title" is suggested, so confirming completes to it
            "tit,   false",
            "title, false",   // exact match on an existing field
            "TITLE, false",   // matching is case insensitive
            "test,  true",    // nothing starts with "test"
            "'',    false",   // nothing typed yet
            "'  ',  false"    // whitespace only
    })
    void hintShowsOnlyWhenNoSuggestionExists(String typedText, boolean expected) {
        assertEquals(expected, JumpToFieldViewModel.willCreateNewField(typedText, FIELDS));
    }

    @Test
    void suggestionsAreMatchedByPrefixNotSubstring() {
        assertEquals(List.of("title", "titleaddon"),
                JumpToFieldViewModel.getSuggestions("titl", FIELDS));
    }

    @Test
    void suggestionsIgnoreSurroundingWhitespace() {
        assertEquals(List.of("author"), JumpToFieldViewModel.getSuggestions("  auth ", FIELDS));
    }

    @Test
    void noSuggestionsForUnknownPrefix() {
        assertTrue(JumpToFieldViewModel.getSuggestions("zzz", FIELDS).isEmpty());
    }

    @Test
    void hintIsSuppressedForAFieldThatOnlyMatchesAsSubstring() {
        // "ime" occurs inside "timestamp" but is not a prefix, so a new field would be created
        assertTrue(JumpToFieldViewModel.willCreateNewField("ime", FIELDS));
    }

    @Test
    void noSuggestionsWhenTheEntryOffersNoFields() {
        assertTrue(JumpToFieldViewModel.getSuggestions("title", List.of()).isEmpty());
    }
}
