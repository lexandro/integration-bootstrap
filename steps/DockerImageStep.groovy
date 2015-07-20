package steps

import project.Project
import environment.Environment
import component.Component

class DockerImageStep extends PipelineStep {

    private DockerImageStep(Project project, Environment environment, Component component) {
        super(project, environment, component);
        name = 'create-docker-image';
    }

    def newInstance(Project project, Environment environment, Component component) {
        return new DockerImageStep(project, environment, component);
    }

    def getJobName() {
        return sprintf('%s-%s-%s-04-create_docker_image', project.namePrefix, component.name, environment.namePrefix);
    }

    def createJob(PipelineStep parentStep) {
        def jobName = this.getJobName();

        dslFactory.job(jobName) {
            description 'Create docker image'
            deliveryPipelineConfiguration("Docker", "push image")
            publishers {
                publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
                for (PipelineStep nextStep : nextSteps) {
                    downstream nextStep.getJobName(), 'SUCCESS'
                }
            }
            /*
            configuring CloudBees docker plugin via configure block
           */
            configure { project ->
                project / builders / 'com.cloudbees.dockerpublish.DockerBuilder' {
                    dockerFileDirectory '.'
                    repoName 'lexandro/' + component.name
                    noCache false
                    forcePull false
                    dockerfilePath '.'
                    skipBuild false
                    skipDecorate true
                    skipTagLatest false
                }
            }
            scm {
                cloneWorkspace parentStep.getJobName(), 'Any'
            }
        }
    }
}