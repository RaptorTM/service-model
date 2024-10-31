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
        Script ServiceInfo = load 'src/ci/Props.groovy'
        Script allure                 = load 'src/ci/allure.groovy'
        Script ServiceInfo                = load 'src/info/ServiceInfo.groovy'
        Script ClusterInfo                = load 'src/info/ClusterInfo.groovy'
        Script LandscapeInfo                = load 'src/info/LandscapeInfo.groovy'
        Script DeployEnvironmentInfo                = load 'src/info/DeployEnvironmentInfo.groovy'
        Script serviceModel                = load 'src/modules/config/serviceModel.groovy'
        Script commonBuildProps                = load 'src/modules/config/commonBuildProps.groovy'
        Script buildProps                = load 'src/modules/config/buildProps.groovy'
        Script build               = load 'src/modules/build.groovy'
        Script dotnet                 = load 'src/modules/dotnet.groovy'
        Script frontend                 = load 'src/modules/frontend.groovy'
        Script infrastructure                 = load 'src/modules/infrastructure.groovy'
        Script  migrations                = load 'src/modules/migrations.groovy'
        Script python                 = load 'src/modules/python.groovy'
        Script shellResult                 = load 'src/modules/shellResult.groovy'
        echo "SUCCESS - All service-model classes are loaded"
    } catch (Exception e) {
        throw new Exception("""[31m!!!!!!!!!!!!!!!!!----------------- При загрузке groovy файлов возникла проблема -----------------!!!!!!!!!!!!!!!!!
            ${e.message}
            !!!!!!!!!!!!!!!!!---------------------------------------------------------------------------------------------------!!!!!!!!!!!!!!!!!""")
    }
}

return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!