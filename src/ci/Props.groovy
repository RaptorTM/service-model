package ci
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

class CheckSwaggerInfo {
    String referencePath
    String swaggerDoc
}

CheckSwaggerInfo makeCheckSwaggerInfo(svc) {
    def options = svc['Props']['checkSwagger']
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
    def steps = svc['Props']
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




