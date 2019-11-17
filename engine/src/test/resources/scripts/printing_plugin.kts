package scripts

import io.github.antolius.engine.api.*

class PrintingPlugin : Plugin, PrinterAware {
    lateinit var p: Printer

    override fun set(printer: Printer) {
        p = printer
    }

    override fun process(req: Request): Response {
        p.print(req.data)
        return Response("OK")
    }
}

PrintingPlugin()