//package modules
//
//import java.nio.file.Path
//import java.nio.file.Files
//import groovy.yaml.YamlSlurper
//import groovy.transform.Field
//
//// константы объявляем без def, чтобы были доступны из функций
////serviceModel = readYaml(file: './ci/devzone/services-model.yaml')
////CommonBuildProps = readYaml(file: './ci/devzone-tools/jenkins/common/common-build-props.yaml')
////ProjectBuildProps = readYaml(file: './ci/devzone/build-props.yaml')
//
////@Field serviceModel = new YamlSlurper().parse(new File('C:\\Users\\Руслан\\IdeaProjects\\test\\src\\ci\\devzone\\services-model.yaml'))
////@Field CommonBuildProps = new YamlSlurper().parse(new File('C:\\Users\\Руслан\\IdeaProjects\\test\\src\\ci\\devzone\\build-props.yaml'))
////ProjectBuildProps = new YamlSlurper().parse(new File('./ci/devzone/build-props.yaml'))
//
//// Чтение конфигурации
////def serviceModel = readYaml(file: './ci/services-model.yaml')
////def CommonBuildProps = readYaml(file: './ci/devzone-tools/jenkins/common/common-build-props.yaml')
////def ProjectBuildProps = readYaml(file: './ci/build-props.yaml')
//
////@Field serviceModel = new YamlSlurper().parse(new File('/Users/ruslan/IdeaProjects/service-model/devzone/services-model.yaml'))
//@Field CommonBuildProps = new YamlSlurper().parse(new File('/Users/ruslan/IdeaProjects/service-model/devzone/build-props.yaml'))
////def ProjectBuildProps = new YamlSlurper().parse(new File('./ci/build-props.yaml'))
//
//class serviceModel {
//    def serviceModelConfig
//
//    def getConfig() {
//        if (this.serviceModelConfig == null) {
//            serviceModelConfig = new YamlSlurper().parse(new File('/Users/ruslan/IdeaProjects/service-model/devzone/services-model.yaml'))
//        }
////        else {
////            throw new Exception("serviceModel file not found or is empty.")
////        }
//        return this.serviceModelConfig
//    }
//}
//
//class commonBuildProps {
//    def commonBuildProps
//
//    commonBuildProps(commonBuildProps) {
//        this.commonBuildProps = commonBuildProps
//    }
//
//    def getConfig() {
//        if (this.commonBuildProps == null) {
//            throw new Exception("CommonBuildProps file not found or is empty.")
//        }
//        return this.commonBuildProps
//    }
//}
//
//class buildProps {
//    def projectBuildProps
//
//    buildProps(projectBuildProps) {
//        this.projectBuildProps = projectBuildProps
//    }
//
//    def getConfig() {
//        if (this.projectBuildProps == null) {
//            throw new Exception("ProjectBuildProps file not found or is empty.")
//        }
//        return this.projectBuildProps
//    }
//}
//
