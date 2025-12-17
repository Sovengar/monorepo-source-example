#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper startup script for UNIX platforms
# ----------------------------------------------------------------------------

set -euf
[ "${MVNW_VERBOSE-}" != true ] || set -x

# OS specific support.
native_path() { printf %s\\n "$1"; }
case "$(uname)" in
  CYGWIN* | MINGW*)  native_path() { cygpath --path --windows "$1"; } ;;
esac

# Make sure JAVA_HOME is set
if [ -z "${JAVA_HOME-}" ]; then
  JAVA_HOME="$(java -XshowSettings:properties -version 2>&1 | grep 'java.home' | awk '{print $3}')"
fi
JAVA_EXE="$JAVA_HOME/bin/java"
if [ ! -x "$JAVA_EXE" ]; then
  JAVA_EXE=java
fi

# Find project base directory
find_maven_basedir() {
  cd "${0%/*}/" >/dev/null 2>&1 || exit 1
  while [ ! -f pom.xml ] && [ "$PWD" != "/" ]; do
    cd ..
  done
  printf %s "$PWD"
}

MAVEN_BASEDIR="$(find_maven_basedir)"
MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR}"
export MAVEN_PROJECTBASEDIR

# Download and run wrapper jar
WRAPPER_JAR="${MAVEN_BASEDIR}/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROPERTIES="${MAVEN_BASEDIR}/.mvn/wrapper/maven-wrapper.properties"

if [ -f "$WRAPPER_PROPERTIES" ]; then
  WRAPPER_URL=$(grep 'wrapperUrl' "$WRAPPER_PROPERTIES" | cut -d'=' -f2-)
  DIST_URL=$(grep 'distributionUrl' "$WRAPPER_PROPERTIES" | cut -d'=' -f2-)
fi

# Download wrapper if not present
if [ ! -f "$WRAPPER_JAR" ]; then
  mkdir -p "$(dirname "$WRAPPER_JAR")"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL -o "$WRAPPER_JAR" "$WRAPPER_URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$WRAPPER_JAR" "$WRAPPER_URL"
  fi
fi

# Run maven
exec "$JAVA_EXE" \
  ${MAVEN_OPTS-} \
  ${MAVEN_DEBUG_OPTS-} \
  -classpath "$WRAPPER_JAR" \
  org.apache.maven.wrapper.MavenWrapperMain \
  ${MAVEN_CONFIG-} \
  "$@"
