package modules

import info.ServiceInfo

class frontend {
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
}
return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!