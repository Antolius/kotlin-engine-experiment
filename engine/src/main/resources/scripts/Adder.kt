package scripts

import io.github.antolius.engine.api.*

class Adder : Plugin, PrinterAware {
    lateinit var p: Printer

    override fun set(printer: Printer) {
        p = printer
    }

    override fun process(req: Request): Response {
        val expression = req.data
        p.print("Evaluating expression `${expression}`")
        try {
            val sum = expression.split("+")
                .map { it.trim() }
                .map { it.toInt() }
                .fold(0) { sum, a -> sum + a}
            return Response("${sum}")
        } catch(e: Exception) {
            p.print("Error: ${e}")
        } 
        return Response("NaN")
    }
}
 