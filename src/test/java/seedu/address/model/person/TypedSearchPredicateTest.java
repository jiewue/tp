package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class TypedSearchPredicateTest {
    private final Person contact = new PersonBuilder().withName("Alex Tan").withTags("Hackathon", "frontend")
            .withNote("Looking for a React teammate").build().toBuilder()
            .withMeeting(new Meeting("NUS Hack Singapore", null, null)).build();

    @Test
    public void test_sameFieldTermsUseOrAndDifferentFieldsUseAnd() {
        assertTrue(new TypedSearchPredicate(List.of("sam", "AL"), List.of("hack"), List.of("react"),
                List.of("singapore")).test(contact));
        assertFalse(new TypedSearchPredicate(List.of("alex"), List.of("backend"), List.of(), List.of())
                .test(contact));
        assertFalse(new TypedSearchPredicate(List.of("sam"), List.of(), List.of(), List.of()).test(contact));
        assertFalse(new TypedSearchPredicate(List.of("alex"), List.of(), List.of("backend"), List.of())
                .test(contact));
        assertFalse(new TypedSearchPredicate(List.of("alex"), List.of(), List.of(), List.of("conference"))
                .test(contact));
    }

    @Test
    public void test_tagOnlyAndNoteOnlySearches_matchPartialCaseInsensitiveText() {
        assertTrue(new TypedSearchPredicate(List.of(), List.of("FRONT"), List.of(), List.of()).test(contact));
        assertTrue(new TypedSearchPredicate(List.of(), List.of(), List.of("REACT"), List.of()).test(contact));
        assertTrue(new TypedSearchPredicate(List.of("alex  tan"), List.of(), List.of(), List.of()).test(contact));
    }

    @Test
    public void test_missingOptionalFieldsDoNotMatchRequestedTerms() {
        Person withoutOptionalFields = new PersonBuilder().withName("Alex Tan").build();

        assertFalse(new TypedSearchPredicate(List.of(), List.of(), List.of("react"), List.of())
                .test(withoutOptionalFields));
        assertFalse(new TypedSearchPredicate(List.of(), List.of(), List.of(), List.of("hack"))
                .test(withoutOptionalFields));
    }

    @Test
    public void equals_andHashCode() {
        TypedSearchPredicate predicate = new TypedSearchPredicate(List.of("alex"), List.of("hack"),
                List.of("react"), List.of("singapore"));
        TypedSearchPredicate samePredicate = new TypedSearchPredicate(List.of("alex"), List.of("hack"),
                List.of("react"), List.of("singapore"));

        assertEquals(predicate, predicate);
        assertEquals(predicate, samePredicate);
        assertEquals(predicate.hashCode(), samePredicate.hashCode());
        assertNotEquals(predicate, new TypedSearchPredicate(List.of("sam"), List.of("hack"),
                List.of("react"), List.of("singapore")));
        assertNotEquals(predicate, new TypedSearchPredicate(List.of("alex"), List.of("frontend"),
                List.of("react"), List.of("singapore")));
        assertNotEquals(predicate, new TypedSearchPredicate(List.of("alex"), List.of("hack"),
                List.of("other note"), List.of("singapore")));
        assertNotEquals(predicate, new TypedSearchPredicate(List.of("alex"), List.of("hack"),
                List.of("react"), List.of("other event")));
        assertNotEquals(predicate, null);
        assertNotEquals(predicate, "search");
    }

    @Test
    public void toString_hidesPersonalNoteAndEventTerms() {
        TypedSearchPredicate predicate = new TypedSearchPredicate(List.of("Alex"), List.of("Hack"),
                List.of("private note"), List.of("private event"));

        assertEquals(TypedSearchPredicate.class.getCanonicalName()
                + "{nameTerms=[alex], tagTerms=[hack], noteTerms=[hidden], eventTerms=[hidden]}",
                predicate.toString());
    }

    @Test
    public void toString_emptyPersonalTermsAreNotRedacted() {
        TypedSearchPredicate predicate = new TypedSearchPredicate(List.of("Alex"), List.of("Hack"),
                List.of(), List.of());

        assertEquals(TypedSearchPredicate.class.getCanonicalName()
                + "{nameTerms=[alex], tagTerms=[hack], noteTerms=[], eventTerms=[]}", predicate.toString());
    }
}
