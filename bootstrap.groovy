//import javaposse.jobdsl.dsl.jobs.FreeStyleJob

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


def checkoutJob = "Imaginarium-master-01-checkout"
def compile = "Imaginarium-master-02-compile"
def build = "Imaginarium-master-03-build"
def sonar = "Imaginarium-master-04-sonar"
// 01 - checkout
job(checkoutJob) {
    description 'Getting the source code for further processing'
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
}

// 02 - quick compile
job(compile) {
    description 'Quick compile for health check'
    deliveryPipelineConfiguration("Build", "compile")
    publishers {
        publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
        downstream build, 'SUCCESS'
    }
    scm {
        cloneWorkspace checkoutJob, 'Any'
    }
    steps {
        maven('compile')
    }
}

// 03 - build
job(compile) {
    description 'Full build to generate artifact'
    deliveryPipelineConfiguration("Build", "build")
    publishers {
        publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
        downstream sonar, 'SUCCESS'
    }
    scm {
        cloneWorkspace checkoutJob, 'Any'
    }
    steps {
        maven('package')
    }
}