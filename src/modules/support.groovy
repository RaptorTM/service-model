package modules

import java.nio.file.Files
import java.nio.file.Path


static boolean pathExist(servicePath) {
    Path path = servicePath
    return Files.exists(path)
}

def load_sm_classes() {
    // Загружаем все классы проекта
    try {
        def Props = load 'src/ci/Props.groovy'
        def allure = load 'src/ci/allure.groovy'
        def ServiceInfo = load 'src/info/ServiceInfo.groovy'
        def ClusterInfo = load 'src/info/ClusterInfo.groovy'
        def LandscapeInfo = load 'src/info/LandscapeInfo.groovy'
        def DeployEnvironmentInfo = load 'src/info/DeployEnvironmentInfo.groovy'
        def serviceModel = load 'src/modules/config/serviceModel.groovy'
        def commonBuildProps = load 'src/modules/config/commonBuildProps.groovy'
        def buildProps = load 'src/modules/config/buildProps.groovy'
        def build = load 'src/modules/build.groovy'
        def dotnet = load 'src/modules/dotnet.groovy'
        def frontend = load 'src/modules/frontend.groovy'
        def infrastructure = load 'src/modules/infrastructure.groovy'
        def migrations = load 'src/modules/migrations.groovy'
        def python = load 'src/modules/python.groovy'
        def shellResult = load 'src/modules/shellResult.groovy'
        echo "SUCCESS - All service-model classes are loaded"
    } catch (Exception e) {
        throw new Exception("""[31m!!!!!!!!!!!!!!!!!----------------- При загрузке groovy файлов возникла проблема -----------------!!!!!!!!!!!!!!!!!
            ${e.message}
            !!!!!!!!!!!!!!!!!---------------------------------------------------------------------------------------------------!!!!!!!!!!!!!!!!!""")
    }
}

return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!