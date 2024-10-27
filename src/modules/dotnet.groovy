package modules

class dotnet {
    def dotnetRestore(svc){
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

    def dotnetBuild(svc) {
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

    def dotnetTest(svc) {
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

    def dotnetCheckSwagger(svc) {
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

    def dotnetClean(svc) {
        def actionName = "CLEAN"
        return {
            echo "============ dotnet ${actionName} - ${svc.name} ==========="
            sh """ dotnet clean ${svc.path} """
            sh """ rm -rf ${svc.path}/swagger """
        }
    }
}
return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!