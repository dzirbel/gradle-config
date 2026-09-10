class Greeter(val name: String)

context(greeter: Greeter)
fun greeting(): String = "Hello ${greeter.name}"

fun greetAda(): String = greeting(greeter = Greeter("Ada"))

fun numbers(): List<Int> = [1, 2, 3]

fun destructure(): String {
    val (name) = Greeter("Ada")
    return name
}

interface Greeting {
    fun message(): String = "hello"
}

fun failAssertion() {
    assert(false) { "assertions stay enabled" }
}
