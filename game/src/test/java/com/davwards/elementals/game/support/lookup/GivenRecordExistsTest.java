package com.davwards.elementals.game.support.lookup;

import org.junit.Test;

import java.util.Optional;

import static com.davwards.elementals.game.support.lookup.GivenRecordExists.givenRecordExists;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

public class GivenRecordExistsTest {
    @Test
    public void whenSuppliedOptionalIsEmpty_returnsOtherwiseValue() throws Exception {
        assertThat(
                givenRecordExists(
                        Optional.empty(),
                        value -> "wrong"
                ).otherwise(() -> "correct"),
                equalTo("correct")
        );
    }

    @Test
    public void whenSuppliedOptionalIsNotEmpty_andIfPresentReturnsNonNull_returnsIfPresentResult() throws Exception {
        assertThat(
                givenRecordExists(
                        Optional.of("original"),
                        value -> "handled " + value
                ).otherwise(() -> "wrong"),
                equalTo("handled original")
        );
    }

    // This case is what makes this different than just an Optional
    @Test
    public void whenSuppliedOptionalIsNotEmpty_andIfPresentReturnsNull_returnsNull() throws Exception {
        assertThat(
                givenRecordExists(
                        Optional.of("original"),
                        value -> null
                ).otherwise(() -> "wrong"),
                nullValue()
        );
    }
}