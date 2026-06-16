
plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

tasks.named("sourcesJar") {
    dependsOn("preprocessLangInJavaFiles")
}
