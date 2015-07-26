import tools.*
import project.*
import environment.*
import component.*
import pipeline.*

/***************************************************************************************************************
 * initalization, cleanup
 ***************************************************************************************************************/
DslFactoryProvider.init(this);
Helpers.purgeViews();
Helpers.purgeJobs();
/***************************************************************************************************************
 * Strictly demonstrational element to show library usage
 ***************************************************************************************************************/
job('testJob') {
    steps {
        echo('some text', 123)
    }
}

job('dockerBuildJob') {
    steps {
        dockerBuild(serverUri: 'tcp://docker.host.test',
                serverCredentialsId: 'docker_host',
                registryUrl: 'http://regisry.url',
                registryCredentialsId: 'registry_credentials',
                repoName: 'testRepo',
                repoTag: 'testTag',
                skipPush: true,
                noCache: true,
                forcePull: true,
                skipBuild: true,
                createFingerPrint: true,
                skipDecorate: true,
                skipTagLatest: true,
                dockerFilePath: 'docker_dir'
        )
    }
}

/***************************************************************************************************************
 * Project settings
 ***************************************************************************************************************/
Project project = Project.builder()
        .name('Imaginarium')
        .namePrefix('img')
        .artifactoryServerId('serverId')
        .artifactoryRepositoryKey('app-repo')
        .artifactorySnapshotsRepositoryKey('app-snapshot-repo')
        .artifactoryServerUrl('server-url')
        .build();
/***************************************************************************************************************
 * setup environments
 ***************************************************************************************************************/
Environment devEnvironment = Environment.builder().serverUrl('http://myserver.com').serverCredentials('server-login').namePrefix('test').build();
/***************************************************************************************************************
 * setup component
 ***************************************************************************************************************/
Component imaginariumComponent = Component.builder().name('application').namePrefix('app').scmUrl('git@github.com:lexandro/integration.git').scmCredentials('git').build();
/***************************************************************************************************************
 * setup jobs, pipelines
 ***************************************************************************************************************/
Pipeline pipeline = Pipeline.builder()
        .project(project)
        .environment(devEnvironment)
        .component(imaginariumComponent)
        .pipelineSteps(ImaginariumPipelineSteps.getInstance())
        .build()

// TODO inject branch via ENV-var
pipeline.generate();

