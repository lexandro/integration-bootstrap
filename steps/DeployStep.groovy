package steps

import project.Project
import environment.Environment
import component.Component

class DeployStep extends PipelineStep {

    private DeployStep(Project project, Environment environment, Component component) {
        super(project, environment, component);
        name = 'Deploy';
    }

    def newInstance(Project project, Environment environment, Component component) {
        return new DeployStep(project, environment, component);
    }

    def getJobName() {
        return sprintf('%s-%s-%s-03-deploy', project.namePrefix, component.name, environment.namePrefix);
    }

    def createJob(PipelineStep parentStep) {
        def jobName = this.getJobName();

        dslFactory.job(jobName) {
            description 'Getting the source code for further processing'
            deliveryPipelineConfiguration("Build", "deploy")
            scm {
                cloneWorkspace parentStep.getJobName(), 'Any'
            }
            /* An ugly XML hacking here*/
            configure { project ->
                project / builders / 'org.jfrog.hudson.maven3.Maven3Builder' {
                    mavenName('Maven3')
                    rootPom('pom.xml')
                    goals('install')
                }
            }

            /*  deploying to artifactory */
            configure { project ->
                project / buildWrappers / 'org.jfrog.hudson.maven3.ArtifactoryMaven3Configurator' {
                    details {
                        artifactoryName(project.artifactoryServerId)
                        repositoryKey(project.artifactoryRepositoryKey)
                        snapshotsRepositoryKey(project.artifactorySnapshotsRepositoryKey)
                        artifactoryUrl(project.artifactoryServerUrl)
                    }
                    deployBuildInfo(true)
                    deployArtifacts(true)
                }
            }

            publishers {
                publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
                for (PipelineStep nextStep : nextSteps) {
                    downstream nextStep.getJobName(), 'SUCCESS'
                }
            }
        }
    }
}