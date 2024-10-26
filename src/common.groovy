import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files

// константы объявляем без def, чтобы были доступны из функций
ServiceModelConfig = readYaml(file: './ci/services-model.yaml')
CommonBuildProps = readYaml(file: './ci/devzone-tools/jenkins/common/common-build-props.yaml')
ProjectBuildProps = readYaml(file: './ci/build-props.yaml')

Map<String,String> serviceTypes = [
        dbMigrations: "db-migrations",
        dotnet: "dotnet",
        front: "front",
        python: "python",
        infrastructure: "infrastructure"
]

Map<String,String> getServiceTypes() {
    return [
            dbMigrations: "db-migrations",
            dotnet: "dotnet",
            front: "front",
            python: "python",
            infrastructure: "infrastructure",
    ]
}

// методы списков Groovy:
// collect => map/select
// find => findFirst
// findAll => filter/where
// any/every - предикаты
// inject => reduce

class FrontendForDtrackInfo {
    String name // GM.Frontend.Auth
    String packageJsonPath // src/GM.Frontend/GM.Frontend.Auth/package.json
}

List<FrontendForDtrackInfo> getFrontendForDtrackInfos() {
    def fronts = ServiceModelConfig['services']
            .findAll { svc -> svc['type'] == serviceTypes.front }
            .collect { svc -> new FrontendForDtrackInfo(
                    name: svc['name'],
                    packageJsonPath: svc['path'] + "/package.json",
            ) }
    return fronts
}

class DotnetForDtrackInfo {
    String name // GM.BFF
    String srcDir // src/GM.Services
    String slnFile // GM.BFF/GM.BFF.sln
}

List<DotnetForDtrackInfo> getDotnetForDtrackInfos() {
    def dotnets = ServiceModelConfig['services']
            .findAll { svc -> svc['type'] == serviceTypes.dotnet }
            .collect { svc ->
                def path = svc['path']
                def serviceName = (path =~ /\/([^\/]+)$/)[0][1]

                return new DotnetForDtrackInfo(
                        name: svc['name'],
                        slnFile: "${serviceName}/${serviceName}.sln",
                        srcDir: (path =~ /^(.*)\/[^\/]+$/)[0][1],
                )
            }
    return dotnets
}

class PythonForDtrackInfo {
    String name // GM.JobsManager
    String srcDir // src/GM.SeismicBackend/GM.JobsManager
}

List<PythonForDtrackInfo> getPythonForDtrackInfos() {
    def infos = ServiceModelConfig['services']
            .findAll { svc -> svc['type'] == serviceTypes.python }
            .collect { svc -> new PythonForDtrackInfo(
                    name: svc['name'],
                    srcDir: svc['path'],
            ) }
    return infos
}

List<String> getImagesForClair() {
    def images = ServiceModelConfig['services']
            .findAll { svc ->
                def isServiceOrMigration = svc['type'] in [
                        serviceTypes.dotnet,
                        serviceTypes.python,
                        serviceTypes.front,
                        serviceTypes.dbMigrations
                ]
                def isProdInfra = svc['type'] == serviceTypes.infrastructure && svc['onlyForTests'] != true
                return isServiceOrMigration || isProdInfra
            }
            .collect { svc -> svc['image'] }
            .sort()
    return images
}

class DotnetForTransferInfo {
    String name // GM.BFF
    String srcDir // src/GM.Services
    String slnFile // GM.BFF/GM.BFF.sln
    String image // geomate-bff
}

DotnetForTransferInfo getDotnetForTransferInfo(String name) {
    def service = ServiceModelConfig['services']
            .find { svc -> svc['name'] == name }

    def path = service['path']
    def serviceName = (path =~ /\/([^\/]+)$/)[0][1]

    def info = new DotnetForTransferInfo(
            name: service['name'],
            image: service['image'],
            slnFile: "${serviceName}/${serviceName}.sln",
            srcDir: (path =~ /^(.*)\/[^\/]+$/)[0][1],
    )
    return info
}

class PythonForTransferInfo {
    String name // GM.JobsManager
    String srcDir // src/GM.SeismicBackend/GM.JobsManager
    String image // geomate-mapbuilder
}

PythonForTransferInfo getPythonForTransferInfo(String name) {
    def service = ServiceModelConfig['services']
            .find { svc -> svc['name'] == name }

    def info = new PythonForTransferInfo(
            name: service['name'],
            image: service['image'],
            srcDir: service['path'],
    )
    return info
}

class FrontForTransferInfo {
    String name
    String packageJsonPath
    String image
}

FrontForTransferInfo getFrontForTransferInfo(String name) {
    def service = ServiceModelConfig['services']
            .find { svc -> svc['name'] == name }

    def info = new FrontForTransferInfo(
            name: service['name'],
            image: service['image'],
            packageJsonPath: service['path'] + "/package.json",
    )
    return info
}

// если не нашла, возвращает null
String getServiceTypeByName(String name) {
    def service = ServiceModelConfig['services']
            .find { svc -> svc['name'] == name }
    return service['type']
}

class ShellResult {
    String stdOut
    String stdErr
    String allOutput
    Integer exitCode
}

// возвращает одновременно стандартные выходы и код возврата
ShellResult runShell(String script, Boolean exitCodeMustBeZero = true) {
    def header = """\
        #!/bin/bash
        set -eu -o pipefail
        # перенаправляем вывод для последующих команд
        # tee будет выводить одновременно в файлы и в консоль
        # >() называется process substitution
        exec 1> >(tee -a stdout stdall) 2> >(tee -a stderr stdall)
    """
    echo "++ " + script.stripIndent().trim()
    def fullScript = "${header.stripIndent()}\n${script.stripIndent()}"

    def exitCode = sh(script: fullScript, returnStatus: true)

    def result = new ShellResult(
            stdOut: readFile('stdout'),
            stdErr: readFile('stderr'),
            allOutput: readFile('stdall'),
            exitCode: exitCode,
    )

    sh('rm stdout stderr stdall')

    if (exitCodeMustBeZero && exitCode != 0) { error "previous shell script's exit code indicates failure" }

    return result
}

class CiProps {
    Boolean runTests
    String testArgs
    Boolean runCoverage
}

class DotnetCiProps extends CiProps {
    Boolean runBuild
    String buildArgs
    Boolean runRestore
    String restoreArgs
    CheckSwaggerInfo checkSwagger
}

class NpmCiProps extends CiProps {
    String cleanInstallArgs
    Boolean runBuildClients
    Boolean runTypecheck
    Boolean runLint
    Boolean runBuildApp
    List<String> customCommands
}

// Для python используется poetry + pytest
class PythonCiProps extends CiProps {
    String installArgs
    Boolean runBuild
    String buildFormat
    Boolean runPylint
    String pylintArgs
    Boolean runMypy
    String mypyArgs
    Boolean runBlack
    String blackArgs
    Boolean runIsort
    String isortArgs
}

class ServiceInfo {
    String buildJob
    String dockerfile
    String image
    String name
    String kuberObjectType // Тип разворачиваемого в openshift объекта, pod или job
    List notInEnvironments
    String migrateArgs
    String type
    String buildContext
    String buildFirst
    CiProps ciProps
    String path
}

class LandscapeInfo {
    String name
    String paasId
    String segmentId
    String mainRegistry
}

class ClusterInfo {
    String name
    String landscape
    String cloudApiUrl
    String paasId
}

class DeployEnvironmentInfo {
    String name
    String cluster
    String projectId
    String cloudNamespace
    String landscape
    String appUrlOrSuffix
    String configMapVariation
    String configMap
    String storageClassForSeismicData
}

class CheckSwaggerInfo {
    String referencePath
    String swaggerDoc
}

CheckSwaggerInfo makeCheckSwaggerInfo(svc) {
    def options = svc['ciProps']['checkSwagger']
    if(options instanceof Map) {
        return new CheckSwaggerInfo(
                referencePath: options['referencePath'] ? options['referencePath'] : 'openapi-reference.json',
                swaggerDoc: options['swaggerDoc'] ? options['swaggerDoc'] : 'v1',
        )
    }
    else {
        return new CheckSwaggerInfo(referencePath: 'openapi-reference.json', swaggerDoc: 'v1')
    }

}

CiProps makeCiProps(svc) {
    CiProps ciProps
    def steps = svc['ciProps']
    if (steps == null) {
        return ciProps
    }
    switch(svc['type']) {
        case serviceTypes.dotnet:
            ciProps = new DotnetCiProps(
                    runBuild: steps['runBuild'] ? steps['runBuild'] : false,
                    buildArgs: steps['buildArgs'] ? steps['buildArgs'] : '',
                    runTests: steps['runTests'] ? steps['runTests'] : false,
                    testArgs: steps['testArgs'] ? steps['testArgs'] : '',
                    runRestore: steps['runRestore'] ? steps['runRestore'] : false,
                    restoreArgs: steps['restoreArgs'] ? steps['restoreArgs'] : '',
                    checkSwagger: steps['checkSwagger'] ? makeCheckSwaggerInfo(svc) : null,
                    runCoverage: steps['runCoverage'] ? steps['runCoverage'] : false,
            )
            break;
        case serviceTypes.front:
            ciProps = new NpmCiProps(
                    runTests: steps['runTests'] ? steps['runTests'] : false,
                    testArgs: '',
                    runCoverage: steps['runCoverage'] ? steps['runCoverage'] : false,
                    cleanInstallArgs: steps['cleanInstallArgs'] ? steps['cleanInstallArgs'] : '',
                    runBuildClients: steps['runBuildClients'] ? steps['runBuildClients'] : false,
                    runTypecheck: steps['runTypecheck'] ? steps['runTypecheck'] : true,
                    runLint: steps['runLint'] ? steps['runLint'] : true,
                    runBuildApp: steps['runBuildApp'] ? steps['runBuildApp'] : true,
                    customCommands: steps['customCommands'] ? steps['customCommands'] : null,
            )
            break;
        case serviceTypes.python:
            ciProps = new PythonCiProps(
                    runTests: steps['runTests'] ? steps['runTests'] : false,
                    testArgs: steps['testArgs'] ? steps['testArgs'] : '',
                    runCoverage: steps['runCoverage'] ? steps['runCoverage'] : false,
                    installArgs: steps['installArgs'] ? steps['installArgs'] : '',
                    runBuild: steps['runBuild'] ? steps['runBuild'] : false,
                    buildFormat: steps['buildFormat'] ? steps['buildFormat'] : '',
                    runPylint: steps['runPylint'] ? steps['runPylint'] : false,
                    pylintArgs: steps['pylintArgs'] ? steps['pylintArgs'] : '',
                    runMypy: steps['runMypy'] ? steps['runMypy'] : false,
                    mypyArgs: steps['mypyArgs'] ? steps['mypyArgs'] : '',
                    runBlack: steps['runBlack'] ? steps['runBlack'] : false,
                    blackArgs: steps['blackArgs'] ? steps['blackArgs'] : '',
                    runIsort: steps['runIsort'] ? steps['runIsort'] : false,
                    isortArgs: steps['isortArgs'] ? steps['isortArgs'] : '',
            )
            break;
        default:
            break;
    }
    return ciProps
}

ServiceInfo makeServiceInfo(svc) {
    return new ServiceInfo(
            buildJob: svc['buildJob'],
            dockerfile: svc['dockerfile'],
            image: svc['image'],
            name: svc['name'],
            kuberObjectType: svc['type'] == serviceTypes.dbMigrations ? 'job' : 'pod',
            notInEnvironments: svc['environmentFilter'] ? svc['environmentFilter'].notInEnvironments : [],
            migrateArgs: svc['type'] == serviceTypes.dbMigrations ? svc['migrateArgs'] : '',
            type: svc['type'],
            buildContext: svc['buildContext'] ? svc['buildContext'] : '.',
            buildFirst: svc['buildFirst'] ? svc['buildFirst'] : false,
            ciProps: makeCiProps(svc),
            path: svc['path'] ? svc['path'] : '',
    )
}

DeployEnvironmentInfo makeDeployEnvironmentInfo(svc) {
    return new DeployEnvironmentInfo(
            cluster: svc['cluster'],
            projectId: svc['projectId'],
            cloudNamespace: svc['cloudNamespace'],
            landscape: svc['landscape'],
            appUrlOrSuffix: svc['appUrlOrSuffix'],
            configMapVariation: svc['configMapVariation'],
            configMap: svc['configMap'],
            storageClassForSeismicData: svc['storageClassForSeismicData'],
    )
}

LandscapeInfo makeLandscapeInfo(svc) {
    return new LandscapeInfo(
            name: svc['name'],
            paasId: svc['paasId'],
            segmentId: svc['segmentId'],
            mainRegistry: svc['mainRegistry'],
    )
}

ClusterInfo makeClusterInfo(svc) {
    return new ClusterInfo(
            name: svc['name'],
            landscape: svc['landscape'],
            cloudApiUrl: svc['cloudApiUrl'],
            paasId: svc['paasId'],
    )
}

List<ServiceInfo> getServicesFromModel(Closure predicate) {
    def infos = ServiceModelConfig['services']
            .findAll { svc -> predicate(svc) }
            .collect { svc -> makeServiceInfo(svc) }
    return infos
}

List<ServiceInfo> getAllServicesFromModel() {
    def infos = ServiceModelConfig['services']
            .collect { svc -> makeServiceInfo(svc) }
    return infos
}

List<ServiceInfo> getServicesWithBuildJobs() {
    return getServicesFromModel({ svc -> svc['buildJob'] != null })
}

List<ServiceInfo> getServicesByTypeForScanners(String type) {
    return getServicesFromModel({ svc -> svc['type'] == type })
}

List<ServiceInfo> getServicesForCi() {
    return getServicesFromModel({ svc ->  svc['ciProps'] != null })
}

// используется в jenkinsfile-transfer-images-new
List<ServiceInfo> getServicesForTransfer() {
    return getServicesFromModel({ svc -> svc['environmentFilter'] == null ||
            (!svc['environmentFilter']['notInEnvironments'].contains('preprod') &&
                    !svc['environmentFilter']['notInEnvironments'].contains('prod'))})
}

// используется в jenkinsfile-transfer-image-any
ServiceInfo getServiceForTransferByName(String name) {
    return getServicesForTransfer().find { it.name == name }
}

ServiceInfo getServiceInfoByName(String name) {
    def svc = ServiceModelConfig['services'].find { svc -> svc['name'] == name }
    return makeServiceInfo(svc)
}

DeployEnvironmentInfo getDeployEnvironmentInfoByName(String name) {
    def svc = ServiceModelConfig['environments'].find { svc -> svc['name'] == name }
    return makeDeployEnvironmentInfo(svc)
}

LandscapeInfo getLandscapeInfoByName(String name) {
    def svc = ServiceModelConfig['landscapes'].find { svc -> svc['name'] == name }
    return makeLandscapeInfo(svc)
}

LandscapeInfo getLandscapeInfoBySegment(String segmentId) {
    def svc = ServiceModelConfig['landscapes'].find { svc -> svc['segmentId'] == segmentId }
    return makeLandscapeInfo(svc)
}

ClusterInfo getClusterInfoByName(String name) {
    def svc = ServiceModelConfig['clusters'].find { svc -> svc['name'] == name }
    return makeClusterInfo(svc)
}

Map<String,String> getBuildProps(String segmentId) {
    def commonProps = CommonBuildProps['segments'].find { svc -> svc['id'] == segmentId}
    def projectProps = ProjectBuildProps['segments'].find { svc -> svc['id'] == segmentId}
    projectProps = projectProps.findAll { it.key != 'jenkinsCredentials' }
    return commonProps + projectProps
}

def getJenkinsCredentials(String segmentId) {
    def projectProps = ProjectBuildProps['segments'].find { svc -> svc['id'] == segmentId}
    def withCredentialsList = []
    if(projectProps['jenkinsCredentials']) {
        for(item in projectProps['jenkinsCredentials']) {
            withCredentialsList.add(
                    string(credentialsId: item['credentialsId'], variable: item['variable'])
            )
        }
    }
    return withCredentialsList
}

void setBuildPropsAsEnvVars(String segmentId) {
    def props = getBuildProps(segmentId)
    props.each { key, value -> env."$key" = "$value" }
}

String getDefaultBuildArgs(String serviceType, String segmentId, String appVersion) {
    def landscape = getLandscapeInfoBySegment(segmentId)
    def buildProps = getBuildProps(segmentId)
    def commonArgs = "--build-arg DOCKER_REGISTRY=${landscape.mainRegistry} " +
            "--build-arg QUAY_DOCKER_REGISTRY=${buildProps.QUAY_DOCKER_REGISTRY} "
    def serviceArgs
    switch(serviceType) {
        case serviceTypes.dbMigrations:
            serviceArgs = "--build-arg MIGRATE_BASE_IMAGE=${buildProps.MIGRATE_BASE_IMAGE} "
            break;
        case serviceTypes.dotnet:
            serviceArgs = "--build-arg DOTNET_SDK_BASE_IMAGE=${buildProps.DOTNET_SDK_BASE_IMAGE} " +
                    "--build-arg DOTNET_RUNTIME_BASE_IMAGE=${buildProps.DOTNET_RUNTIME_BASE_IMAGE} " +
                    "--build-arg NUGET_V3_EXTERNAL=${buildProps.NUGET_V3_EXTERNAL} " +
                    "--build-arg APP_VERSION=${appVersion} "
            break;
        case serviceTypes.front:
            serviceArgs = "--build-arg NODEJS_BASE_IMAGE=${buildProps.NODEJS_BASE_IMAGE} " +
                    "--build-arg NGINX_BASE_IMAGE=${buildProps.NGINX_BASE_IMAGE} " +
                    "--build-arg NPM_REGISTRY=${buildProps.NPM_REGISTRY} " +
                    "--build-arg APP_VERSION=${appVersion} "
            break;
        case serviceTypes.python:
            serviceArgs = "--build-arg POETRY_VERSION=${buildProps.POETRY_VERSION} " +
                    "--build-arg PYTHON_BASE_IMAGE=${buildProps.PYTHON_BASE_IMAGE} " +
                    "--build-arg PIP_INDEX_URL=${buildProps.PIP_INDEX_URL} " +
                    "--build-arg PIP_TRUSTED_HOST=${buildProps.PIP_TRUSTED_HOST} " +
                    "--build-arg APP_VERSION=${appVersion} "
            break;
        default:
            serviceArgs = ""
            break;
    }
    return commonArgs + serviceArgs
}

Boolean isLifetimeExpired(Integer appLifeTime, String appname) {
    def isExpired
    def dateNowUnixTimeInSeconds = "${new Date().getTime()}".substring(0, 10).toInteger()
    def timeStamps = runShell("""
        oc get all --selector app=${appname} -o jsonpath=\"{range .items[*]}{.metadata.annotations.creationTimestampInUnixTime}{' '}{end}\"
    """).allOutput.trim().split(/\s+/).findAll { it != '' }
    if (timeStamps) {
        for (timeStamp in timeStamps) {
            if (dateNowUnixTimeInSeconds - timeStamp.toInteger() < appLifeTime * 3600) {
                isExpired = false
                echo "App was deployed less than ${appLifeTime}h ago"
                break
            } else {
                isExpired = true
            }
        }
    } else {
        echo "Nothing to delete"
        isExpired = false
    }
    return isExpired
}

boolean pathExist(servicePath){
    Path path = servicePath;
    return Files.exists(path)
}

def dotnetRestore(ServiceInfo svc){
    def actionName = "RESTORE"
    def title = "============ dotnet ${actionName} - ${svc.name} ==========="
    def command
    if(svc.ciProps.runRestore) {
        command = "dotnet restore ${svc.ciProps.restoreArgs} ${svc.path}"
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def dotnetBuild(ServiceInfo svc) {
    def actionName = "BUILD"
    def title = "============ dotnet ${actionName} - ${svc.name} ==========="
    def command
    if(svc.ciProps.runBuild) {
        command = "dotnet build ${svc.ciProps.buildArgs} ${svc.path}"
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def dotnetTest(ServiceInfo svc) {
    def actionName = "TEST"
    def title = "============ dotnet ${actionName} - ${svc.name} ==========="
    def command
    if(svc.ciProps.runTests) {
        command = "dotnet test ${svc.ciProps.testArgs} ${svc.path}"
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def dotnetCheckSwagger(ServiceInfo svc) {
    def actionName = "CHECK SWAGGEER"
    def title = "============ dotnet ${actionName} - ${svc.name} ==========="
    def command
    if(svc.ciProps.checkSwagger) {
        command = """
        basedir=\$(pwd)
        cd ${svc.path}
        swaggerToolVersion=\$(dotnet list package | grep 'Swashbuckle.AspNetCore' | awk '{print \$3}')
        dotnet tool install swashbuckle.aspnetcore.cli --version \$swaggerToolVersion --tool-path .
        export DOTNET_ROOT=/home/jenkins/.dotnet
        export DOTNET_SYSTEM_GLOBALIZATION_INVARIANT=1

        swagger tofile --output openapi.json \$(find bin -name \$(basename -- ${svc.path}).dll | head -n 1) ${svc.ciProps.checkSwagger.swaggerDoc}
        
        urlRegex='https?:\\\\/\\\\/(www\\\\.)?[-a-zA-Z0-9@:%._\\\\+~#=]{1,256}\\\\.[a-zA-Z0-9()]{1,6}\\\\b([-a-zA-Z0-9()@:%_\\\\+.~#?&//=]*)'
        referencePath='${svc.ciProps.checkSwagger.referencePath}'
        if [[ \$referencePath =~ \$urlRegex ]]
        then 
            curl -o openapi-reference.json \$referencePath
            diff openapi-reference.json openapi.json --ignore-all-space -c
        else
            diff \${basedir}/\$referencePath openapi.json --ignore-all-space -c
        fi
        """
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def dotnetClean(ServiceInfo svc) {
    def actionName = "CLEAN"
    return {
        echo "============ dotnet ${actionName} - ${svc.name} ==========="
        sh """ dotnet clean ${svc.path} """
        sh """ rm -rf ${svc.path}/swagger """
    }
}

def npmRootPackageCleanInstall(ServiceInfo svc) {
    def actionName = "CLEAN INSTALL"
    def title = "============ npm ${actionName} - ${svc.name} ==========="
    def command
    if(svc.ciProps.runCleanInstall) {
        command = """
        cd ${svc.path}
        npm ci ${svc.ciProps.cleanInstallArgs}
        """
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def npmRootPackageExists(ServiceInfo svc) {
    if (fileExists("${svc.path}/../package.json")) {
        def content = readFile(file: "${svc.path}/../package.json")
        if(content.contains("scripts")) {
            println("Root package exists")
            return true
        }
    }
    println("Root package doesn't exist")
    return false
}

def npmCleanInstall(ServiceInfo svc) {
    def actionName
    def command
    if(npmRootPackageExists(svc)) {
        actionName = "ROOT PACKAGE CLEAN INSTALL"
        command = "cd ${svc.path}/..\n"
    }
    else {
        actionName = "CLEAN INSTALL"
        command = "cd ${svc.path}\n"
    }
    command += "npm ci ${svc.ciProps.cleanInstallArgs}"
    def title = "============ npm ${actionName} - ${svc.name} ==========="
    return {
        echo "${title}"
        sh "${command}"
    }
}

def npmBuildClients(ServiceInfo svc) {
    def actionName = "BUILD CLIENTS"
    def title = "============ npm ${actionName} - ${svc.name} ==========="
    def command
    if(svc.ciProps.runBuildClients) {
        command = """
        cd ${svc.path}
        npm run build:clients
        """
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def npmTypeCheck(ServiceInfo svc) {
    def actionName = "TYPE CHECK"
    def title = "============ npm ${actionName} - ${svc.name} ==========="
    def command
    if(svc.ciProps.runTypecheck) {
        command = """
        cd ${svc.path}
        npm run typecheck
        """
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def npmLint(ServiceInfo svc) {
    def actionName = "LINT"
    def title = "============ npm ${actionName} - ${svc.name} ==========="
    def command
    if(svc.ciProps.runLint) {
        command = """
        cd ${svc.path}
        npm run lint
        """
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def npmBuildApp(ServiceInfo svc) {
    def actionName = "BUILD APP"
    def title = "============ npm ${actionName} - ${svc.name} ==========="
    def command
    if(svc.ciProps.runBuildApp) {
        command = """
        cd ${svc.path}
        npm run build:app
        """
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def npmTest(ServiceInfo svc) {
    def actionName = "TEST"
    def title = "============ npm ${actionName} - ${svc.name} ==========="
    def command
    if(svc.ciProps.runTests) {
        command = """
        cd ${svc.path}
        npm run test
        """
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def npmCustomCommands(ServiceInfo svc) {
    def actionName = "CUSTOM COMMANDS"
    def title = "============ npm ${actionName} - ${svc.name} ==========="
    def commands = ""
    if(!svc.ciProps.customCommands) {
        commands = "echo '${actionName} not specified in service-model.yaml'"
    }
    else {
        commands = "cd ${svc.path}\n"
        for(command in svc.ciProps.customCommands) {
            commands += "npm ${command}\n"
        }
    }
    return {
        echo "${title}"
        sh "${commands}"
    }
}

def npmClean(ServiceInfo svc) {
    def actionName = "CLEAN"
    return {
        echo "============ npm ${actionName} - ${svc.name} ==========="
        sh """
        cd ${svc.path}
        rm -rf ./node_modules
        """
    }
}

def pythonPoetryInstall(ServiceInfo svc) {
    def actionName = "POETRY INSTALL"
    def title = "============ python ${actionName} - ${svc.name} ==========="
    def command = """
    cd ${svc.path}
    poetry install ${svc.installArgs}
    """
    return {
        echo "${title}"
        sh "${command}"
    }
}

def pythonPoetryBuild(ServiceInfo svc) {
    def actionName = "POETRY BUILD"
    def title = "============ python ${actionName} - ${svc.name} ==========="
    def command
    if(svc.ciProps.runBuild) {
        command = """
        cd ${svc.path}
        poetry build
        """
        if(svc.ciProps.buildFormat) {
            command += " -f ${svc.buildFormat}"
        }
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def pythonPoetryPytest(ServiceInfo svc) {
    def actionName = "POETRY RUN PYTEST"
    def title = "============ python ${actionName} - ${svc.name} ==========="
    def command
    if(svc.ciProps.runTests) {
        command = """
        cd ${svc.path}
        poetry run pytest ${svc.ciProps.testArgs}
        """
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def getPoetryProjectName(path) {
    return sh(
            script: "cd ${path} && grep \"^name\" pyproject.toml | hed -1 | sed -e \"s/^name = //\" | sed -e \"s/\\\"//g",
            returnStdout: true
    ).trim()
}

def pythonPoetryPylint(ServiceInfo svc) {
    def actionName = "POETRY RUN PYLINT"
    def title = "============ python ${actionName} - ${svc.name} ==========="
    def command
    def poetryProjectName = getPoetryProjectName(svc.path)
    if(svc.ciProps.runPylint) {
        command = """
        cd ${svc.path}
        poetry run pylint ${svc.ciProps.pylintArgs} ${poetryProjectName}
        """
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def pythonPoetryMypy(ServiceInfo svc) {
    def actionName = "POETRY RUN MYPY"
    def title = "============ python ${actionName} - ${svc.name} ==========="
    def command
    def poetryProjectName = getPoetryProjectName(svc.path)
    if(svc.ciProps.runMypy) {
        command = """
        cd ${svc.path}
        poetry run mypy ${svc.ciProps.mypyArgs} ${poetryProjectName}
        """
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def pythonPoetryBlack(ServiceInfo svc) {
    def actionName = "POETRY RUN BLACK CHECK"
    def title = "============ python ${actionName} - ${svc.name} ==========="
    def command
    def poetryProjectName = getPoetryProjectName(svc.path)
    if(svc.ciProps.runBlack) {
        command = """
        cd ${svc.path}
        poetry run black --check ${svc.ciProps.blackArgs} ${poetryProjectName}
        """
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def pythonPoetryIsort(ServiceInfo svc) {
    def actionName = "POETRY RUN ISORT CHECK"
    def title = "============ python ${actionName} - ${svc.name} ==========="
    def command
    def poetryProjectName = getPoetryProjectName(svc.path)
    if(svc.ciProps.runIsort) {
        command = """
        cd ${svc.path}
        poetry run isort --check-only ${svc.ciProps.isortArgs} ${poetryProjectName}
        """
    }
    else {
        command = "echo '${actionName} not specified in service-model.yaml'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def pythonPoetryClean(ServiceInfo svc) {
    def actionName = "POETRY CLEAN"
    return {
        echo "============ python ${actionName} - ${svc.name} ==========="
        sh """
        cd ${svc.path}
        VENV_NAME=\$(poetry env info --path | sed -e "s/^."\\///")
        if [ "\$VENV_NAME" != "" ]; then
            poetry env remove \$VENV_NAME
        fi
        """
    }
}

def findAllureResults(String path) {
    echo "============ LOOKING FOR allure-results FOLDER INSIDE ${path} ... ==========="
    return sh(
            script: "cd ${path} && find \$PWD -type d -name 'allure-results' | head -n 1",
            returnStdout: true
    ).trim()
}

def copyAllureResults(ServiceInfo svc) {
    def actionName = "COPY ALLURE RESULTS"
    def title = "============ ${actionName} - ${svc.name} ==========="
    def command
    def svcResultsPath = findAllureResults(svc.path)
    if(svcResultsPath) {
        command = """
        mkdir -p ./allure-results
        cp -R ${svcResultsPath}/. ./allure-results/
        """
    }
    else {
        command = "echo 'allure-results folder not found for service ${svc.name}'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

def cleanAllureResults(ServiceInfo svc) {
    def actionName = "CLEAN ALLURE RESULTS"
    def title = "============ ${actionName} - ${svc.name} ==========="
    def command
    def svcResultsPath = findAllureResults(svc.path)
    if(svcResultsPath) {
        command = "rm -rf ${svcResultsPath}"
    }
    else {
        command = "echo 'allure-results folder not found for service ${svc.name}'"
    }
    return {
        echo "${title}"
        sh "${command}"
    }
}

return this