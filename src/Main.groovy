import groovy.transform.Field
import info.ServiceInfo
import modules.smConfig.*




def serviceModel = new ServiceModelConfig().getServiceModelConfig()
def serviceInfo = new ServiceInfo()
def Services = serviceInfo.getServiceInfoByName('uber-bff', serviceModel)
println Services['dockerfile']














//@Field serviceModelConfigYaml
//
//def ServiceModelConfig = new ServiceModelConfig()
//serviceModelConfigYaml = ServiceModelConfig.getServiceModelConfig() as ServiceModelConfig
//
//
//
//def PrintServiceModel(){
//  println serviceModelConfigYaml
//}



//




//  def serviceModelConfig = serviceModel.getServiceModelConfig()

//  def commonBuildProps = new CommonBuildPropsConfig()
//  def commonBuildPropsConfig = commonBuildProps.getCommonBuildProps()
//
//  def projectBuildProps = new ProjectBuildPropsConfig()
//  def projectBuildPropsConfig = projectBuildProps.getProjectBuildProps()

//    println serviceModel
//  println serviceModelConfig
//  println commonBuildPropsConfig
//  println projectBuildPropsConfig














// Создание экземпляров классов с передачей конфигураций
//def serviceModelConfigInstance = modules.ServiceModelConfig.get  ServiceModelConfig.getServiceModelConfig()
//def commonBuildPropsConfigInstance = new CommonBuildPropsConfig(CommonBuildProps)
////def projectBuildPropsConfigInstance = new ProjectBuildPropsConfig(ProjectBuildProps)



//try {
//  // Получение конфигураций через методы классов
//  def serviceModelConfig = serviceModelConfigInstance.getServiceModelConfig()
//  def commonBuildProps = commonBuildPropsConfigInstance.getCommonBuildProps()
////  def projectBuildProps = projectBuildPropsConfigInstance.getProjectBuildProps()
//
//  // Использование конфигураций
//  println "ServiceModelConfig: ${serviceModelConfig}"
//  println "CommonBuildProps: ${commonBuildProps}"
////  println "ProjectBuildProps: ${projectBuildProps}"
//} catch (Exception e) {
//  println "Error: ${e.getMessage()}"
//}