import info.ServiceInfo
import info.ClusterInfo
import info.LandscapeInfo
import info.DeployEnvironmentInfo
import modules.*
import modules.config.buildProps
import modules.config.commonBuildProps
import modules.config.serviceModel

def run_test_script(){
    def serviceModel = new serviceModel().getConfig()
    def commonBuildProps = new commonBuildProps().getConfig()
    def projectBuildProps = new buildProps().getConfig()

    def serviceInfo = new ServiceInfo()
    def clusterInfo = new ClusterInfo()
    def landscapeInfo = new LandscapeInfo()
    def deployEnvironmentInfo = new DeployEnvironmentInfo()
    def Services = serviceInfo.getServiceInfoByName('uber-bff', serviceModel)
    def build = new build()
    println Services['dockerfile']

    def deployEnvironment = deployEnvironmentInfo.getDeployEnvironmentInfoByName("dev", serviceModel)
    println deployEnvironment
    def cluster = clusterInfo.getClusterInfoByName(deployEnvironment.cluster, serviceModel)
    println cluster
    def landscape = landscapeInfo.getLandscapeInfoByName(cluster.landscape, serviceModel)
    println landscape
    def servicesToDeploy = serviceInfo.getAllServicesFromModel(serviceModel)
    println servicesToDeploy
    def servicesToBuild = serviceInfo.getAllServicesFromModel(serviceModel)
    println servicesToBuild
//    build.setBuildPropsAsEnvVars("devzone",  commonBuildProps, projectBuildProps) //Работает только в linux
}

run_test_script()

return this



//servicesToDeploy.findAll {it.type == serviceInfo.getServiceTypes().infrastructure}
//
//servicesToDeploy.findAll {it.type == serviceInfo.getServiceTypes().dbMigrations}
//
//servicesToDeploy.findAll {it.type in [
//        serviceInfo.getServiceTypes().dotnet,
//        serviceInfo.getServiceTypes().front,
//        serviceInfo.getServiceTypes().python
//]}

//@Field serviceModelConfigYaml
//
//def serviceModel = new serviceModel()
//serviceModelConfigYaml = serviceModel.getConfig() as serviceModel
//
//
//
//def PrintServiceModel(){
//  println serviceModelConfigYaml
//}

//  def serviceModelConfig = serviceModel.getConfig()

//  def commonBuildProps = new commonBuildProps()
//  def commonBuildPropsConfig = commonBuildProps.getConfig()
//
//  def projectBuildProps = new buildProps()
//  def projectBuildPropsConfig = projectBuildProps.getConfig()

//    println serviceModel
//  println serviceModelConfig
//  println commonBuildPropsConfig
//  println projectBuildPropsConfig


// Создание экземпляров классов с передачей конфигураций
//def serviceModelConfigInstance = modules.serviceModel.get  serviceModel.getConfig()
//def commonBuildPropsConfigInstance = new commonBuildProps(CommonBuildProps)
////def projectBuildPropsConfigInstance = new buildProps(ProjectBuildProps)

//try {
//  // Получение конфигураций через методы классов
//  def serviceModelConfig = serviceModelConfigInstance.getConfig()
//  def commonBuildProps = commonBuildPropsConfigInstance.getConfig()
////  def projectBuildProps = projectBuildPropsConfigInstance.getConfig()
//
//  // Использование конфигураций
//  println "serviceModel: ${serviceModelConfig}"
//  println "CommonBuildProps: ${commonBuildProps}"
////  println "ProjectBuildProps: ${projectBuildProps}"
//} catch (Exception e) {
//  println "Error: ${e.getMessage()}"
//}