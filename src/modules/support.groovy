package modules

import java.nio.file.Files
import java.nio.file.Path

class support {
    static boolean pathExist(servicePath) {
        Path path = servicePath
        return Files.exists(path)
    }
}


// return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!