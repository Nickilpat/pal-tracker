package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.micrometer.core.instrument.Counter;

import java.util.List;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private TimeEntryRepository timeEntryRepository;
    private Counter actionCounter;
    private DistributionSummary distributionSummary;

    public TimeEntryController(TimeEntryRepository timeEntryRepository, MeterRegistry meterRegistry) {
        this.timeEntryRepository = timeEntryRepository;

        actionCounter = meterRegistry.counter("timeEntry.actionCounter");
        distributionSummary = meterRegistry.summary("timeEntry.summary");
    }
    @PostMapping
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntryToCreate) {
        TimeEntry timeEntryCreated = timeEntryRepository.create(timeEntryToCreate);
        actionCounter.increment();
        distributionSummary.record(timeEntryRepository.list().size());
        return new ResponseEntity<>(timeEntryCreated , HttpStatus.CREATED);
    }
    @GetMapping("/{timeEntryId}")
    public ResponseEntity<TimeEntry> read(@PathVariable long timeEntryId) {
        TimeEntry found = timeEntryRepository.find(timeEntryId);
        if (found != null) {
            actionCounter.increment();
            return new ResponseEntity<>(found, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        actionCounter.increment();
        return new ResponseEntity<>(timeEntryRepository.list(), HttpStatus.OK);
    }

    @PutMapping("/{timeEntryId}")
    public ResponseEntity update(@PathVariable long timeEntryId, @RequestBody TimeEntry timeEntry) {
        TimeEntry updated = timeEntryRepository.update(timeEntryId, timeEntry);
        HttpStatus httpStatus;
        if ( updated != null ) {
            actionCounter.increment();
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(updated, httpStatus);
    }

    @DeleteMapping("/{timeEntryId}")
    public ResponseEntity delete(@PathVariable long timeEntryId) {
        timeEntryRepository.delete(timeEntryId);
        actionCounter.increment();
        distributionSummary.record(timeEntryRepository.list().size());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
