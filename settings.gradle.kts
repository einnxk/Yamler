rootProject.name = "Yamler"

include(":core")
include(":paper")

project(":core").projectDir = file("core")
project(":paper").projectDir = file("paper")
project(":core").name = "Yamler-core"
project(":paper").name = "Yamler-Paper"