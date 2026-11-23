package org.projectharvester.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class ProviderUrlTest {
    @Test
    public void detectsGithubAsDownloadableProvider() {
        ProviderUrl value = ProviderUrl.parse("https://github.com/owner/project");
        assertEquals(ProviderUrl.Provider.GITHUB, value.provider);
        assertTrue(value.supportsArchiveDownload());
        assertEquals("owner", value.owner);
        assertEquals("project", value.project);
    }

    @Test
    public void detectsGitlabNestedNamespaceAsDownloadableProvider() {
        ProviderUrl value = ProviderUrl.parse("https://gitlab.com/group/subgroup/project.git");
        assertEquals(ProviderUrl.Provider.GITLAB, value.provider);
        assertTrue(value.supportsArchiveDownload());
        assertEquals("group/subgroup", value.namespace);
        assertEquals("project", value.project);
    }

    @Test
    public void classifiesHacksterAsReferenceOnly() {
        ProviderUrl value = ProviderUrl.parse("https://www.hackster.io/maker/sensor-project");
        assertEquals(ProviderUrl.Provider.HACKSTER, value.provider);
        assertFalse(value.supportsArchiveDownload());
    }

    @Test
    public void classifiesGitbookInstructablesAndNatureAsReferenceOnly() {
        assertEquals(ProviderUrl.Provider.GITBOOK,
                ProviderUrl.parse("https://docs.example.gitbook.io/guide").provider);
        assertEquals(ProviderUrl.Provider.INSTRUCTABLES,
                ProviderUrl.parse("https://www.instructables.com/example-project/").provider);
        assertEquals(ProviderUrl.Provider.NATURE,
                ProviderUrl.parse("https://www.nature.com/articles/example").provider);
    }

    @Test
    public void buildsProviderSpecificArchiveUrls() {
        ProviderUrl gitlab = ProviderUrl.parse("https://gitlab.com/group/subgroup/project");
        assertEquals("https://gitlab.com/api/v4/projects/group%2Fsubgroup%2Fproject/repository/archive.zip?sha=feature%2Fnew-ui",
                gitlab.archiveUrl("feature/new-ui"));
        ProviderUrl github = ProviderUrl.parse("https://github.com/owner/project");
        assertEquals("https://codeload.github.com/owner/project/zip/refs/heads/feature/new-ui",
                github.archiveUrl("feature/new-ui"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsQueryParametersThatCouldChangeSourceMeaning() {
        ProviderUrl.parse("https://github.com/owner/project?tab=readme");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNestedRepositoryPagesAsProjectRoots() {
        ProviderUrl.parse("https://gitlab.com/group/project/-/tree/main");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsCredentialBearingUrls() {
        ProviderUrl.parse("https://user:pass@github.com/owner/project");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsUnsupportedProviders() {
        ProviderUrl.parse("https://unknown.example/owner/project");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsInsecureProviderUrls() {
        ProviderUrl.parse("http://gitlab.com/group/project");
    }
}
