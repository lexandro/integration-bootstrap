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
def stepCount = 0;

def jobNamePrefix = sprintf('%s-%s', projectName, branchName)

def checkoutJobName = sprintf("%s-%02d-checkout", jobNamePrefix, ++stepCount)
def compileJobName = sprintf("%s-%02d-compile", jobNamePrefix, ++stepCount)
def buildJobName = sprintf("%s-%02d-build", jobNamePrefix, ++stepCount)

def dockerImageJobName = sprintf("%s-%02d-docker-image", jobNamePrefix, ++stepCount)
def sonarJobName = sprintf("%s-%02d-sonar", jobNamePrefix, stepCount)

// 01 - checkout
job(checkoutJobName) {
    description 'Getting the source code for further processing'
    deliveryPipelineConfiguration("Build", "clone")
    scm {
        git {
            remote {
                url "https://github.com/lexandro/integration.git"
            }
            branch 'master'
            shallowClone true
        }
    }
    publishers {
        publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
        downstream compileJobName, 'SUCCESS'
    }
}

// 02 - quick compileJobName
job(compileJobName) {
    description 'Quick compileJobName for health check'
    deliveryPipelineConfiguration("Build", "compile")
    scm {
        cloneWorkspace checkoutJobName, 'Any'
    }
    /* An ugly XML hacking here, but it calls the latest maven plugin*/
    configure { project ->
        project / builders / 'org.jfrog.hudson.maven3.Maven3Builder' {
            mavenName('Maven3')
            rootPom('pom.xml')
            goals('compile')
        }
    }
    publishers {
        publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
        downstream buildJobName, 'SUCCESS'
    }
}

// 03 - buildJobName
job(buildJobName) {
    description 'Full buildJobName to generate artifact'
    deliveryPipelineConfiguration("Package", "build")
    scm {
        cloneWorkspace checkoutJobName, 'Any'
    }
    steps {
        maven('package -DskipTests')
    }
    publishers {
        publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
        downstream dockerImageJobName, 'SUCCESS'
    }
}

// 04 - docker
job(dockerImageJobName) {
    description 'Create and publish docker image'
    deliveryPipelineConfiguration("Rollout", "dockerize")
    scm {
        cloneWorkspace checkoutJobName, 'Any'
    }
    /*
    * configuring cloudbee docker plugin via configure block
    */
    configure { project ->
        project / builders / 'com.cloudbees.dockerpublish.DockerBuilder ' {
            dockerFileDirectory '.'
            repoName 'lexandro/' + projectName.toLowerCase()
            noCache false
            forcePull true
            dockerfilePath '.'
            skipBuild false
            skipDecorate true
            repoTag latest
            skipTagLatest false

        }
    }
    publishers {
        publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
    }
}

// 04 - sonar
job(sonarJobName) {
    description 'Quality check'
    deliveryPipelineConfiguration("QA", "sonar")
    scm {
        cloneWorkspace checkoutJobName, 'Any'
    }
    steps {
        maven('-version')
    }
    publishers {
        publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
    }
}

// 06 - deploy
job(deployJobName) {
    description 'Deploy app image to the demo server'
    deliveryPipelineConfiguration("Rollout", "deploy")
    scm {
        cloneWorkspace checkoutJobName, 'Any'
    }
    steps {
        maven('-version')
    }
    publishers {
        publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
    }
}

listView(jobNamePrefix + ' jobs') {
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
        component(jobNamePrefix + ' pipeline', checkoutJobName)
    }
}

