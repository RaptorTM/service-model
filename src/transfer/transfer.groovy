package transfer
import info.*
import modules.smConfig.*

getFrontForTransferInfo("bff")

// используется в jenkinsfile-transfer-images-new
List<ServiceInfo> getServicesForTransfer() {
    return getServicesFromModel({ svc -> svc['environmentFilter'] == null ||
            (!svc['environmentFilter']['notInEnvironments'].contains('preprod') &&
                    !svc['environmentFilter']['notInEnvironments'].contains('prod'))})
}

// используется в jenkinsfile-transfer-image-any
ServiceInfo getServiceForTransferByName(String name) {
    return getServicesForTransfer().find { it.name == name }
}

FrontForTransferInfo getFrontForTransferInfo(String name) {
    def service = ServiceModelConfig['services']
            .find { svc -> svc['name'] == name }

    def info = new FrontForTransferInfo(
        name: service['name'],
        image: service['image'],
        packageJsonPath: service['path'] + "/package.json" )
    return info
}

