package modules

class python {
    def pythonPoetryInstall(svc) {
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

    def pythonPoetryBuild(svc) {
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

    def pythonPoetryPytest(svc) {
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

    def pythonPoetryPylint(svc) {
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

    def pythonPoetryMypy(svc) {
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

    def pythonPoetryBlack(svc) {
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

    def pythonPoetryIsort(svc) {
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

    def pythonPoetryClean(svc) {
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
}
return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!