package org.projectharvester.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class GitHubRepositoryUrlTest {
    @Test
    public void parsesRepositoryRoot() {
        GitHubRepositoryUrl value =
                GitHubRepositoryUrl.parse("https://github.com/example/project");
        assertEquals("example", value.owner);
        assertEquals("project", value.repository);
    }

    @Test
    public void acceptsGitSuffix() {
        GitHubRepositoryUrl value =
                GitHubRepositoryUrl.parse("https://github.com/example/project.git");
        assertEquals("project", value.repository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNonGithubHost() {
        GitHubRepositoryUrl.parse("https://example.com/example/project");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNestedPath() {
        GitHubRepositoryUrl.parse("https://github.com/example/project/tree/main");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsHttp() {
        GitHubRepositoryUrl.parse("http://github.com/example/project");
    }
}
