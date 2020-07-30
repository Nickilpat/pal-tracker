package io.pivotal.pal.tracker;

import java.util.List;

public interface TimeEntryRepository {
    public TimeEntry create(TimeEntry any);

    public List<TimeEntry> list();

    public TimeEntry find(long timeEntryId);

    public TimeEntry update(long eq, TimeEntry any);

    public void delete(long timeEntryId);

}
