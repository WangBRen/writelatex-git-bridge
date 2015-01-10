package uk.ac.ic.wlgitbridge.git.handler;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ServiceMayNotContinueException;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import uk.ac.ic.wlgitbridge.bridge.RepositorySource;
import uk.ac.ic.wlgitbridge.git.exception.InvalidRootDirectoryPathException;
import uk.ac.ic.wlgitbridge.util.Util;
import uk.ac.ic.wlgitbridge.writelatex.SnapshotRepositoryBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * Created by Winston on 02/11/14.
 */
public class WLRepositoryResolver implements RepositoryResolver<HttpServletRequest> {

    private File rootGitDirectory;
    private RepositorySource repositorySource;

    public WLRepositoryResolver(String rootGitDirectoryPath, SnapshotRepositoryBuilder repositorySource) throws InvalidRootDirectoryPathException {
        this.repositorySource = repositorySource;
        initRootGitDirectory(rootGitDirectoryPath);
    }

    @Override
    public Repository open(HttpServletRequest httpServletRequest, String name) throws RepositoryNotFoundException, ServiceNotAuthorizedException, ServiceNotEnabledException, ServiceMayNotContinueException {
        try {
            return repositorySource.getRepositoryWithNameAtRootDirectory(Util.removeAllSuffixes(name, "/", ".git"), rootGitDirectory);
        } catch (RepositoryNotFoundException e) {
            Util.printStackTrace(e);
            throw e;
            /*
        } catch (ServiceNotAuthorizedException e) {
            cannot occur
        } catch (ServiceNotEnabledException e) {
            cannot occur
            */
        } catch (ServiceMayNotContinueException e) { /* Such as FailedConnectionException */
            Util.printStackTrace(e);
            throw e;
        } catch (RuntimeException e) {
            Util.printStackTrace(e);
            throw new ServiceMayNotContinueException(e);
        }
    }

    private void initRootGitDirectory(String rootGitDirectoryPath) throws InvalidRootDirectoryPathException {
        rootGitDirectory = new File(rootGitDirectoryPath);
        /* throws SecurityException */
        rootGitDirectory.mkdirs();
        rootGitDirectory.getAbsolutePath();
        if (!rootGitDirectory.isDirectory()) {
            throw new InvalidRootDirectoryPathException();
        }
    }

}