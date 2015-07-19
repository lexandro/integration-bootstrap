package project

/*
Global, project wide data stored here
*/

class Project {

    static boolean _initialized = false;
    String name
    String namePrefix

    def static builder() {
        return new Project();
    }

    def name(String name) {
        this.name = name
        return this;
    }

    def namePrefix(String namePrefix) {
        this.namePrefix = namePrefix
        return this;
    }

    def build() {

    }


}
