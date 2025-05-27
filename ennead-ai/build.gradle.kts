plugins {
  id("ennead-workflows")
  id("ennead-workflows-publish")
}

dependencies {
  implementation(project(":ennead-core"))

  implementation(libs.osirisCore)
  implementation(libs.osirisOpenAi)

  testImplementation(libs.kairoEnvironmentVariableSupplier)
  testImplementation(libs.kairoTesting)
}
