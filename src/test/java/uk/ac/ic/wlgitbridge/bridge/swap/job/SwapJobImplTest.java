package uk.ac.ic.wlgitbridge.bridge.swap.job;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ic.wlgitbridge.bridge.db.DBStore;
import uk.ac.ic.wlgitbridge.bridge.db.sqlite.SqliteDBStore;
import uk.ac.ic.wlgitbridge.bridge.lock.ProjectLock;
import uk.ac.ic.wlgitbridge.bridge.repo.FSGitRepoStore;
import uk.ac.ic.wlgitbridge.bridge.repo.FSGitRepoStoreTest;
import uk.ac.ic.wlgitbridge.bridge.repo.RepoStore;
import uk.ac.ic.wlgitbridge.bridge.swap.store.InMemorySwapStore;
import uk.ac.ic.wlgitbridge.bridge.swap.store.SwapStore;
import uk.ac.ic.wlgitbridge.data.ProjectLockImpl;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by winston on 20/08/2016.
 */
public class SwapJobImplTest {

    private SwapJobImpl swapJob;

    private ProjectLock lock;
    private RepoStore repoStore;
    private DBStore dbStore;
    private SwapStore swapStore;

    @Before
    public void setup() throws IOException {
        TemporaryFolder tmpFolder = new TemporaryFolder();
        tmpFolder.create();
        lock = new ProjectLockImpl();
        repoStore = new FSGitRepoStore(
                FSGitRepoStoreTest.makeTempRepoDir(
                        tmpFolder,
                        "repostore"
                ).getAbsolutePath()
        );
        dbStore = new SqliteDBStore(tmpFolder.newFile());
        dbStore.setLatestVersionForProject("proj1", 0);
        dbStore.setLatestVersionForProject("proj2", 0);
        dbStore.setLastAccessedTime(
                "proj1",
                Timestamp.valueOf(LocalDateTime.now())
        );
        dbStore.setLastAccessedTime(
                "proj2",
                Timestamp.valueOf(
                        LocalDateTime.now().minus(1, ChronoUnit.SECONDS)
                )
        );
        swapStore = new InMemorySwapStore();
        swapJob = new SwapJobImpl(
                1,
                15000,
                30000,
                Duration.ofMillis(100),
                lock,
                repoStore,
                dbStore,
                swapStore
        );
    }

    @Test
    public void startingTimerAlwaysCausesASwap() {
        swapJob.lowWatermarkBytes = 16384;
        swapJob.interval = Duration.ofHours(1);
        assertEquals(0, swapJob.swaps.get());
        swapJob.start();
        while (swapJob.swaps.get() <= 0);
        assertTrue(swapJob.swaps.get() > 0);
    }

    @Test
    public void swapsHappenEveryInterval() {
        swapJob.lowWatermarkBytes = 16384;
        assertEquals(0, swapJob.swaps.get());
        swapJob.start();
        while (swapJob.swaps.get() <= 1);
        assertTrue(swapJob.swaps.get() > 1);
    }

    @Test
    public void noProjectsGetSwappedWhenUnderHighWatermark() {
        swapJob.highWatermarkBytes = 65536;
        assertEquals(2, dbStore.getNumUnswappedProjects());
        swapJob.start();
        while (swapJob.swaps.get() < 1);
        assertEquals(2, dbStore.getNumUnswappedProjects());
    }

    @Test
    public void correctProjGetSwappedWhenOverHighWatermark(
    ) throws IOException {
        swapJob.lowWatermarkBytes = 16384;
        assertEquals(2, dbStore.getNumUnswappedProjects());
        assertEquals("proj2", dbStore.getOldestUnswappedProject());
        swapJob.start();
        while (swapJob.swaps.get() < 1);
        assertEquals(1, dbStore.getNumUnswappedProjects());
        assertEquals("proj1", dbStore.getOldestUnswappedProject());
        swapJob.restore("proj2");
        int numSwaps = swapJob.swaps.get();
        while (swapJob.swaps.get() <= numSwaps);
        assertEquals(1, dbStore.getNumUnswappedProjects());
        assertEquals("proj2", dbStore.getOldestUnswappedProject());
    }

}