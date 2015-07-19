package environment


class Environment {

    static boolean _initialized = false;
    String serverUrl
    String serverCredentials

    def static builder() {
        return new Environment();
    }

    def serverUrl(String serverUrl) {
        this.serverUrl = serverUrl
        return this;
    }

    def serverCredentials(String serverCredentials) {
        this.serverCredentials = serverCredentials
        return this;
    }


    def build() {

    }
}
