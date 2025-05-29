plugins {
  id("ennead-workflows")
  id("ennead-workflows-publish")
}

dependencies {
  implementation(project(":ennead-ai"))

  implementation(libs.kairoSerialization)
  implementation(libs.kairoTesting)
}
