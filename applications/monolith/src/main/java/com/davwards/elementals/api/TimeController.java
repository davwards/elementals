package com.davwards.elementals.api;

import com.davwards.elementals.support.scheduling.ManualTimeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@Profile("controlled-time")
public class TimeController {

    private final ManualTimeProvider timeProvider;

    @Autowired
    public TimeController(ManualTimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @RequestMapping(value = "/test/time", method = RequestMethod.GET)
    public @ResponseBody String getCurrentTime() {
        return timeProvider.currentTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @RequestMapping(value = "/test/time", method = RequestMethod.PUT)
    public @ResponseBody String setCurrentTime(@RequestBody String time) {
        timeProvider.setCurrentTime(LocalDateTime.parse(time));
        return "Okay, time is now " + timeProvider.currentTime();
    }
}
