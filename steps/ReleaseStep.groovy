package steps

import project.Project
import environment.Environment
import component.Component

class ReleaseStep extends PipelineStep {

    private ReleaseStep(Project project, Environment environment, Component component) {
        super(project, environment, component);
        name = 'Release';
    }

    def newInstance(Project project, Environment environment, Component component) {
        return new ReleaseStep(project, environment, component);
    }

    def getJobName() {
        return sprintf('%s-%s-%s-06-release', project.namePrefix, component.name, environment.namePrefix);
    }

    def createJob(PipelineStep parentStep) {
        def jobName = this.getJobName();

        dslFactory.job(jobName) {
            description 'Deploy app image to the demo server'
            deliveryPipelineConfiguration("Rollout", "deploy image")
            scm {
                /*
                * configuring ssh plugin to run docker commands
                */
                configure { project ->
                    project / buildWrappers / 'org.jvnet.hudson.plugins.SSHBuildWrapper' {
                        siteName 'user@server:22'
                        postScript """
docker stop ${component.name}
docker rm ${component.name}
docker pull lexandro/${component.name}
docker run -dt --name '${component.name}' -e MONGO_URI=$MONGO_URI -p 80:8080 lexandro/${component.name}
"""
                    }
                }
            }
        }
    }
}