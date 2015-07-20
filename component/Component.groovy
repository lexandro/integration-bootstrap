package component

class Component {

    String name
    String namePrefix
    String serverUrl
    String serverCredentials
    String scmUrl
    String scmCredentials

    def static builder() {
        return new ComponentBuilder();
    }
    //
    static class ComponentBuilder {

        Component _instance;

        ComponentBuilder() {
            _instance = new Component();
        }

        def name(String name) {
            _instance.name = name
            return this;
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

        def scmUrl(String scmUrl) {
            _instance.scmUrl = scmUrl
            return this;
        }

        def scmCredentials(String scmCredentials) {
            _instance.scmCredentials = scmCredentials
            return this;
        }

        def build() {
            return _instance;
        }
    }
}
