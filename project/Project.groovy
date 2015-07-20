package project

/*
Global, project wide data stored here
*/

class Project {
    //
    String name
    String namePrefix
    String artifactoryServerId
    String artifactoryRepositoryKey
    String artifactorySnapshotsRepositoryKey
    String artifactoryServerUrl
    //
    def static builder() {
        return new ProjectBuilder();
    }
    //
    static class ProjectBuilder {

        Project _instance;

        ProjectBuilder() {
            _instance = new Project();
        }

        def name(String name) {
            _instance.name = name
            return this;
        }

        def namePrefix(String namePrefix) {
            _instance.namePrefix = namePrefix
            return this;
        }

        def artifactoryServerId(String artifactoryServerId) {
            _instance.artifactoryServerId = artifactoryServerId
            return this;
        }

        def artifactoryRepositoryKey(String artifactoryRepositoryKey) {
            _instance.artifactoryRepositoryKey = artifactoryRepositoryKey
            return this;
        }

        def artifactorySnapshotsRepositoryKey(String artifactorySnapshotsRepositoryKey) {
            _instance.artifactorySnapshotsRepositoryKey = artifactorySnapshotsRepositoryKey
            return this;
        }

        def artifactoryServerUrl(String artifactoryServerUrl) {
            _instance.artifactoryServerUrl = artifactoryServerUrl
            return this;
        }

        def build() {
            return _instance;
        }
    }
}
