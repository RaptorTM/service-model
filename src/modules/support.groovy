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
        load 'src/ci/Props.groovy'
        load 'src/ci/allure.groovy'
        load 'src/info/ServiceInfo.groovy'
        load 'src/info/ClusterInfo.groovy'
        load 'src/info/LandscapeInfo.groovy'
        load 'src/info/DeployEnvironmentInfo.groovy1'
        load 'src/modules/config/serviceModel.groovy1'
        load 'src/modules/config/commonBuildProps.groovy'
        load 'src/modules/config/buildProps.groovy'
        load 'src/modules/build.groovy'
        load 'src/modules/dotnet.groovy'
        load 'src/modules/frontend.groovy'
        load 'src/modules/infrastructure.groovy'
        load 'src/modules/migrations.groovy'
        load 'src/modules/python.groovy'
        load 'src/modules/shellResult.groovy'
        println("SUCCESS - All service-model classes are loaded")
    } catch (Exception e) {
        throw new Exception("""!!!!!!!!!!!!!!!!!----------------- При загрузке groovy файлов возникла проблема-----------------!!!!!!!!!!!!!!!!!
                Ошибка: ${e.message} \\n
                !!!!!!!!!!!!!!!!!----------------------------------!!!!!!!!!!!!!!!!!""")
    }
}

return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!