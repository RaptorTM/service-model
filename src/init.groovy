import info.*
//import info.ServiceInfo
import info.ClusterInfo
import info.LandscapeInfo
import info.DeployEnvironmentInfo
import modules.*
import modules.config.*
import modules.config.buildProps
import modules.config.commonBuildProps
//import modules.config.serviceModel

class init {
    def ClusterInfo = new ClusterInfo()
    def a = ClusterInfo.getClusterInfoByName("ddd")
    void get(){
        print("____________________ import clasess ____________________________")
    }
}
return this