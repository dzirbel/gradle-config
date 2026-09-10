import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated

class WarningProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = object : SymbolProcessor {
        override fun process(resolver: Resolver): List<KSAnnotated> {
            environment.logger.warn("Fixture processor warning")
            return emptyList()
        }
    }
}
