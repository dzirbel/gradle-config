plugins { `kotlin-dsl` }
check(kotlin.compilerOptions.allWarningsAsErrors.get())
check(!kotlin.compilerOptions.extraWarnings.get())
check(kotlin.compilerOptions.optIn.get().isEmpty())
