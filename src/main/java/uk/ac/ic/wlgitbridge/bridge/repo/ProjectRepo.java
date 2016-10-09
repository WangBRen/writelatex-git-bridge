package uk.ac.ic.wlgitbridge.bridge.repo;

import uk.ac.ic.wlgitbridge.data.filestore.GitDirectoryContents;
import uk.ac.ic.wlgitbridge.data.filestore.RawFile;
import uk.ac.ic.wlgitbridge.git.exception.SizeLimitExceededException;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Created by winston on 20/08/2016.
 */
public interface ProjectRepo {

    String getProjectName();

    void initRepo(
            RepoStore repoStore
    ) throws IOException;

    void useExistingRepository(
            RepoStore repoStore
    ) throws IOException;

    Map<String, RawFile> getFiles(
    ) throws IOException, SizeLimitExceededException;

    Collection<String> commitAndGetMissing(
            GitDirectoryContents gitDirectoryContents
    ) throws IOException;

}
