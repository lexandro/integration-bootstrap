package tools

public class Helpers {

    static public def purgeJobs() {

        jenkins.model.Jenkins.theInstance.getProjects().each { job ->
            if (!job.name.contains('bootstrap') && !job.name.contains('Jenkins')) {
                job.delete()
            }
        }
    }

    static public def purgeViews() {
        jenkins.model.Jenkins.theInstance.getViews().each {
            view ->
                if (view.name != 'All' && view.name != 'Jenkins') {
                    jenkins.model.Jenkins.theInstance.deleteView(view)
                }
        }
    }
}
