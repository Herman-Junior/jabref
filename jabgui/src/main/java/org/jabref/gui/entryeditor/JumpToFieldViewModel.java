package org.jabref.gui.entryeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.jabref.gui.AbstractViewModel;
import org.jabref.logic.util.strings.StringUtil;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.Field;
import org.jabref.model.entry.field.FieldFactory;
import org.jabref.model.entry.field.InternalField;

public class JumpToFieldViewModel extends AbstractViewModel {

    private final StringProperty searchText = new SimpleStringProperty("");
    private final EntryEditor entryEditor;

    public JumpToFieldViewModel(EntryEditor entryEditor) {
        this.entryEditor = entryEditor;
    }

    public StringProperty searchTextProperty() {
        return searchText;
    }

    public List<String> getFieldNames() {
        BibEntry entry = entryEditor.getCurrentlyEditedEntry();
        if (entry == null) {
            return List.of();
        }

        return suggestedFields(entry).stream()
                                     .map(Field::getName)
                                     .distinct()
                                     .sorted()
                                     .toList();
    }

    /// The field names offered as suggestions for `typedText`.
    public List<String> getSuggestions(String typedText) {
        return getSuggestions(typedText, getFieldNames());
    }

    /// `true` when confirming `typedText` would add a field the entry editor does not offer,
    /// that is: a custom field that does not exist yet.
    ///
    /// While the popup offers a suggestion, confirming completes to that suggestion rather than
    /// to the typed text, so no new field is created and the hint must stay hidden.
    public boolean willCreateNewField(String typedText) {
        return willCreateNewField(typedText, getFieldNames());
    }

    /// Prefix matching rather than substring matching: the popup always preselects its first
    /// suggestion, so "file" would otherwise offer (and jump to) "dayfiled" first.
    static List<String> getSuggestions(String typedText, List<String> fieldNames) {
        String prefix = normalize(typedText);
        if (prefix.isEmpty()) {
            return List.of();
        }
        return fieldNames.stream()
                         .filter(fieldName -> fieldName.toLowerCase(Locale.ROOT).startsWith(prefix))
                         .toList();
    }

    /// A field name that no existing field starts with cannot be completed, so confirming it
    /// creates that field.
    static boolean willCreateNewField(String typedText, List<String> fieldNames) {
        return !normalize(typedText).isEmpty() && getSuggestions(typedText, fieldNames).isEmpty();
    }

    private static String normalize(String text) {
        return StringUtil.isBlank(text) ? "" : text.trim().toLowerCase(Locale.ROOT);
    }

    private List<Field> suggestedFields(BibEntry entry) {
        List<Field> suggestedFields = new ArrayList<>();
        suggestedFields.add(InternalField.KEY_FIELD);
        suggestedFields.addAll(entry.getFields());
        suggestedFields.addAll(FieldFactory.getAllFieldsWithOutInternal());
        return suggestedFields;
    }
}
