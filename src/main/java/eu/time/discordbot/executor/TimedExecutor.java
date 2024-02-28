package eu.time.discordbot.executor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class TimedExecutor {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    protected abstract void runTask();

    public final void startExecutionAt(int targetHour, int targetMin, int targetSec) {
        Runnable taskWrapper = () -> {
            runTask();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            startExecutionAt(targetHour, targetMin, targetSec);
        };
        long delay = computeNextDelay(targetHour, targetMin, targetSec);
        executorService.schedule(taskWrapper, delay, TimeUnit.SECONDS);
    }

    private long computeNextDelay(int targetHour, int targetMin, int targetSec) {
        LocalDateTime localNow = LocalDateTime.now();
        LocalDateTime nextTarget = localNow.withHour(targetHour).withMinute(targetMin).withSecond(targetSec);
        if (localNow.isAfter(nextTarget)) {
            nextTarget = nextTarget.plusDays(1);
        }
        Duration duration = Duration.between(localNow, nextTarget);
        return duration.getSeconds();
    }

    public void stop() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
        }
    }
}
