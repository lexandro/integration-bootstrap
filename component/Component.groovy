package component

class Component {

    static boolean _initialized = false;
    String name
    String namePrefix
    String serverUrl
    String serverCredentials
    String scmUrl
    String scmCredentials

    def static builder() {
        return new Component();
    }


    def name(String name) {
        this.name = name
        return this;
    }

    def namePrefix(String namePrefix) {
        this.namePrefix = namePrefix
        return this;
    }


    def serverUrl(String serverUrl) {
        this.serverUrl = serverUrl
        return this;
    }

    def serverCredentials(String serverCredentials) {
        this.serverCredentials = serverCredentials
        return this;
    }

    def scmUrl(String scmUrl) {
        this.scmUrl = scmUrl
        return this;
    }

    def scmCredentials(String scmCredentials) {
        this.scmCredentials = scmCredentials
        return this;
    }


    def build() {

    }

}
