#!/bin/bash
if [ $# != 2 ]; then
  echo "You need to pass the Github username and access tokens as parameters."
  exit 1
fi

m2="<settings>
  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
        </repository>
        <repository>
          <id>github</id>
          <url>https://maven.pkg.github.com/clockify/addon-java-sdk</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

    <servers>
      <server>
        <id>github</id>
        <username>$1</username>
        <password>$2</password>
      </server>
    </servers>
</settings>"

mkdir /root/.m2
echo "$m2" > /root/.m2/settings.xml