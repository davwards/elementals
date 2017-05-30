package com.davwards.elementals.biweekly;

import biweekly.ICalVersion;
import biweekly.io.ParseContext;
import biweekly.io.scribe.property.RecurrenceRuleScribe;
import biweekly.parameter.ICalParameters;
import biweekly.property.RecurrenceRule;
import biweekly.util.Recurrence;
import biweekly.util.com.google.ical.compat.javautil.DateIterator;
import org.junit.Test;

import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class BiweeklyTest {
    @Test
    public void doesBiweeklyWork() throws Exception {
        RecurrenceRuleScribe scribe = new RecurrenceRuleScribe();
        ParseContext context = new ParseContext();
        context.setVersion(ICalVersion.V2_0);
        RecurrenceRule rrule = scribe.parseText("FREQ=WEEKLY;INTERVAL=2", null, new ICalParameters(), context);

        Date startDate = new Date();
        DateIterator it = rrule.getDateIterator(startDate, TimeZone.getDefault());

        it.next();
        assertThat(it.next(), greaterThan(startDate));
    }
}
