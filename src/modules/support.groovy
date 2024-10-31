package modules

import java.nio.file.Files
import java.nio.file.Path
import src.info.ServiceInfo
import src.info.ClusterInfo
import src.info.LandscapeInfo
import src.info.DeployEnvironmentInfo
import src.modules.*
import src.modules.config.buildProps
import src.modules.config.commonBuildProps
import src.modules.config.serviceModel

static boolean pathExist(servicePath) {
    Path path = servicePath
    return Files.exists(path)
}

def load_sm_classes() {
    // Загружаем все классы проекта
    try {
        load 'src/ci/Props.groovy'
        load 'src/ci/allure.groovy'
//        load 'src/info/ServiceInfo.groovy'
        load 'src/info/ClusterInfo.groovy'
        load 'src/info/LandscapeInfo.groovy'
        load 'src/info/DeployEnvironmentInfo.groovy'
//        load 'src/modules/config/serviceModel.groovy'
        load 'src/modules/config/commonBuildProps.groovy'
        load 'src/modules/config/buildProps.groovy'
        load 'src/modules/build.groovy'
        load 'src/modules/dotnet.groovy'
        load 'src/modules/frontend.groovy'
        load 'src/modules/infrastructure.groovy'
        load 'src/modules/migrations.groovy'
        load 'src/modules/python.groovy'
        load 'src/modules/shellResult.groovy'
        echo "SUCCESS - All service-model classes are loaded"
    } catch (Exception e) {
        throw new Exception("""[31m!!!!!!!!!!!!!!!!!----------------- При загрузке groovy файлов возникла проблема -----------------!!!!!!!!!!!!!!!!!
            ${e.message}
            !!!!!!!!!!!!!!!!!---------------------------------------------------------------------------------------------------!!!!!!!!!!!!!!!!!""")
    }
}

return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!