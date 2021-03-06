package scripts

import io.github.antolius.engine.api.Plugin
import io.github.antolius.engine.api.Request
import io.github.antolius.engine.api.Response

class EchoPlugin : Plugin {
    override fun process(req: Request) = Response(req.data)
}