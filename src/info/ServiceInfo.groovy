package src.info

import src.ci.CiProps

import java.nio.file.*

class ServiceInfo {
    String buildJob
    String dockerfile
    String image
    String name
    String kuberObjectType // Тип разворачиваемого в openshift объекта, pod или job
    List notInEnvironments
    String migrateArgs
    String type
    String buildContext
    String buildFirst
    CiProps ciProps
    String path


    Map<String,String> serviceTypes = [
            dbMigrations: "db-migrations",
            dotnet: "dotnet",
            front: "front",
            python: "python",
            infrastructure: "infrastructure"
    ]

    static Map<String,String> getServiceTypes() {
        return [
                dbMigrations: "db-migrations",
                dotnet: "dotnet",
                front: "front",
                python: "python",
                infrastructure: "infrastructure",
        ]
    }

    ServiceInfo makeServiceInfo(svc) {
        return new ServiceInfo(
                buildJob: svc['buildJob'] ? svc['buildJob'] : './build/build-common',
                dockerfile: svc['dockerfile'],
                image: svc['image'],
                name: svc['name'],
                kuberObjectType: svc['type'] == serviceTypes.dbMigrations ? 'job' : 'pod',
                notInEnvironments: svc['environmentFilter'] ? svc['environmentFilter'].notInEnvironments : [],
                migrateArgs: svc['type'] == serviceTypes.dbMigrations ? svc['migrateArgs'] : '',
                type: svc['type'],
                buildContext: svc['buildContext'] ? svc['buildContext'] : '.',
                buildFirst: svc['buildFirst'] ? svc['buildFirst'] : false,
//                ciProps: makeCiProps(svc),
                path: svc['path'] ? svc['path'] : '',
        )
    }


    List<ServiceInfo> getServicesFromModel(Closure predicate, serviceModelConfigYaml) {
        def infos = serviceModelConfigYaml['services']
                .findAll { svc -> predicate(svc) }
                .collect { svc -> makeServiceInfo(svc) }
        return infos
    }

    List<ServiceInfo> getAllServicesFromModel(serviceModelConfigYaml) {
        def infos = serviceModelConfigYaml['services']
                .collect { svc -> makeServiceInfo(svc) }
        return infos
    }

    List<ServiceInfo> getServicesForBuild(serviceModelConfigYaml) {
        return getServicesFromModel({ svc -> svc['dockerfile'] != null },serviceModelConfigYaml)
    }

    List<ServiceInfo> getServicesWithBuildJobs(serviceModelConfigYaml) {
        return getServicesFromModel({ svc -> svc['buildJob'] != null },serviceModelConfigYaml)
    }

    List<ServiceInfo> getServicesByTypeForScanners(String type, serviceModelConfigYaml) {
        return getServicesFromModel({ svc -> svc['type'] == type },serviceModelConfigYaml)
    }

    List<ServiceInfo> getServicesForCi(serviceModelConfigYaml) {
        return getServicesFromModel({ svc -> svc['Props.groovy'] != null },serviceModelConfigYaml)
    }


    ServiceInfo getServiceInfoByName(String name, serviceModelConfigYaml) {
        def svc = serviceModelConfigYaml['services'].find { svc -> svc['name'] == name }
        return makeServiceInfo(svc)
    }

    // если не нашла, возвращает null
    static String getServiceTypeByName(String name, serviceModelConfigYaml) {
        def service = serviceModelConfigYaml['services']
                .find { svc -> svc['name'] == name }
        return service['type']
    }

    String getDefaultBuildArgs(String serviceType, String segmentId, String appVersion) {
        def landscape = getLandscapeInfoBySegment(segmentId)
        def buildProps = getBuildProps(segmentId)
        def commonArgs = "--build-arg DOCKER_REGISTRY=${landscape.mainRegistry} " +
                "--build-arg QUAY_DOCKER_REGISTRY=${buildProps.QUAY_DOCKER_REGISTRY} "
        def serviceArgs
        switch (serviceType) {
            case serviceTypes.dbMigrations:
                serviceArgs = "--build-arg MIGRATE_BASE_IMAGE=${buildProps.MIGRATE_BASE_IMAGE} "
                break
            case serviceTypes.dotnet:
                serviceArgs = "--build-arg DOTNET_SDK_BASE_IMAGE=${buildProps.DOTNET_SDK_BASE_IMAGE} " +
                        "--build-arg DOTNET_RUNTIME_BASE_IMAGE=${buildProps.DOTNET_RUNTIME_BASE_IMAGE} " +
                        "--build-arg NUGET_V3_EXTERNAL=${buildProps.NUGET_V3_EXTERNAL} " +
                        "--build-arg APP_VERSION=${appVersion} "
                break
            case serviceTypes.front:
                serviceArgs = "--build-arg NODEJS_BASE_IMAGE=${buildProps.NODEJS_BASE_IMAGE} " +
                        "--build-arg NGINX_BASE_IMAGE=${buildProps.NGINX_BASE_IMAGE} " +
                        "--build-arg NPM_REGISTRY=${buildProps.NPM_REGISTRY} " +
                        "--build-arg APP_VERSION=${appVersion} "
                break
            case serviceTypes.python:
                serviceArgs = "--build-arg POETRY_VERSION=${buildProps.POETRY_VERSION} " +
                        "--build-arg PYTHON_BASE_IMAGE=${buildProps.PYTHON_BASE_IMAGE} " +
                        "--build-arg PIP_INDEX_URL=${buildProps.PIP_INDEX_URL} " +
                        "--build-arg PIP_TRUSTED_HOST=${buildProps.PIP_TRUSTED_HOST} " +
                        "--build-arg APP_VERSION=${appVersion} "
                break
            case serviceTypes.java:
                serviceArgs = "--build-arg OPENJDK_BASE_IMAGE=${buildProps.OPENJDK_BASE_IMAGE} " +
                        "--build-arg GRADLE_BASE_IMAGE=${buildProps.GRADLE_BASE_IMAGE} " +
                        "--build-arg APP_VERSION=${appVersion} "
                break
            case serviceTypes.clang:
                serviceArgs = "--build-arg APP_VERSION=${appVersion} "
                break
            default:
                serviceArgs = ""
                break
        }
        return commonArgs + serviceArgs
    }
}

return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!