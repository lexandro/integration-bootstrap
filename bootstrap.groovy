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


def checkoutJobName = "Imaginarium-master-01-checkout"
def compileJobName = "Imaginarium-master-02-compileJobName"
def buildJobName = "Imaginarium-master-03-build"
def sonarJobName = "Imaginarium-master-04-sonar"
def deployJobName = "Imaginarium-master-04-deploy"
// 01 - checkout
job(checkoutJobName) {
    description 'Getting the source code for further processing'
    deliveryPipelineConfiguration("Build", "clone")
    publishers {
        publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
        downstream "Imaginarium-master-02-compileJobName", 'SUCCESS'
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

// 02 - quick compileJobName
job(compileJobName) {
    description 'Quick compileJobName for health check'
    deliveryPipelineConfiguration("Build", "compile")
    publishers {
        publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
        downstream buildJobName, 'SUCCESS'
    }
    scm {
        cloneWorkspace checkoutJobName, 'Any'
    }
    /* An ugly XML hacking here*/
    configure { project ->
        project / builders / 'org.jfrog.hudson.maven3.Maven3Builder' {
            mavenName('Maven3')
            rootPom('pom.xml')
            goals('compile')
        }
    }
}

// 03 - buildJobName
job(buildJobName) {
    description 'Full buildJobName to generate artifact'
    deliveryPipelineConfiguration("Package", "build")
    publishers {
        publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
        downstream sonarJobName, 'SUCCESS'
        downstream deployJobName, 'SUCCESS'
    }
    scm {
        cloneWorkspace checkoutJobName, 'Any'
    }
    steps {
        maven('package -DskipTests')
    }
}

// 04 - sonar
job(sonarJobName) {
    description 'Quality check'
    deliveryPipelineConfiguration("QA", "sonar")
    publishers {
        publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
        downstream sonarJobName, 'SUCCESS'
    }
    scm {
        cloneWorkspace checkoutJobName, 'Any'
    }
    steps {
        maven('version')
    }
}

// 04 - deploy
job(deployJobName) {
    description 'Deploy app to the demo server'
    deliveryPipelineConfiguration("Rollout", "deploy")
    publishers {
        publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
        downstream sonarJobName, 'SUCCESS'
    }
    scm {
        cloneWorkspace checkoutJobName, 'Any'
    }
    steps {
        maven('version')
    }
}