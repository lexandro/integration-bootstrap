package environment


class Environment {

    String namePrefix
    String serverUrl
    String serverCredentials
    //
    def static builder() {
        return new EnvironmentBuilder();
    }
    //
    static class EnvironmentBuilder {

        Environment _instance;

        EnvironmentBuilder() {
            _instance = new Environment();
        }

        def namePrefix(String namePrefix) {
            _instance.namePrefix = namePrefix
            return this;
        }

        def serverUrl(String serverUrl) {
            _instance.serverUrl = serverUrl
            return this;
        }

        def serverCredentials(String serverCredentials) {
            _instance.serverCredentials = serverCredentials
            return this;
        }

        def build() {
            return _instance;
        }
    }
}
