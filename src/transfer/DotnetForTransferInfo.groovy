package transfer

class DotnetForTransferInfo {
    String name // GM.BFF
    String srcDir // src/GM.Services
    String slnFile // GM.BFF/GM.BFF.sln
    String image // geomate-bff

    DotnetForTransferInfo getDotnetForTransferInfo(String name) {
        def service = ServiceModelConfig['services']
                .find { svc -> svc['name'] == name }

        def path = service['path']
        def serviceName = (path =~ /\/([^\/]+)$/)[0][1]

        def info = new DotnetForTransferInfo(
                name: service['name'],
                image: service['image'],
                slnFile: "${serviceName}/${serviceName}.sln",
                srcDir: (path =~ /^(.*)\/[^\/]+$/)[0][1],
        )
        return info
    }
}
