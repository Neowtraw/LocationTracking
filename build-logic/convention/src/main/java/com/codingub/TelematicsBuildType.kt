
package com.codingub

/**
 * This is shared between :app and :benchmarks module to provide configurations type safety.
 */
enum class TelematicsBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
    BENCHMARK(".benchmark")
}
