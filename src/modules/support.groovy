package modules

import java.nio.file.Files
import java.nio.file.Path


static boolean pathExist(servicePath) {
    Path path = servicePath
    return Files.exists(path)
}

def load_sm_classes() {
    // Загружаем все необходимые файлы
    load 'src/ci/Props.groovy'
    load 'src/info/ServiceInfo.groovy'
    load 'src/info/ClusterInfo.groovy'
    load 'src/info/LandscapeInfo.groovy'
    load 'src/info/DeployEnvironmentInfo.groovy'
    load 'src/modules/config/serviceModel.groovy'
    load 'src/modules/config/commonBuildProps.groovy'
    load 'src/modules/config/buildProps.groovy'
    load 'src/modules/build.groovy'
    println("SUCCESS - All service-model classes is load")
}

return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!