scalaVersion := "2.13.8"

Compile / PB.targets := Seq(
    scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

libraryDependencies ++= Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
)
