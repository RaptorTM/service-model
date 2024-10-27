package ci

import info.ServiceInfo

class allure {
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
}
return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!