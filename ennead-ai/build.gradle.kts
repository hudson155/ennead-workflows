plugins {
  id("ennead-workflows")
  id("ennead-workflows-publish")
}

dependencies {
  implementation(project(":ennead-core")) // Peer dependency.

  implementation(libs.kairoReflect)
  implementation(libs.kairoSerialization)
  api(libs.osirisCore)

  testImplementation(project(":ennead-ai:testing"))

  testImplementation(libs.kairoEnvironmentVariableSupplier)
  testImplementation(libs.kairoTesting)
  testImplementation(libs.osirisOpenAi)
}
