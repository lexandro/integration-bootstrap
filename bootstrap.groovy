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

def projectName = 'Imaginarium'
def branchName = 'master'
def stepCount = 1;

def jobNamePrefix = sprintf('%s-%s', projectName, branchName)

def checkoutJobName = sprintf("%s-%02d-checkout", jobNamePrefix, stepCount++)
def compileJobName = sprintf("%s-%02d-compile", jobNamePrefix, stepCount++)
def buildJobName = sprintf("%s-%02d-build", jobNamePrefix, stepCount++)
def deployJobName = sprintf("%s-%02d-deploy", jobNamePrefix, stepCount++)
def sonarJobName = sprintf("%s-%02d-sonar", jobNamePrefix, stepCount++)

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
        downstream deployJobName, 'SUCCESS'
    }
    scm {
        cloneWorkspace checkoutJobName, 'Any'
    }
    steps {
        maven('package -DskipTests')
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
        maven('-version')
    }
}

// 05 - sonar
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
        maven('-version')
    }
}

listView(jobNamePrefix + ' jobs') {
    description viewDescription
    jobs {
        regex(jobNamePrefix + '(.*?)')
    }
    columns {
        status()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}

deliveryPipelineView(jobNamePrefix + ' pipeline') {
    pipelines {
        component(viewName, checkoutJobName)
    }
}

