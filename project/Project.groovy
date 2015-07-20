package project

/*
Global, project wide data stored here
*/

class Project {
    //
    String name
    String namePrefix
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

        def build() {
            return _instance;
        }
    }
}
