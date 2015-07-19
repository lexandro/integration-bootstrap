package steps

import component.Component
import project.Project

class DockerImageStep extends PipelineStep {

    private DockerImageStep(Project project, Component component) {
        super(project, component);
        name = 'createdockerimage';
    }

    def newInstance(Project project, Component component) {
        return new DockerImageStep();
    }

    def getJobName() {
        return sprintf('%s-%s-04-create_docker_image', project.prefix, component.name);
    }

    def createJob(PipelineStep parentStep) {
        def jobName = this.getJobName();

        dslFactory.job(jobName) {
            description 'Create docker image'
            abel project.docker_client_agent_label
            deliveryPipelineConfiguration("Build", "docker_image")
            publishers {
                publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
                for (PipelineStep nextStep : nextSteps) {
                    downstream nextStep.getJobName(), 'SUCCESS'
                }
//                mailer(ProjectData.notification_email, true, false)
            }

            configure { project ->
                project / builders / 'com.nirima.jenkins.plugins.docker.builder.DockerBuilderPublisher' {
                    dockerFileDirectory '.'
                    tag project.docker_image_prefix + component.dockerImageName
                    pushOnSuccess true
                    cleanImages false
                    cleanupWithJenkinsJobDelete false
                }
            }
            scm {
                cloneWorkspace parentStep.getJobName(), 'Any'
            }
            steps {
                maven('compile')
            }
        }

    }

}