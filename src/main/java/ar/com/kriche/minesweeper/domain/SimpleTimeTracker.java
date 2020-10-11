package ar.com.kriche.minesweeper.domain;

import javax.persistence.*;

@Entity
@Table(name = "simple_time_tracker")
public class SimpleTimeTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private long elapsedTimeMilliseconds;
    private Long lastStartedTimeMilliseconds;


    public void start() {
        if (getLastStartedTimeMilliseconds() == null) {
            setLastStartedTimeMilliseconds(System.currentTimeMillis());
        }
    }

    public void stop() {
        if (getLastStartedTimeMilliseconds() != null) {
            setElapsedTimeMilliseconds(getAccumulatedTimeMilliseconds());
            setLastStartedTimeMilliseconds(null);
        }
    }

    public long getAccumulatedTimeMilliseconds() {
        long currentDiff;
        if (getLastStartedTimeMilliseconds() != null) {
            currentDiff = System.currentTimeMillis() - getLastStartedTimeMilliseconds();
        } else {
            currentDiff = 0;
        }

        return getElapsedTimeMilliseconds() + currentDiff;
    }

    public long getElapsedTimeMilliseconds() {
        return elapsedTimeMilliseconds;
    }

    public void setElapsedTimeMilliseconds(long elapsedTimeMilliseconds) {
        this.elapsedTimeMilliseconds = elapsedTimeMilliseconds;
    }

    public Long getLastStartedTimeMilliseconds() {
        return lastStartedTimeMilliseconds;
    }

    public void setLastStartedTimeMilliseconds(Long lastStartedTimeMilliseconds) {
        this.lastStartedTimeMilliseconds = lastStartedTimeMilliseconds;
    }

}
