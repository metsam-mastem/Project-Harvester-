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
