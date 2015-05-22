// purge jobs
jenkins.model.Jenkins.theInstance.getProjects().each { job ->
    if (!job.name.contains('bootstrap') && !job.name.contains('Jenkins')) {
        job.delete()
    }
}

// purge views
jenkins.model.Jenkins.theInstance.getViews().each {
    view ->
        if (view.name != 'All' && view.name != 'Jenkins') {
            jenkins.model.Jenkins.theInstance.deleteView(view)
        }
}

// 01 - checkout
job("Imaginarium-master-01-checkout") {
    description 'Getting the source code for further processing'
    label project.build_agent_label
    deliveryPipelineConfiguration("Build", "clone")
    publishers {
        publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
        downstream "Imaginarium-master-02-compile", 'SUCCESS'
    }
    scm {
        git {
            remote {
                url "https://github.com/lexandro/integration.git"
            }
            branch 'master'
            shallowClone true
        }
    }
    triggers {
    }
}