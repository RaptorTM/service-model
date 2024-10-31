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

    def servicesToBuild = serviceInfo.getServicesForBuild(serviceModel)
    for (s in servicesToBuild) {
        println(s['name'])
        println(s['dockerfile'])
    }
    def servicesToDeploy = serviceInfo.getAllServicesFromModel(serviceModel)
    for (s in servicesToDeploy) {
        println(s['image'])
        println(s['type'])
    }
//    build.setBuildPropsAsEnvVars("devzone",  commonBuildProps, projectBuildProps) //Работает только в linux
}

//run_test_script()

return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!