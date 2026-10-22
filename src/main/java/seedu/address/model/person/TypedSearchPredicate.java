package seedu.address.model.person;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Matches typed search terms across contact fields.
 * Terms within one field use OR; terms from different fields use AND.
 */
public final class TypedSearchPredicate implements Predicate<Person> {
    private final List<String> nameTerms;
    private final List<String> tagTerms;
    private final List<String> noteTerms;
    private final List<String> eventTerms;
    private final EventContainsTermsPredicate eventPredicate;

    /** Creates a predicate from the typed terms for each searchable contact field. */
    public TypedSearchPredicate(List<String> nameTerms, List<String> tagTerms, List<String> noteTerms,
            List<String> eventTerms) {
        this.nameTerms = normalise(nameTerms);
        this.tagTerms = normalise(tagTerms);
        this.noteTerms = normalise(noteTerms);
        this.eventTerms = normalise(eventTerms);
        eventPredicate = new EventContainsTermsPredicate(this.eventTerms);
    }

    @Override
    public boolean test(Person person) {
        return matchesAny(nameTerms, person.getName().fullName)
                && matchesAnyTag(person)
                && matchesAny(noteTerms, person.getNote().value)
                && matchesEvent(person);
    }

    private boolean matchesAnyTag(Person person) {
        return tagTerms.isEmpty() || person.getTags().stream()
                .anyMatch(tag -> matchesAny(tagTerms, tag.tagName));
    }

    private boolean matchesEvent(Person person) {
        return eventTerms.isEmpty() || eventPredicate.test(person);
    }

    private static boolean matchesAny(List<String> terms, String text) {
        String normalisedText = normalise(text);
        return terms.isEmpty() || terms.stream().anyMatch(normalisedText::contains);
    }

    private static List<String> normalise(List<String> terms) {
        return terms.stream().map(TypedSearchPredicate::normalise).toList();
    }

    private static String normalise(String text) {
        return text.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TypedSearchPredicate predicate)) {
            return false;
        }
        return nameTerms.equals(predicate.nameTerms)
                && tagTerms.equals(predicate.tagTerms)
                && noteTerms.equals(predicate.noteTerms)
                && eventTerms.equals(predicate.eventTerms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameTerms, tagTerms, noteTerms, eventTerms);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameTerms", nameTerms)
                .add("tagTerms", tagTerms)
                .add("noteTerms", redact(noteTerms))
                .add("eventTerms", redact(eventTerms))
                .toString();
    }

    private static String redact(List<String> terms) {
        return terms.isEmpty() ? "[]" : "[hidden]";
    }
}
