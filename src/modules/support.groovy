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
        def _Props = load 'src/ci/Props.groovy'
        def _allure = load 'src/ci/allure.groovy'
        def _ServiceInfo = load 'src/info/ServiceInfo.groovy'
        def _ClusterInfo = load 'src/info/ClusterInfo.groovy'
        def _LandscapeInfo = load 'src/info/LandscapeInfo.groovy'
        def _DeployEnvironmentInfo = load 'src/info/DeployEnvironmentInfo.groovy'
        def _serviceModel = load 'src/modules/config/serviceModel.groovy'
        def _commonBuildProps = load 'src/modules/config/commonBuildProps.groovy'
        def _buildProps = load 'src/modules/config/buildProps.groovy'
        def _build = load 'src/modules/build.groovy'
        def _dotnet = load 'src/modules/dotnet.groovy'
        def _frontend = load 'src/modules/frontend.groovy'
        def _infrastructure = load 'src/modules/infrastructure.groovy'
        def _migrations = load 'src/modules/migrations.groovy'
        def _python = load 'src/modules/python.groovy'
        def _shellResult = load 'src/modules/shellResult.groovy'
        echo "SUCCESS - All service-model classes are loaded"
    } catch (Exception e) {
        throw new Exception("""[31m!!!!!!!!!!!!!!!!!----------------- При загрузке groovy файлов возникла проблема -----------------!!!!!!!!!!!!!!!!!
            ${e.message}
            !!!!!!!!!!!!!!!!!---------------------------------------------------------------------------------------------------!!!!!!!!!!!!!!!!!""")
    }
}

return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!